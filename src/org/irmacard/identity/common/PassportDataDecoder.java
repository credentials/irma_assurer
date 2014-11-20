package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PassportDataDecoder extends ByteToMessageDecoder {
    byte[] incoming;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Determine buffer size and allocate resources
        int size = in.readInt();
        incoming = new byte[size];

        // Store data
        try {
            in.readBytes(incoming);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Send acknowledgement
        out.add(CONSTANTS.ACK);
    }
}
