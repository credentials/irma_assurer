package org.irmacard.identity.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

// TODO: Skip this encoder altogether and just use PassportDataEncoder?
public class MessageEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        System.out.printf("Encoding '%s' into a byte array.\n", msg);
        byte[] encoded = msg.getBytes(Charset.forName("UTF-8"));
        int len = encoded.length;

        System.out.printf("Encoding result: %s.\n", new String(encoded));
        System.out.printf("Length of the byte array: %d.\n", len);

        //Write a message.
        out.writeByte(CONSTANTS.AUTH_REQUEST);      // magic number
        out.writeInt(len);              // data length
        out.writeBytes(encoded);        // data
    }
}
