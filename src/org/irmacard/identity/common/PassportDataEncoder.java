package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.sourceforge.scuba.util.Hex;
import org.jmrtd.Passport;
import org.jmrtd.lds.LDS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PassportDataEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof Passport) {
            Passport passport = (Passport) msg;
            LDS source = passport.getLDS();

            for (Short fileID : source.getFileList()) {
                try {
                    System.out.printf("Creating streams.\n");
                    InputStream input = source.getInputStream(fileID);
                    OutputStream output = new ByteBufOutputStream(out);
                    String entryName = Hex.shortToHexString(fileID) + ".bin";
                    System.out.printf("Converting %s.\n", entryName);
                    int bytesRead = 0;
                    byte[] dgBytes = new byte[1024];
                    System.out.printf("Starting read from datagroup %s.\n", fileID.byteValue());
                    while((bytesRead = input.read(dgBytes)) > 0){
                        System.out.printf("Read %d bytes from the input stream.\n", bytesRead);
                        output.write(dgBytes);
                        System.out.printf("Bytes written to output stream.\n");
                    }
                } catch (IOException ioe) {
                    System.err.printf("Skipping DataGroup %s. %s.\n", fileID.byteValue(), ioe.getMessage());
                }
            }
        } else {
            System.err.printf("This message is not a passport. Skipping.\n");
        }
    }
}
