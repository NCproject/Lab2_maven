package Model;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

import Exception.*;

public class Client implements ClientModel{
    /** The logger. */
    private static final Logger log = Logger.getLogger(Client.class);

    /** List of faculties. */
    private List<Faculty> facultiesList;

    /** List of students. */
    private List<Student> studentsList;

    private String access;
    private Element messageText;
    private Element body;
    private Document document;
    private DataOutputStream out;

    public Client(){
    }

    /**
     * Send message to server
     */
    public void sendMessage(String message) throws ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called send message");
        }
        try {
            out = new DataOutputStream(SocketSingleton.getSocket().getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            throw new ClientException(e);
        }finally {
            if (!(out == null)) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
            e.printStackTrace();
        }

    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }


    public String authorisationMessage(String login, String password){
        if (log.isDebugEnabled()){
            log.debug("Creating SOAP message called");
        }
        createAction("SIGN");
        Element loginTag = document.createElement("login");
        loginTag.setTextContent(login);
        body.appendChild(loginTag);
        Element passwordTag = document.createElement("password");
        passwordTag.setTextContent(password);
        body.appendChild(passwordTag);
        return nodeToString(messageText);
    }

    /**
     * Reading answer from server
     */
    public String reading() throws ServerException {
        if (log.isDebugEnabled()){
            log.debug("Reading stream called");
        }
        /* Answer from server side. */
        String xmlResult;
        try {
            DataInputStream in = new DataInputStream(SocketSingleton.getSocket().getInputStream());
            xmlResult = in.readUTF();
        } catch (Exception e) {
            throw new ServerException(e);
        }
        return xmlResult;
    }

    /**
     * Parsing server answer according to ACTION
     */
    public void parsingAnswer(String xmlResult) throws ServerException {
        if (log.isDebugEnabled()){
            log.debug("Parsing answer called");
        }
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
            } else  if ("SIGN".equals(action)){
                this.access = xPath.evaluate("//access", xBody);
            } else  if ("SEARCH_STUDENTS".equals(action)){
                XPathExpression expr3 = xPath.compile("//students/*");
                NodeList xStudents = (NodeList) expr3.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < xStudents.getLength(); i++) {
                    Student student = new Student();
                    Element g = (Element) xStudents.item(i);
                    student.setId(Integer.parseInt(g.getFirstChild().getTextContent()));
                    student.setFirstName(xPath.evaluate("firstName", g));
                    student.setLastName(xPath.evaluate("lastName", g));
                    student.setEnrolled(xPath.evaluate("enrolledDate", g));
                    this.studentsList.add(student);
                }
            }else

                this.access = xPath.evaluate("//status", xBody);
                if (access.equals("Exception")){
                    String stackTrace = xPath.evaluate("//stackTrace", xBody);
                    log.error(stackTrace);
                    throw new ServerException(stackTrace);
                }


        } catch(XPathExpressionException | IOException | ParserConfigurationException | SAXException e){
            throw new ServerException(e);
        }
    }

    public String getAccess(String login, String password) throws ServerException,ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get update");
        }
        sendMessage(authorisationMessage(login, password));
        parsingAnswer(reading());
        return access;
    }


    public List<Faculty> getFilters() throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get filters");
        }
        createAction("SHOW_FILTERS");
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
        return facultiesList;
    }

    public void addGroup( Group group) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding group");
        }
        createAction("ADD_GROUP");
        group.createNode(document, body);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public void removeGroup( Integer groupID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called removing group");
        }
        createAction("REMOVE_GROUP");
        Element nodeId = document.createElement("id");
        nodeId.setTextContent(Integer.toString(groupID));
        body.appendChild(nodeId);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public void addFaculty(Faculty faculty) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding faculty");
        }
        createAction("ADD_FACULTY");
        faculty.createNode(document, body);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public void removeFaculty( Integer facultyID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called removing faculty");
        }
        createAction("REMOVE_FACULTY");
        Element nodeId = document.createElement("id");
        nodeId.setTextContent(Integer.toString(facultyID));
        body.appendChild(nodeId);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public List<Student> showStudents( Integer facultyID, Integer groupID, String searchText) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        createAction("SEARCH_STUDENTS");
        Element nodeFaculty = document.createElement("faculty");
        nodeFaculty.setTextContent(Integer.toString(facultyID));
        body.appendChild(nodeFaculty);
        Element nodeGroup = document.createElement("group");
        nodeGroup.setTextContent(Integer.toString(groupID));
        body.appendChild(nodeGroup);
        Element nodeSearchText = document.createElement("searchText");
        nodeSearchText.setTextContent(searchText);
        body.appendChild(nodeSearchText);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
        return studentsList;
    }

    public void addStudent(Student student) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        createAction("ADD_STUDENT");
        student.createNode(document, body);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public void changeStudent(Student student) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called changing student");
        }
        createAction("CHANGE_STUDENT");
        student.createNodeId(document, body);
        student.createNode(document, body);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }

    public void removeStudent( Integer studentID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called removing student");
        }
        createAction("REMOVE_STUDENT");
        Element nodeId = document.createElement("id");
        nodeId.setTextContent(Integer.toString(studentID));
        body.appendChild(nodeId);
        sendMessage(nodeToString(messageText));
        parsingAnswer(reading());
    }
}