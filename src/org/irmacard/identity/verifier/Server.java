package org.irmacard.identity.verifier;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
    RSAPrivateKey skT;

    public Server() {
        try {
            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            serverSocket = ssf.createServerSocket(8888);
            System.out.print(serverSocket);
            clientSocket = serverSocket.accept(); // Blocks until a connection is made. Move it to the start() method.
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (SSLException e) {
            System.out.print("Error");
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
        System.out.println("Initializing server.");
        while (true) {
            System.out.println("Waiting for a request.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Abort requested. Terminating.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        // server.start();
    }
}
