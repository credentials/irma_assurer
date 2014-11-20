package org.irmacard.identity.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

// TODO: Skip this decoder altogether and just use PassportDataDecoder?
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Wait until the length prefix is available.
        if (in.readableBytes() < CONSTANTS.DATA_LENGTH_OFFSET) {
            System.out.println("Insufficient bytes to start decoding. Waiting for more data...");
            return;
        }

        in.markReaderIndex();

        System.out.printf("%d bytes available for reading.\n", in.readableBytes());

        // Check the magic number.
        System.out.println("Checking the magic number.");
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != CONSTANTS.AUTH_REQUEST) {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        // Wait until the whole data is available.
        int dataLength = in.readInt();
        System.out.printf("Length of the data is: %d.\n", dataLength);
        if (in.readableBytes() < dataLength) {
            System.out.printf("Insufficient bytes to read, waiting until we have at least %d.\n", dataLength);
            in.resetReaderIndex();
            return;
        }

        System.out.printf("Starting decoding process.\n");

        // Store the received data in a byte array.
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);

        System.out.printf("%d bytes have been read. Result: %s.\n", dataLength, new String(decoded));

        out.add(new String(decoded));
    }
}
