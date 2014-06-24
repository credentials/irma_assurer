package org.irmacard.identity.verifier;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

/**
 * This class represents the backend of the IRMA card assurer protocol. It verifies the credentials read and sent by
 * the tablet to ensure their validity. To do this, it uses the JMRTD toolset. If the credentials are valid, it will
 * create and sign an IRMA attribute to be issued by the assurer.
 */
public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;

    PrintWriter out;
    BufferedReader in;

    RSAPrivateKey skT;

    public Server() {
        try {
            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            serverSocket = ssf.createServerSocket(8888);
            System.out.println(serverSocket);
            clientSocket = serverSocket.accept(); // Blocks until a connection is made. Move it to the start() method.
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateSessionKey() {
        // TODO: Method stub
    }

    private void verifyCredential() {
        // TODO: Method stub
    }

    private void createAttribute() {
        // TODO: Method stub
    }

    private void signAttribute() {
        // TODO: Method stub
    }

    private void start() {
        System.out.println("Awaiting instructions");
        String command;
        try {
            while ((command = in.readLine()) != null) {
                System.out.println("Received command \"" + command + "\".");
                if (command.equals("VERIFY_ID")) {
                    out.println("UNDERSTOOD");
                } else {
                    out.println("ERROR");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
