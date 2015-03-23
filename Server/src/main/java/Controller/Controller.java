package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Model.*;
import Controller.ControllerException;

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
    private int port = 8090;
    
    /** threadController */
    private ThreadController threadController;

    /**
     * Instantiates a new controller.
     * 
     * @param server the server
     * @param view the view
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
        try {
        	if ("AddFaculty".equals(e.getActionCommand())){
                int id = server.addFaculty((Faculty) e.getSource());
            	this.threadController.setResultId(id);
        	}
            if ("AddGroup".equals(e.getActionCommand())){
            	int id = server.addGroup((Group) e.getSource());
            	this.threadController.setResultId(id);
            }
            if ("AddStudent".equals(e.getActionCommand())){
            	int id = server.addStudent((Student) e.getSource());
            	this.threadController.setResultId(id);
            }
            if ("ChangeFaculty".equals(e.getActionCommand()))
                server.changeFaculty((Faculty) e.getSource());
            if ("ChangeGroup".equals(e.getActionCommand()))
                server.changeGroup((Group) e.getSource());
            if ("ChangeStudent".equals(e.getActionCommand()))
                server.changeStudent((Student) e.getSource());
            if ("RemoveFaculty".equals(e.getActionCommand()))
                server.removeFaculty((int) e.getSource());
            if ("RemoveGroup".equals(e.getActionCommand()))
                server.removeGroup((int) e.getSource());
            if ("RemoveStudent".equals(e.getActionCommand()))
                server.removeStudent((int) e.getSource());
        } catch (ServerException ex) {
            ControllerException e1 = new ControllerException(ex);
            log.error("Exception", e1);
            threadController.exceptionHandling(e1);
        }
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
