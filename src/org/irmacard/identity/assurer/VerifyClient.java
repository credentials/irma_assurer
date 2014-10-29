package org.irmacard.identity.assurer;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * The verification client sets up a connection with the verification server and assigns handlers
 */
public class VerifyClient {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            // TODO: Change to secure TrustManager
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new VerifyClientInitializer(sslCtx));

            // Make a new connection.
            ChannelFuture f = b.connect(HOST, PORT).sync();



            // Get the handler instance to retrieve the answer.
            VerifyClientHandler handler = (VerifyClientHandler) f.channel().pipeline().last();

            // Print out the answer.
            // System.err.format("Factorial of %,d is: %,d", COUNT, handler.getFactorial());

            // handler.authenticate();
        } finally {
            group.shutdownGracefully();
        }
    }
}
