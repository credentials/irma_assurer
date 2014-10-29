package org.irmacard.identity.assurer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;
import org.irmacard.identity.common.*;

public class VerifyClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public VerifyClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), VerifyClient.HOST, VerifyClient.PORT));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        // pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        // pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());

        // Add the passport codecs
        // pipeline.addLast(new PassportDataDecoder());
        // pipeline.addLast(new PassportDataEncoder());

        // Add cryptographic codecs
        // pipeline.addLast(new MessageDecrypter());
        // pipeline.addLast(new MessageEncrypter());

        // TODO: Add some more codecs if necessary.

        // and then business logic.
        pipeline.addLast(new VerifyClientHandler());
    }
}
