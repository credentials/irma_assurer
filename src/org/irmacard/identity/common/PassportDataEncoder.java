package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jmrtd.Passport;
import org.jmrtd.lds.LDS;
import org.jmrtd.lds.LDSFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassportDataEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof Passport) {
            Passport passport = (Passport) msg;
            LDS source = passport.getLDS();
            int numberOfBytes = 0;

            List<LDSFile> fileList = new ArrayList<LDSFile>();

            for (Short fileID : source.getFileList()) {
                try {
                    fileList.add(source.getFile(fileID));
                } catch (IOException ioe) {
                    System.err.printf("Skipping DataGroup %s. %s\n", fileID.byteValue(), ioe.getMessage());
                }

            }

            for (LDSFile f : fileList) {
                numberOfBytes += f.getLength();
            }

            // dest should hold the entire LDS, plus an integer decribing size, plus a one-byte magic number 'P'
            byte[] dest = new byte[numberOfBytes + 19]; // FIXME: Not sure why 19 extra bytes are needed
            int writeOffset = 0;

            try {
                for (LDSFile f : fileList) {
                    // tag (int) + value (byte[]) = encoded
                    byte[] bytes = f.getEncoded();
                    int size = bytes.length;
                    System.arraycopy(bytes, 0, dest, writeOffset, size);
                    writeOffset += size;
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.printf("Out of bounds. There was too much data to fit in the buffer.\n");
            }

            // Write the encoded passport to the output stream
            out.writeByte('P');
            out.writeInt(numberOfBytes);
            out.writeBytes(dest);
        } else {
            System.err.printf("This message is not a passport. Skipping.\n");
        }
    }
}
