package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

import Model.*;
import org.apache.log4j.Logger;

/**
 * The Class Controller.
 */
public class Controller implements ActionListener {

    /** logger */
    private static final Logger log = Logger.getLogger(Controller.class);

    /** server */
    private ServerModel server;

    /** port */
    private int port = 7070;

    /** threadController */
    private ThreadController threadController;

    /** Connection */
    private Connection conn;

    /**
     * Instantiates a new controller.
     *
     * @param server the server
     *
     */
    public Controller(ServerModel server) {
        if (log.isDebugEnabled())
            log.debug("Constructor call");
        this.server = server;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + e.getActionCommand() + " "
                    + e.getSource());
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        try {
            ServerModel server = new Server();
            Controller controller = new Controller(server);
            DB db = new DB();
            db.connectToDB();
            server.setDb(db);
            controller.server = server;
            controller.starting();
        } catch (ServerException ex) {
            ControllerException e = new ControllerException(ex);
            log.error("Exception", e);
        }
    }

    public void starting() throws ServerException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        ServerSocket s = null;
        try {
            s = new ServerSocket(port);
            while (true) {
                Socket socket = s.accept();
                try {
                    threadController = new ThreadController(socket, this, server);
                }
                catch (IOException e) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.error("Error", e);
            ServerException ex = new ServerException (e);
            throw ex;
        } finally {
            try {
                if (s != null)
                    s.close();
            } catch (IOException e) {
                log.error("Error", e);
                ServerException ex = new ServerException (e);
                throw ex;
            }
        }
    }
}