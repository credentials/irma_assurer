package org.irmacard.identity.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

// TODO: Check if string is the correct format
public class MessageDecrypter extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        String decrypted = "";

        // Decrypt here, following the scheme as described in the design



        out.add(decrypted);
    }
}
