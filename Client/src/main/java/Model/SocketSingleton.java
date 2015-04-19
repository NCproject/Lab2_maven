package Model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import Exception.*;

public class SocketSingleton {

    /** The Socket */
    private static Socket socket;

    private SocketSingleton() {
    }

    /**
     * Creates socket? if it doesn't exist yet and return it
     *
     * @return socket
     * @throws ClientException
     */
    public static Socket getSocket() throws ClientException {
        if (socket==null) {
            try {
                String address = "127.0.0.1";
                InetAddress ipAddress = InetAddress.getByName(address);
                int serverPort = 8094;
                socket = new Socket(ipAddress, serverPort);
            }
            catch (IOException e) {
                throw new ClientException("Something wrong with socket",e);
            }
        }
        return socket;
    }
}