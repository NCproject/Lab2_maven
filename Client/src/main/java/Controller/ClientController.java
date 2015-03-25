package Controller;

import Model.Client;
import Model.Faculty;
import Model.Group;
import View.LoginView;
import View.MainView;

import Exception.*;
import Model.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static java.awt.BorderLayout.CENTER;

public class ClientController {
    private MainView view;
    private LoginView loginView;
    private Client client;
    private String access;
    private List<Faculty> faculties;

    private List<Student> students;

    private Integer groupID;
    private Integer facultyID;
    private DefaultTableModel model1;
    /** The logger. */
    private static final Logger logger = Logger.getLogger(ClientController.class);

    public ClientController(Client client, final MainView view ) {
        this.client = client;
        this.view = view;
        getFilters();

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

        view.getJButtonAddNEw().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.getButtonAddClick()){
                    addFaculty();
                    getFilters();
                } else {
                    addGroup();
                    getFilters();
                }
            }
        });

        view.getJB_DeleteFaculty().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFaculty();
                getFilters();
            }
        });

        view.getJB_DeleteGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteGroup();
                getFilters();
            }
        });

        view.getJCB_Faculty().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        view.getJB_Update().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    view.setModelToTableOfStudents(getObjectModel(showStudents()));
                } catch (ClientException e1) {
                    e1.printStackTrace();
                }
            }
        });
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

    private Integer addGroup(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getNew().equals("")){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                groupID = client.addGroup(temp.getId(), view.getNew());
            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return groupID;

    }

    private Integer addFaculty(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getNew().equals("")){
                facultyID = client.addFaculty(view.getNew());
            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return facultyID;

    }

    private Integer deleteFaculty(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getJCB_Faculty().getSelectedItem().equals(null)){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                facultyID = client.deleteFaculty(temp.getId());
            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return groupID;
    }

    private Integer deleteGroup(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getJCB_Group().getSelectedItem().equals(null)){
                Group temp = (Group) view.getJCB_Group().getSelectedItem();
                groupID = client.removeGroup(temp.getID());
            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return groupID;
    }

    private List<Student> showStudents(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            Faculty f = (Faculty) view.getJCB_Faculty().getSelectedItem();
            Group g = (Group) view.getJCB_Group().getSelectedItem();
            if (!f.equals(null) && ! g.equals(null)){
                students = client.showStudents(f.getId(), g.getID(), " ");

            }
        }catch (ClientException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!" );
        }catch (ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
        return students;

    }

    private void getFilters(){
        List<Faculty> facultyList = setListFaculties();
        if (view.getJCB_Faculty().getItemCount() != 0){
            view.getJCB_Faculty().removeAllItems();
            view.getJCB_Faculty().repaint();
        }
        if (view.getJCB_Group().getItemCount() != 0){
            view.getJCB_Group().removeAllItems();
            view.getJCB_Group().repaint();
        }
        if (facultyList != null){
            for (Faculty f : facultyList){
                view.getJCB_Faculty().addItem(f);
                if (view.getJCB_Faculty().getSelectedItem().equals(f)){
                    List<Group> groups = f.getGroups();
                    for (Group g : groups){
                        view.getJCB_Group().addItem(g);
                    }
                }

            }
        }
        view.getJCB_Faculty().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                Faculty petName = (Faculty)cb.getSelectedItem();
                if (petName != null){
                    view.getJCB_Group().removeAllItems();
                    List<Group> groups = petName.getGroups();
                    for (Group g : groups){
                        view.getJCB_Group().addItem(g);
                    }
                }
            }
        });
    }

    public DefaultTableModel getObjectModel(List<Student> students) throws ClientException {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get object model");
        }
              String[][] data = new String[students.size()][4];
                Object[] columnNames = new Object[4];
                columnNames[0] = "firstname";
                columnNames[1] = "lastname";
                columnNames[2] = "enrolled";
                columnNames[3] = "groupnumber";
        int i = 0;
                for (Student st : students) {
                        data[i][0] = st.getFirstName();
                        data[i][1] = st.getLastName();
                        data[i][2] = st.getEnrolled();
                        data[i][3] = st.getGroupNumber();
                   i++;
                }
                model1 = new DefaultTableModel(data, columnNames);
                return model1;
        }
}
