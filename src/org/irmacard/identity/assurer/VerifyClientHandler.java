package org.irmacard.identity.assurer;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class VerifyClientHandler extends SimpleChannelInboundHandler<String> {
    ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel has become active.");
        this.ctx = ctx;

        String command = "auth";
        sendMessage(command);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Incoming data: " + msg);
    }

    protected void sendMessage(String command) {
        ChannelFuture future;

        System.out.printf("Writing command '%s' to the channel.\n", command);
        future = ctx.writeAndFlush(command);

        System.out.println("Waiting for the operation to complete.");
        assert future != null;
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Operation complete.");
                if (future.isSuccess()) {
                    System.out.println("Message sent successfully.");
                } else {
                    future.cause().printStackTrace();
                    future.channel().close();
                }
            }
        });
    }
}
