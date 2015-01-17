package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jmrtd.MRTDTrustStore;
import org.jmrtd.Passport;

import java.io.*;
import java.util.List;
import java.util.zip.ZipException;

public class PassportDataDecoder extends ByteToMessageDecoder {
    MRTDTrustStore trustStore = new MRTDTrustStore();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Determine buffer size and allocate resources
        System.out.printf("Found %d readable bytes in the buffer.\n", in.readableBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream(CONSTANTS.CHUNK_SIZE);

        try {
            in.readBytes(output, CONSTANTS.CHUNK_SIZE);

            File file = new File("passport.zip");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            fileOutputStream.write(output.toByteArray());
            fileOutputStream.close();

            // TODO: How do we know if we have all the bytes before attempting to create a passport object?
            Passport p = new Passport(file, trustStore);

            // Send acknowledgement
            out.add(CONSTANTS.ACK);
        } catch (IndexOutOfBoundsException oob) {
            System.err.printf("Index out of bounds. %s.\n", oob.getMessage());
            System.exit(1235);
        } catch (ZipException ze) {
            System.err.printf("Zip Exception: %s.\n", ze.getMessage());
        }
    }
}
