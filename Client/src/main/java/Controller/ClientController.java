package Controller;

import Model.Client;
import Model.Faculty;
import Model.Group;
import View.LoginView;
import View.MainView;

import Exception.*;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientController {
    private MainView view;
    private LoginView loginView;
    private Client client;
    private String access;
    private List<Faculty> faculties;
    /** The logger. */
    private static final Logger logger = Logger.getLogger(ClientController.class);

    public ClientController(Client client, final MainView view ) {
        this.client = client;
        this.view = view;

        view.setFacultyList(setListFaculties());
        view.getButtonSignIn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginView = new LoginView();
                loginView.setVisible(true);
                loginView.getButtonOK().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                         setSign(loginView.getUserName(), loginView.getUserPassword() );
                        if (access.equals("allow")){
                            view.setEnabledComponents(true);
                            loginView.setVisible(false);

                        }else {
                            view.setEnabledComponents(false);
                            loginView.setNullToTF();
                            view.showMessage("Please, insert again!");
                        }
                    }
                });
            }
        });

        List<Faculty> facultyList = setListFaculties();
        if (facultyList != null){
            for (Faculty f : facultyList){
                view.getJCB_Faculty().addItem(f);
                for (Group g : f.getGroups()){
                    view.getJCB_Group().addItem(g);
                }
            }
        }
    }

    private String setSign(String login, String password) {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            access = client.getSign(login, password);
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
        }
        return access;
    }

    private List<Faculty> setListFaculties(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (client.getFilters()!= null) {
               faculties = client.getFilters();
            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return faculties;

    }


}
