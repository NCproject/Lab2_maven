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
    private String serverAnswer;
    private String stackTrace;

    /** List of faculties. */
    private List<Faculty> facultiesList;

    /** List of students. */
    private List<Student> studentsList;

    private Integer groupID;
    private Integer facultyID;
    private Integer studentID;

    private String access;

    private Element messageText;
    private Element body;
    private Document document;
    public boolean connection;
    private String message;
    private String result;
    private DataOutputStream out;

    public Client(){
       // start();
    }
/*
    @Override
    public void run() {
        try {
            connection = true;
            while(connection) {
                sendMessage(message);
                reading();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }*/
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


   private Element singTag(String login, String password){
       Element loginTag = document.createElement("login");
       loginTag.setTextContent(login);
       body.appendChild(loginTag);
       Element passwordTag = document.createElement("password");
       passwordTag.setTextContent(password);
       return passwordTag;
   }

    /**
     * Create new message according to ACTION
     */
    public String authorisationMessage(String login, String password){
        if (log.isDebugEnabled()){
            log.debug("Creating SOAP message called");
        }
        createAction("SIGN");
        body.appendChild(singTag(login, password));
        return nodeToString(messageText);
    }


    public String filtersMessage(){
        if (log.isDebugEnabled()){
            log.debug("Creating SOAP message called");
        }
        createAction("SHOW_FILTERS");
        return nodeToString(messageText);
    }

    public String facultyUpdateMessage(String ACTION, Faculty faculty){
        createAction(ACTION);
        Element facultyNode = faculty.createNode(document);
        body.appendChild(facultyNode);
        return nodeToString(messageText);
    }

    /**
     * Create new message according to ACTION
     */
    public String createMessage(String ACTION, String faculty, String group,
                                 String firstName, String lastName, String  enrolledDate, Integer studentID, Integer facultyID, Integer groupID, String searchText) {
        if (log.isDebugEnabled()){
            log.debug("Creating SOAP message called");
        }

        StringBuilder message = new StringBuilder();
        message.append("<envelope><header><action>");
        message.append(ACTION);
        message.append("</action>");

        if ("SHOW_FILTERS".equals(ACTION)) {
            message.append("</header><body>");
        }

        if ("ADD_FACULTY".equals(ACTION)  || "CHANGEFaculty".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<name>");
            message.append(faculty);
            message.append("</name>");
        }

        if ("REMOVE_FACULTY".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<id>");
            message.append(facultyID);
            message.append("</id>");
        }

        if ("ADD_GROUP".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<faculty>");
            message.append(facultyID);
            message.append("</faculty>");
            message.append("<number>");
            message.append(group);
            message.append("</number>");
        }

        if ("CHANGEGroup".equals(ACTION)) {
            message.append("<groupID>");
            message.append(groupID);
            message.append("</groupID>");
            message.append("</header><body>");
            message.append("<groupName>");
            message.append(group);
            message.append("</groupName>");
        }

        if ("REMOVE_GROUP".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<id>");
            message.append(groupID);
            message.append("</id>");
        }

        if ("SEARCH_STUDENTS".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<faculty>");
            message.append(facultyID);
            message.append("</faculty>");
            message.append("<group>");
            message.append(groupID);
            message.append("</group>");
            message.append("<searchText>");
            message.append(searchText);
            message.append("</searchText>");
        }

        if ("ADD_STUDENT".equals(ACTION)){
            message.append("</header><body>");
            message.append("<group>");
            message.append(groupID);
            message.append("</group>");
            message.append("<studentName>");
            message.append(firstName);
            message.append("</studentName>");
            message.append("<studentLastname>");
            message.append(lastName);
            message.append("</studentLastname>");
            message.append("<enrolledDate>");
            message.append(enrolledDate);
            message.append("</enrolledDate>");
        }

        if ("REMOVE_STUDENT".equals(ACTION)){
            message.append("</header><body>");
            message.append("<id>");
            message.append(studentID);
            message.append("</id>");
        }

        message.append("</body></envelope>");
        return message.toString();
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
                //evaluate строка compile узел
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
            } else  if ("ADD_Group".equals(action)){
                this.result = xPath.evaluate("//id", xBody);
            }else  if ("SEARCH_STUDENTS".equals(action)){
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
                if ("ADD_FACULTY".equals(action) || "CHANGE_FACULTY".equals(action) || "REMOVE_FACULTY".equals(action) ||
                        "ADD_STUDENT".equals(action) || "CHANGE_STUDENT".equals(action) || "REMOVE_STUDENT".equals(action) ||
                        "ADD_GROUP".equals(action) || "CHANGE_GROUP".equals(action) || "REMOVE_GROUP".equals(action)){
                    this.access = xPath.evaluate("//status", xBody);
                    if (access.equals("Exception")){
                        stackTrace = xPath.evaluate("//stackTrace", xBody);
                    }
                  }
            else {
                serverAnswer = action;
                if ("Exception".equals(action)) {
                    NodeList xException = (NodeList) xPath.evaluate("//envelope/body", is, XPathConstants.NODESET);
                    stackTrace = xPath.evaluate("//stackTrace", xException);
                }}

        }catch(XPathExpressionException | IOException | ParserConfigurationException | SAXException e){
            throw new ServerException(e);
        }
    }


    public String getSign(String login, String password) throws ServerException,ClientException {
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
        sendMessage(filtersMessage());
        //sendMessage(createMessage("SHOW_FILTERS", null, null, null, null, null, null, null, null, null));
        parsingAnswer(reading());
        return facultiesList;
    }


    public Integer addGroup( Integer facultyID, String group) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("ADD_GROUP", null, group, null, null, null, null, facultyID, null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return groupID;
    }

    public Integer removeGroup( Integer groupID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("REMOVE_GROUP", null, null, null, null, null, null, null, groupID, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return groupID;
    }


    public String addFaculty(Faculty faculty) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
      // sendMessage(createMessage("ADD_FACULTY", faculty, null, null, null, null, null, null, null, null));
        sendMessage(facultyUpdateMessage("ADD_FACULTY", faculty));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return result;
    }


    public Integer deleteFaculty( Integer facultyID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("REMOVE_FACULTY", null, null, null, null, null,null, facultyID, null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return groupID;
    }

    public List<Student> showStudents( Integer facultyID, Integer groupID, String searchText) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("SEARCH_STUDENTS", null, null, null, null, null,null, facultyID, groupID, searchText));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return studentsList;
    }

    public Integer addStudent( Integer groupID, String firstName, String lastName, String enrolled) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("ADD_STUDENT", null, null, firstName, lastName, enrolled, null, null, groupID, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return studentID;
    }

    public void removeStudent( Integer studentID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("REMOVE_STUDENT", null, null, null, null, null, studentID, null, null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
    }
 }