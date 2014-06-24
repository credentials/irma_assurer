package org.irmacard.identity.verifier;

import org.irmacard.identity.common.CONSTANTS;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.interfaces.RSAPrivateKey;

/**
 * This class represents the backend of the IRMA card assurer protocol. It verifies the credentials read and sent by
 * the tablet to ensure their validity. To do this, it uses the JMRTD toolset. If the credentials are valid, it will
 * create and sign an IRMA attribute to be issued by the assurer.
 */
public class Server {
    Crypto crypto;
    ServerSocket serverSocket;
    Socket clientSocket;

    PrintWriter out;
    BufferedReader in;

    RSAPrivateKey skT;

    public Server(int port) {
        try {
            crypto = new Crypto();
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
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

    private void verifyCredential() {
        // TODO: Method stub
    }

    private void createAttribute() {
        // TODO: Method stub
    }

    private void signAttribute() {
        // TODO: Method stub
    }

    private void authenticate() {
        crypto.generateSessionKey();
        crypto.encrypt("TODO");
        crypto.storeClientKey();
    }

    private void start() {
        authenticate();
        System.out.println("Server initialized. Awaiting instructions.");
        String instruction;
        try {
            while ((instruction = in.readLine()) != null) {
                System.out.println("Received instruction \"" + instruction + "\".");

                if (instruction.equals(CONSTANTS.INS_VERIFY_ID)) {
                    out.println(CONSTANTS.OK);
                } else if (instruction.equals(CONSTANTS.INS_SIGN_CREDENTIALS)) {
                    out.println(CONSTANTS.OK);
                } else {
                    out.println(CONSTANTS.ERROR);
                    System.out.println("Unsupported instruction \"" + instruction + "\"");
                }
            }
            System.out.println("Server shutting down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8888);
        server.start();
    }
}
