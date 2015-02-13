package org.irmacard.identity.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jmrtd.MRTDTrustStore;
import org.jmrtd.Passport;
import org.jmrtd.lds.LDS;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class PassportDataDecoder extends ByteToMessageDecoder {
    MRTDTrustStore trustStore = new MRTDTrustStore();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.printf("Found %d readable bytes in the buffer.\n", in.readableBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream(CONSTANTS.CHUNK_SIZE);


        try {
            while (in.readableBytes() > 0) {
                System.out.printf("Reading %d bytes from buffer.\n", CONSTANTS.CHUNK_SIZE);
                in.readBytes(output, CONSTANTS.CHUNK_SIZE);
                System.out.printf("Output Stream now contains %d bytes.\n", output.size());

                System.out.printf("Creating new zipfile and LDS.\n");
                File f = new File("passport");
                new FileOutputStream(f).write(output.toByteArray()); // TODO: Do we need to assign the FOS to a variable?
                ZipFile zipFile = new ZipFile(f, ZipFile.OPEN_DELETE);
                LDS lds = new LDS();

                System.out.printf("Looking for entries in the zip.\n");

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    System.out.printf("Zip entry found: %s.\n", entry);
                }


                // fileOutputStream.write(output.toByteArray());
                // fileOutputStream.close();

                // TODO: How do we know if we have all the bytes before attempting to create a passport object?
                // Passport p = new Passport(file, trustStore);

                // Send acknowledgement
                // out.add(CONSTANTS.ACK);
            }
        } catch (IndexOutOfBoundsException oob) {
            System.err.printf("Index out of bounds. %s.\n", oob.getMessage());
            System.exit(1235);
        } catch (ZipException ze) {
            System.err.printf("Zip Exception: %s.\n", ze.getMessage());
        } catch (IllegalArgumentException iae) {
            System.err.printf("Illegal Argument Exception: %s.\n", iae.getMessage());
        }
    }
}
