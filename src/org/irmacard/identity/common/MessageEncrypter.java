package org.irmacard.identity.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.bouncycastle.jcajce.provider.symmetric.DES;
import org.bouncycastle.jcajce.provider.symmetric.DESede;

import java.security.Key;
import java.util.List;

// TODO: Check if String is the correct format
public class MessageEncrypter extends MessageToMessageEncoder<String> {
    Key kTS;

    protected void generateSessionKey() {
        // TODO: Method stub
        System.out.println("Starting session key generation...");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        String encrypted = "";

        encrypted = msg;

        // encrypt here, following the scheme of the design

        out.add(encrypted);
    }
}
