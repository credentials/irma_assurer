package org.irmacard.identity.assurer;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sourceforge.scuba.smartcards.CardService;
import net.sourceforge.scuba.smartcards.CardServiceException;
import org.jmrtd.Passport;
import org.jmrtd.PassportService;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

public class VerifyClientHandler extends SimpleChannelInboundHandler<String> {
    ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel has become active.");
        this.ctx = ctx;

        String documentNumber = "NSK8DCPJ4";
        String dateOfBirth = "880810";
        String dateOfExpiry = "180321";

        sendPassportData(documentNumber, dateOfBirth, dateOfExpiry);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Incoming data: " + msg);
    }

    protected void sendPassportData(String documentNumber, String dateOfBirth, String dateOfExpiry) {
        CardTerminals terminalList = TerminalFactory.getDefault().terminals();

        try {
            CardService cs = CardService.getInstance(terminalList.list().get(0));
            PassportReader passportReader = new PassportReader(new PassportService(cs));

            // FIXME: Unless verifyIntegrity() and in turn verifyLocally() is called first, passport will be null
            passportReader.verifyIntegrity(documentNumber, dateOfBirth, dateOfExpiry);
            Passport passport = passportReader.getPassport();

            ChannelFuture future;

            System.out.printf("Sending passport data.\n");
            future = ctx.writeAndFlush(passport);

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
        } catch (CardException e) {
            e.printStackTrace();
        } catch (CardServiceException e) {
            e.printStackTrace();
        }
    }
}
