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
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private MainView view;
    private LoginView loginView;
    private Client client;
    private String access;
    private List<Faculty> faculties;
    private List<Student> students = new ArrayList<>();
    private Integer groupID;
    private Integer facultyID;
    private Integer studentID;
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
                            Component[] c =  view.getJP_CreateStudent().getComponents();
                            for (Component k: c){
                                k.setEnabled(true);

                            }
                            view.getButtonSignIn().setVisible(false);
                        }else {
                            view.setEnabledComponents(false);
                            loginView.setNullToTF();
                            view.showMessage("An incorrect password or username. Please enter again!");
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
                    showStudents("");
                    view.buildTable(students);
            }
        });

        view.getJB_AddStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
                showStudents("");
                view.buildTable(students);
            }
        });

        view.getJB_ClearStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStudent();
                showStudents("");
                view.buildTable(students);
            }
        });

        view.getJB_Search().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudents(view.getTextFromJTF_Search());
                view.buildTable(students);
            }
        });
    }

    private String setSign(String login, String password) {
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            this.access = client.getSign(login, password);
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!" );
        }
        return access;
    }

    private List<Faculty> setListFaculties(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (client.getFilters()!= null) {
               this.faculties = client.getFilters();
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
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
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
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
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
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
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
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
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
        }
        return groupID;
    }

    private void showStudents(String searchText) throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (view.getJCB_Faculty().getItemCount()!= 0 && view.getJCB_Group().getItemCount() != 0){
                Faculty f = (Faculty) view.getJCB_Faculty().getSelectedItem();
                Group g = (Group) view.getJCB_Group().getSelectedItem();
                    if (students.size() == 0){
                    this.students = client.showStudents(f.getId(), g.getID(), searchText);}
                else{
                        this.students.clear();
                        this.students = client.showStudents(f.getId(), g.getID(), searchText);
                    }
            }

        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
        }
    }

    private Integer addStudent() throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (view.getJCB_Group().getItemCount() != 0){

                Group g = (Group) view.getJCB_Group().getSelectedItem();
                Integer group = g.getID();
                String first = view.getTextFromJTF_FirstName();
                String last = view.getTextFromJTF_LastName();
                String date = view.getTextFromJTF_Enrolled();
                this.studentID = client.addStudent(group, first, last, date);
            }

        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
        }
        return studentID;
    }

    private void removeStudent() throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
                Integer h = view.getJT_Students().getSelectedRow();
                view.getJB_ClearStudent().setEnabled(true);
                client.removeStudent(view.getStudents().get(h).getId());
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.getLoginView().showMessage("Can't update data from server!");
        }
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


}
