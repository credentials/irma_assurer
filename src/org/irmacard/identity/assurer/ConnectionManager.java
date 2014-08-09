package org.irmacard.identity.assurer;

import org.irmacard.identity.common.CONSTANTS;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class ConnectionManager {
    Crypto crypto;
    private static ConnectionManager instance = null;
    private Connection connection = null;
    private final String defaultHost = "localhost";
    private final int defaultPort = 9999;

    private ConnectionManager() {
        // TODO: Constructor stub
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public Connection connectToServer() {
        return connectToServer(defaultHost, defaultPort);
    }

    private Connection connectToServer(String hostname, int port) {
        connection = new Connection(hostname, port);
        return connection;
    }

    // TODO: We need a more sophisticated way of sending commands, rather than via strings
    public String sendCommand(Connection connection, String command) {
        return connection.sendCommand(command);
    }

    /**
     * Inner Connection class to make sure only the Connection Manager can set it up
     */
    private class Connection {
        private byte CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_DISCONNECTED;
        private SSLSocket sslSocket = null;

        private Connection(String hostname, int port) {
            System.out.println("Setting up a new connection to the server...");

            try {
                sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostname, port);

                CONNECTION_STATUS = CONSTANTS.CONNECTION_STATUS_CONNECTED;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (ConnectException ce) {
                System.out.println("ERROR: " + ce.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String sendCommand(String command) {
            // Simply echo the command for now
            return command;
        }

        private boolean isConnected() {
            return CONNECTION_STATUS == CONSTANTS.CONNECTION_STATUS_CONNECTED;
        }

        private void close() {
            // TODO: Method stub
        }

        private void reset() {
            // TODO: Method stub
        }
    }
}
