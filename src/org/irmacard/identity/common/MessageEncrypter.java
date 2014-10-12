package org.irmacard.identity.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

// TODO: Check if String is the correct format
public class MessageEncrypter extends MessageToMessageEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        String encrypted = "";

        // encrypt here, following the scheme of the design

        out.add(encrypted);
    }
}
