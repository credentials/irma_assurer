package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.sourceforge.scuba.util.Hex;
import org.jmrtd.Passport;
import org.jmrtd.lds.LDS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PassportDataEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof Passport) {
            Passport passport = (Passport) msg;

            System.out.printf("Creating streams.\n");
            InputStream input;
            ByteBuf buffer = Unpooled.buffer();
            OutputStream output = new ByteBufOutputStream(buffer);
            ZipOutputStream zipOut = new ZipOutputStream(output);

            LDS source = passport.getLDS();

            for (Short fileID : source.getFileList()) {
                try {
                    input = source.getInputStream(fileID);
                    String entryName = Hex.shortToHexString(fileID) + ".bin";
                    zipOut.putNextEntry(new ZipEntry(entryName));

                    System.out.printf("Converting %s.\n", entryName);

                    int bytesRead = 0;
                    byte[] dgBytes = new byte[CONSTANTS.CHUNK_SIZE];

                    System.out.printf("Starting read from datagroup %s.\n", fileID.byteValue());

                    while((bytesRead = input.read(dgBytes)) > 0){
                        System.out.printf("Read %d bytes from the input stream.\n", bytesRead);
                        zipOut.write(dgBytes, 0, bytesRead);
                        System.out.printf("Bytes written to output stream.\n");
                    }

                    System.out.printf("Closing zip entry and proceeding with next datagroup.\n");

                    zipOut.closeEntry();

                } catch (IOException ioe) {
                    System.err.printf("Skipping DataGroup %s. %s.\n", fileID.byteValue(), ioe.getMessage());
                }
            }

            zipOut.finish();
            zipOut.close();
            output.flush();
            output.close();
            System.out.printf("Buffer can contain %d bytes.\n", buffer.capacity());

            out.writeBytes(buffer);
        } else {
            System.err.printf("This message is not a passport. Skipping.\n");
        }
    }
}
