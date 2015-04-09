package Controller;

import View.MainView;
import Exception.*;
import Model.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController extends Thread {
    private MainView view;
    private Client client;

    private List<Faculty> faculties;
    private List<Student> students = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(ClientController.class);

    public ClientController(Client client, final MainView view ) {
        this.client = client;
        this.view = view;
        addActionListeners();
        getFilters();
    }

    private void addActionListeners(){

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

        view.getJB_UpdateStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeStudent();
                showStudents("");
                view.buildTable(students);
            }
        });

        view.getJB_DeleteFaculty().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFaculty();
                getFilters();
            }
        });

        view.getJB_DeleteGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeGroup();
                getFilters();
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

        view.getJB_DeleteStudent().addActionListener(new ActionListener() {
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

        view.getJB_Reset().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudents("");
                view.buildTable(students);
            }
        });
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
            view.showMessage("Can't update data from server!");
        }
        return faculties;

    }

    private void addGroup(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getNew().equals("")){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                Group group = new Group(temp.getId(), view.getNew());
                client.addGroup(group);
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data from server!",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void addFaculty(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getNew().equals("")){
                Faculty faculty = new Faculty();
                faculty.setName(view.getNew());
                client.addFaculty(faculty);
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data from server!",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void removeFaculty(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getJCB_Faculty().getSelectedItem().equals("")){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                client.removeFaculty(temp.getId());
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void removeGroup(){
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (!view.getJCB_Group().getSelectedItem().equals("")){
                Group temp = (Group) view.getJCB_Group().getSelectedItem();
                client.removeGroup(temp.getID());
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void showStudents(String searchText) throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (view.getJCB_Faculty().getItemCount()!= 0 && view.getJCB_Group().getItemCount() != 0){
                Faculty f = (Faculty) view.getJCB_Faculty().getSelectedItem();
                Group g = (Group) view.getJCB_Group().getSelectedItem();
                List<Student> temp = client.showStudents(f.getId(), g.getID(), searchText);
                if (temp.size() == 0) {
                    view.showMessage("Group " + g.getNumber() + " doesn't have a single student!");
                } else {
                    students.clear();
                    this.students = temp;
                }
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void addStudent() throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            if (view.getJCB_Group().getItemCount() != 0){
                Group g = (Group) view.getJCB_Group().getSelectedItem();
                Student student = new Student();
                student.setGroupId(g.getID());
                student.setFirstName(view.getTextFromJTF_FirstName());
                student.setLastName(view.getTextFromJTF_LastName());
                student.setEnrolled(view.getTextFromJTF_Enrolled());
                client.addStudent(student);
            }
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
    }

    private void changeStudent() throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
                int i = view.getJT_Students().getSelectedRow();
                int id = students.get(i).getId();
                Student student = new Student();
                student.setId(id);
                student.setFirstName(view.getTextFromJTF_FirstName());
                student.setLastName(view.getTextFromJTF_LastName());
                student.setEnrolled(view.getTextFromJTF_Enrolled());
                client.changeStudent(student);
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
        }
    }
    private void removeStudent() throws NullPointerException{
        if (logger.isDebugEnabled()){
            logger.debug("Called method to get data from server");
        }
        try{
            Integer h = view.getJT_Students().getSelectedRow();
            view.getJB_DeleteStudent().setEnabled(true);
            client.removeStudent(view.getStudents().get(h).getId());
        }catch (ClientException | ServerException e){
            logger.error("Can't update data form server",e);
            view.showMessage("Can't update data from server!");
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
