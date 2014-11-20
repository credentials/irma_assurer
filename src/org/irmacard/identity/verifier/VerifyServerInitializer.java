package org.irmacard.identity.verifier;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;
import org.irmacard.identity.common.*;


public class VerifyServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public VerifyServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        // pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        // pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // Convert Strings to Bytes and vice versa
        // pipeline.addLast(new MessageDecoder());
        // pipeline.addLast(new MessageEncoder());

        // Add the passport codecs
        pipeline.addLast(new PassportDataDecoder());
        // pipeline.addLast(new PassportDataEncoder());

        // Add cryptographic codecs
        // pipeline.addLast(new MessageDecrypter());
        // pipeline.addLast(new MessageEncrypter());

        // TODO: Add some more codecs if necessary.

        // and then business logic.
        // Please note we create a handler for every new channel
        // because it has stateful properties.
        pipeline.addLast(new VerifyServerHandler());
    }
}
