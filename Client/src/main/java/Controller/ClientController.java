package Controller;

import View.MainView;
import Exception.*;
import Model.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ClientController extends Thread {

    /** JFrame of the MainView */
    private MainView view;

    /** The Client */
    private Client client;

    /* The connection */
    private boolean connection;

    /** List of faculties */
    private List<Faculty> facultiesList;

    /** List of students */
    private List<Student> studentsList;

    /** The access from server */
    private String access = new String();

    /** The logger */
    private static final Logger logger = Logger.getLogger(ClientController.class);

    /**
     * Starting new thread for every client
     *
     * @param client Client
     * @param view JFrame of the MainView
     */
    public ClientController(Client client, final MainView view ) {
        this.client = client;
        this.view = view;
        start();
        configureMainView();
        getListFaculties();
    }

    /**
     * Set the text to the logger.debug
     *
     * @param text Text to the debug
     */
    private static void debugLogger(String text){
        if (logger.isDebugEnabled()){
            logger.debug(text);
        }
    }

    @Override
    public void run() {
        try {
            connection = true;
            while (connection) {
                parsingAnswer(reading());
            }
        } catch (ServerException e){
            logger.error("Can't update data from server!", e);
            view.showMessage("Can't update data from server!");
        }
    }


    /**
     * Reads answer from server
     */
    public String reading() throws ServerException {
        debugLogger("Reading InputStream");
        /* Answer from server side. */
        String xmlResult = new String();
        try {
            DataInputStream in = new DataInputStream(SocketSingleton.getSocket().getInputStream());
            xmlResult = in.readUTF();
        }
        catch (Exception e) {
            throw new ServerException(e);
        }
        return xmlResult;
    }

    /**
     * Parses server answer according to ACTION
     *
     * @param xmlResult Result from Server
     * @throws ServerException
     */
    public void parsingAnswer(String xmlResult) throws ServerException {
        debugLogger("Parsing the answer:" + xmlResult);
        try{
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlResult));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression expr = xPath.compile("//envelope/*");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList xDoc = (NodeList) result;

            NodeList xHeader = (NodeList) xDoc.item(0);
            Node xItem =  xDoc.item(1);
            NodeList xBody = (NodeList) xItem.getFirstChild();
            String action = xPath.evaluate("//action", xHeader);
            if ("SHOW_FILTERS".equals(action)) {
                facultiesList = new ArrayList<>();
                studentsList = new ArrayList<>();
                XPathExpression expr2 = xPath.compile("//faculties/*");
                NodeList xFaculties = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < xFaculties.getLength(); i++) {
                    Faculty faculty = new Faculty();
                    Element g = (Element) xFaculties.item(i);
                    faculty.setId(Integer.parseInt(g.getFirstChild().getTextContent()));
                    faculty.setName(xPath.evaluate("name", g));
                    NodeList groups = g.getElementsByTagName("group");

                    for (int u = 0; u < groups.getLength(); u++) {
                        Group group = new Group();
                        Element studentElement = (Element) groups.item(u);
                        Integer groupId = Integer.parseInt(studentElement.getFirstChild().getTextContent());
                        group.setID(groupId);
                        group.setNumber(xPath.evaluate("number", studentElement));
                        faculty.addGroup(group);
                    }
                    this.facultiesList.add(faculty);
                }
                view.setFilters(facultiesList);
            }  else  if ("SEARCH_STUDENTS".equals(action)){
                XPathExpression expr3 = xPath.compile("//students/*");
                NodeList xStudents = (NodeList) expr3.evaluate(doc, XPathConstants.NODESET);
                if (studentsList.size() != 0 ) {
                    studentsList.clear();
                }
                for (int i = 0; i < xStudents.getLength(); i++) {
                    Student student = new Student();
                    Element g = (Element) xStudents.item(i);
                    student.setId(Integer.parseInt(g.getFirstChild().getTextContent()));
                    student.setFirstName(xPath.evaluate("firstName", g));
                    student.setLastName(xPath.evaluate("lastName", g));
                    student.setEnrolled(xPath.evaluate("enrolledDate", g));
                    this.studentsList.add(student);
                }
                view.showStudents(studentsList);
            } else if ("REMOVE_FACULTY".equals(action) || "REMOVE_GROUP".equals(action) ||
                    "ADD_FACULTY".equals(action) || "ADD_GROUP".equals(action) ) {
                this.access = xPath.evaluate("//status", xBody);
                if (access.equals("Success")){
                    getListFaculties();
                } else {
                    String stackTrace = xPath.evaluate("//stackTrace", xBody);
                    logger.error(stackTrace);
                    throw new ServerException(stackTrace);
                }
            }
            else {
                this.access = xPath.evaluate("//status", xBody);
                if (access.equals("Success")){
                    showStudents("");
                } else {
                    String stackTrace = xPath.evaluate("//stackTrace", xBody);
                    logger.error(stackTrace);
                    throw new ServerException(stackTrace);
                }
            }
        }
        catch(XPathExpressionException | IOException | ParserConfigurationException | SAXException e){
            throw new ServerException(e);
        }
    }

    /**
     * Specifies the configuration View
     */
    private void configureMainView(){

        /** Add ActionListener to the JB_AddFacultyOrGroup */
        view.getJB_AddFacultyOrGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.getButtonAddClick()){
                    addFaculty();
                } else {
                    addGroup();
                }
            }
        });

        /** Add ActionListener to the JB_UpdateStudent */
        view.getJB_UpdateStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeStudent();
            }
        });

        /** Add ActionListener to the JB_RemoveFaculty */
        view.getJB_RemoveFaculty().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFaculty();
                getListFaculties();
            }
        });

        /** Add ActionListener to the JB_RemoveGroup */
        view.getJB_RemoveGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeGroup();
                getListFaculties();
            }
        });

        /** Add ActionListener to the JB_UpdateTable */
        view.getJB_UpdateTable().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.getJCB_Faculty().getItemCount()!= 0 && view.getJCB_Group().getItemCount() != 0) {
                    view.getJB_AddStudent().setEnabled(true);
                }else {
                    view.getJB_AddStudent().setEnabled(false);
                }
               showStudents("");
            }
        });

        /** Add ActionListener to the JB_AddStudent */
        view.getJB_AddStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        /** Add ActionListener to the JB_RemoveStudent */
        view.getJB_RemoveStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStudent();
            }
        });

        /** Add ActionListener to the JB_SearchStudent */
        view.getJB_SearchStudent().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudents(view.getTextFromJTF_Search());
            }
        });

        /** Add ActionListener to the JB_ResetTable */
        view.getJB_ResetTable().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudents("");
            }
        });

        /** Add WindowListener to the View */
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connection = false;
            }
        });
    }


    /**
     * @return List of Faculties
     */
    private List<Faculty> getListFaculties(){
        try{
            client.getFilters();
        } catch (ClientException e){
            logger.error("Can't send data to the server!", e);
        }
        return facultiesList;
    }

    /**
     * Adds the Group to the Selected Faculty
     */
    private void addGroup(){
        try{
            if (!view.getNameOfNewFacultyOrGroup().equals("")){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                Group group = new Group(temp.getId(), view.getNameOfNewFacultyOrGroup());
                client.addGroup(group);
            } else {
                view.showMessage("Please, create the Name fo new Group.");
            }
        } catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }

    /**
     * Removes the Group
     */
    private void removeGroup(){
        try{
            if (!view.getJCB_Group().getSelectedItem().equals("")){
                Group temp = (Group) view.getJCB_Group().getSelectedItem();
                client.removeGroup(temp.getID());
            } else {
                view.showMessage("Please, select the Group for removing.");
            }
        }catch (ClientException e){
            logger.error("Can't update data form server", e);
        }
    }


    /**
     * Adds the Faculty
     */
    private void addFaculty(){
        try{
            if (!view.getNameOfNewFacultyOrGroup().equals("")){
                Faculty faculty = new Faculty();
                faculty.setName(view.getNameOfNewFacultyOrGroup());
                client.addFaculty(faculty);
            } else {
                view.showMessage("Please, create the Name for new Faculty.");
            }
        } catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }


    /**
     * Removes the Faculty
     */
    private void removeFaculty(){
        try{
            if (!view.getJCB_Faculty().getSelectedItem().equals("")){
                Faculty temp = (Faculty) view.getJCB_Faculty().getSelectedItem();
                client.removeFaculty(temp.getId());
            }else {
                view.showMessage("Please, select the Faculty for removing.");
            }
        }catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }


    /**
     * Shows list of students by selected Faculty and Group
     *
     * @param searchText Text for searching
     */
    private void showStudents(String searchText) {
        try{
            if (view.getJCB_Faculty().getItemCount()!= 0 && view.getJCB_Group().getItemCount() != 0){
                Faculty f = (Faculty) view.getJCB_Faculty().getSelectedItem();
                Group g = (Group) view.getJCB_Group().getSelectedItem();
                client.showStudents(f.getId(), g.getID(), searchText);
            } else {
                view.showMessage("Please, create the Faculty or Group.");
            }
        }catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }

    /**
     * Adds the Student to the selected Group
     */
    private void addStudent() {
        try{
            if (view.getJCB_Group().getItemCount()!=0 && view.getJCB_Faculty().getItemCount()!= 0){
                String firstName = view.getTextFromJTF_FirstName();
                String lastName = view.getTextFromJTF_LastName();
                String enrolled = view.getTextFromJTF_Enrolled();
                if (!firstName.equals("") && !lastName.equals("") && !enrolled.equals("")) {
                    Group g = (Group) view.getJCB_Group().getSelectedItem();
                    Student student = new Student();
                    student.setGroupId(g.getID());
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setEnrolled(enrolled);
                    client.addStudent(student);
                } else {
                    view.showMessage("Please, enter the First Name, Last Name and Enrolled Year of the student.");
                }
            } else {
                view.showMessage("Please, select the Faculty and Group. ");
            }
        }catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }

    /**
     * Changes the Student
     */
    private void changeStudent() {
        try{
            int i = view.getJT_Students().getSelectedRow();
            if (i != 0){
                int id = studentsList.get(i).getId();
                Student student = new Student();
                student.setId(id);
                student.setFirstName(view.getTextFromJTF_FirstName());
                student.setLastName(view.getTextFromJTF_LastName());
                student.setEnrolled(view.getTextFromJTF_Enrolled());
                client.changeStudent(student);
            } else {
                view.showMessage("Please, select the Student for Changing.");
            }
        } catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }

    /**
     * Removes the Student
     */
    private void removeStudent() {
        try{
            Integer i = view.getJT_Students().getSelectedRow();
            if (i != 0){
                view.getJB_RemoveStudent().setEnabled(true);
                client.removeStudent(view.getStudents().get(i).getId());
            } else {
                view.showMessage("Please, select the Student for Removing.");
            }
        }catch (ClientException e){
            logger.error("Can't send data to the server!",e);
        }
    }

}
