package Model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import Exception.*;

public class SocketSingleton {
    private static int serverPort = 8092;
    private static String address = "127.0.0.1";
    private static Socket socket;

    private SocketSingleton() {

    }

    /**
     * Create socket if it doesnt exist yet and return it
     */
    public static Socket getSocket() throws ClientException {
        if (socket==null) {
            try{
                InetAddress ipAddress = InetAddress.getByName(address);
                socket = new Socket(ipAddress, serverPort);
            } catch (IOException e) {
                throw new ClientException("Something wrong with socket",e);
            }
        }
        return socket;
    }
}