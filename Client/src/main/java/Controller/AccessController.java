package Controller;

import Model.*;
import View.*;
import Exception.*;

import org.apache.log4j.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccessController {

    private LoginView view;
    private Client client;
    private String access;
    private static final Logger logger = Logger.getLogger(ClientController.class);

    public AccessController(final Client client, final LoginView view) {
        this.client = client;
        this.view = view;
        view.setVisible(true);
        view.getButtonOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSign(view.getUserName(), view.getUserPassword());
                if (access.equals("allow")) {
                    view.setVisible(false);
                    MainView mainView = new MainView();
                    ClientController controller = new ClientController(client, mainView);
                } else {
                    view.setNullToTF();
                    view.showMessage("An incorrect password or username. Please enter again!");
                }
            }
        });
    }

    private String setSign(String login, String password) {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            this.access = client.getAccess(login, password);
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }
        return access;
    }

}
