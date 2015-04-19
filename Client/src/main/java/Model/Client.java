package Model;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;

import Exception.*;

public class Client implements ClientModel{

    /** The text of message to the server */
    private Element messageText;

    /** The body of the xml request */
    private Element body;

    /** The document */
    private Document document;

    /** The OutputStream to the server*/
    private DataOutputStream out;

    /** The logger. */
    private static final Logger logger = Logger.getLogger(Client.class);

    /**
     * Sends message to the server
     *
     * @param message Message
     * @throws ClientException
     */
    public void sendMessage(String message) throws ClientException {
        if (logger.isDebugEnabled()){
            logger.debug("Sending a message to the server: " + message);
        }
        try {
            out = new DataOutputStream(SocketSingleton.getSocket().getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            throw new ClientException(e);
        } finally {
            if (!(out == null)) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Appends Child to the body
     *
     * @param name Name of the new Element
     * @param text TextContext
     */
    private void appendChildToBody(String name, String text ){
        Element element = document.createElement(name);
        element.setTextContent(text);
        body.appendChild(element);
    }

    /**
     * Set a text to the logger.debug
     *
     * @param text Text to the debug
     */
    private static void debugLogger(String text){
        if (logger.isDebugEnabled()){
            logger.debug(text);
        }
    }

    /**
     * Creates Action tag in the messageText
     *
     * @param ACTION Action
     */
    private void createAction(String ACTION){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();
            messageText = document.createElement("envelope");
            Element header = document.createElement("header");
            messageText.appendChild(header);
            Element action = document.createElement("action");
            action.setTextContent(ACTION);
            header.appendChild(action);
            body = document.createElement("body");
            messageText.appendChild(body);
        } catch (ParserConfigurationException e) {
            logger.error(e);
        }
    }

    /**
     * Transform the MessageText to he String
     *
     * @param node
     * @return String node
     */
    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    /**
     * Creates a request for access.
     *
     * @param login User Login
     * @param password User Password
     * @return messageText
     */
    public String authorisationMessage(String login, String password){
        createAction("SIGN");
        appendChildToBody("login", login);
        appendChildToBody("password", password);
        return nodeToString(messageText);
    }

    /**
     * Sends a request for the List of Faculties
     *
     * @throws ClientException
     */
    public void getFilters() throws ClientException {
        createAction("SHOW_FILTERS");
        sendMessage(nodeToString(messageText));
    }

    /**
     * Sends a request for the Adding Group
     *
     * @param group Group
     * @throws ClientException
     */
    public void addGroup( Group group) throws ClientException {
        debugLogger("Adding a group: " + group);
        createAction("ADD_GROUP");
        group.createNode(document, body);
        sendMessage(nodeToString(messageText));
    }


    /**
     * Sends a request for the Removing Group
     *
     * @param groupID ID of the Group
     * @throws ClientException
     */
    public void removeGroup( Integer groupID) throws ClientException {
        debugLogger("Removing a group with ID: " + groupID);
        createAction("REMOVE_GROUP");
        appendChildToBody("id", Integer.toString(groupID));
        sendMessage(nodeToString(messageText));
    }


    /**
     * Sends a request for the Adding Faculty
     *
     * @param faculty Faculty
     * @throws ClientException
     */
    public void addFaculty(Faculty faculty) throws ClientException {
        debugLogger("Adding a faculty: " + faculty);
        createAction("ADD_FACULTY");
        faculty.createNode(document, body);
        sendMessage(nodeToString(messageText));
    }

    /**
     * Sends a request for the Removing Faculty
     *
     * @param facultyID ID of the Faculty
     * @throws ClientException
     */
    public void removeFaculty( Integer facultyID) throws ClientException {
        debugLogger("Removing a faculty with ID: " + facultyID);
        createAction("REMOVE_FACULTY");
        appendChildToBody("id", Integer.toString(facultyID));
        sendMessage(nodeToString(messageText));
    }


    /**
     * Sends a request for the list of Students
     *
     * @param facultyID ID of the Faculty
     * @param groupID ID of the Group
     * @param searchText Text for Searching
     * @throws ClientException
     */
    public void showStudents( Integer facultyID, Integer groupID, String searchText) throws ClientException {
        createAction("SEARCH_STUDENTS");
        appendChildToBody("faculty", Integer.toString(facultyID));
        appendChildToBody("group", Integer.toString(groupID));
        appendChildToBody("searchText", searchText);
        sendMessage(nodeToString(messageText));
    }

    /**
     * Sends a request for the Adding Student
     *
     * @param student Student
     * @throws ClientException
     */
    public void addStudent(Student student) throws ClientException {
        debugLogger("Adding a student: " + student);
        createAction("ADD_STUDENT");
        student.createNode(document, body);
        sendMessage(nodeToString(messageText));
    }

    /**
     * Sends a request for the Changing Student
     *
     * @param student Student
     * @throws ClientException
     */
    public void changeStudent(Student student) throws ClientException {
        debugLogger("Changing a student: " + student);
        createAction("CHANGE_STUDENT");
        student.createNodeId(document, body);
        student.createNode(document, body);
        sendMessage(nodeToString(messageText));
    }

    /**
     * Sends a request for the Removing Student
     *
     * @param studentID ID of the Student
     * @throws ClientException
     */
    public void removeStudent( Integer studentID) throws ClientException {
        debugLogger("Removing a student with ID: " + studentID);
        createAction("REMOVE_STUDENT");
        appendChildToBody("id", Integer.toString(studentID));
        sendMessage(nodeToString(messageText));
    }
}