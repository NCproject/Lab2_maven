package Model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import Exception.*;

public class SocketSingleton {
    private static Socket socket;

    private SocketSingleton() {

    }

    /**
     * Create socket if it doesnt exist yet and return it
     */
    public static Socket getSocket() throws ClientException {
        if (socket==null) {
            try{
                String address = "127.0.0.1";
                InetAddress ipAddress = InetAddress.getByName(address);
                int serverPort = 8090;
                socket = new Socket(ipAddress, serverPort);
            } catch (IOException e) {
                throw new ClientException("Something wrong with socket",e);
            }
        }
        return socket;
    }
}