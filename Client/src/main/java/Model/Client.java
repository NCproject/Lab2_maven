package Model;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import javax.swing.table.DefaultTableModel;

import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;
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
    private DataOutputStream out;
    private DataInputStream in;
    private String serverAnswer;
    private String stackTrace;

    /** List of faculties. */
    private List<Faculty> facultiesList;

    /** List of groups. */
    private List<Group> groupsList;

    /** List of students. */
    private List<Student> studentsList;

    private Integer groupID;
    private Integer facultyID;

    private String access;


    /** Answer from server side. */
    private String xmlResult;

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
        }
    }

    /**
     * Create new message according to ACTION
     */
    public String authorisationMessage(String ACTION, String login, String password){
        StringBuilder message = new StringBuilder();
        message.append("<envelope><header><action>");
        message.append(ACTION);
        message.append("</action>");
        if ("SIGN".equals(ACTION)){

            message.append("</header><body>");
            message.append("<login>");
            message.append(login);
            message.append("</login>");
            message.append("<password>");
            message.append(password);
            message.append("</password>");
        }
        message.append("</body></envelope>");
        return message.toString();
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
            message.append("<facultyID>");
            message.append(facultyID);
            message.append("</facultyID>");
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
        try {
            in = new DataInputStream(SocketSingleton.getSocket().getInputStream());
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
                facultiesList = new ArrayList<Faculty>();
                studentsList = new ArrayList<Student>();
                //evaluate строка compile узел
                Integer facultyId = 0;
                XPathExpression expr2 = xPath.compile("//faculties/*");
                NodeList xFaculties = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                Integer ik = xFaculties.getLength();
                for (int i = 0; i < xFaculties.getLength(); i++) {
                    Faculty faculty = new Faculty();
                    Element g = (Element) xFaculties.item(i);
                    faculty.setId(Integer.parseInt(g.getFirstChild().getTextContent()));
                    faculty.setName(xPath.evaluate("name", g));
                    NodeList fh = g.getElementsByTagName("group");
                    Group gh = new Group();
                    Integer size = fh.getLength();
                    for (int u = 0; u < fh.getLength(); u++) {
                        Element studentElement = (Element) fh.item(u);
                        Integer groupId = Integer.parseInt(studentElement.getFirstChild().getTextContent());
                        gh.setID(groupId);
                        gh.setNumber(xPath.evaluate("number", studentElement));
                        faculty.addGroup(gh);
                    }
                    this.facultiesList.add(faculty);
                }
            } else  if ("SIGN".equals(action)){
                String access = xPath.evaluate("//access", xBody);
                this.access = access;
            } else  if ("ADD_Group".equals(action)){
                String access = xPath.evaluate("//id", xBody);
                this.access = access;
            }else  if ("SEARCH_STUDENTS".equals(action)){

                XPathExpression expr3 = xPath.compile("//students/*");
                NodeList xStudents = (NodeList) expr3.evaluate(doc, XPathConstants.NODESET);
                Integer ik = xStudents.getLength();
                for (int i = 0; i < xStudents.getLength(); i++) {
                    Student student = new Student();
                    Element g = (Element) xStudents.item(i).getFirstChild();
                    student.setId(Integer.parseInt(xPath.evaluate("id", g)));
                    student.setFirstName(xPath.evaluate("firstName", g));
                    student.setLastName(xPath.evaluate("lastName", g));
                    student.setEnrolled(xPath.evaluate("enrolledDate", g));
                    this.studentsList.add(student);
            }}
            else {
                serverAnswer = action;
                if ("Exception".equals(action)) {
                    NodeList xException = (NodeList) xPath.evaluate("//envelope/body", is, XPathConstants.NODESET);
                    stackTrace = xPath.evaluate("//stackTrace", xException);
                }}

        }catch(XPathExpressionException e){
            throw new ServerException(e);
        }catch(ParserConfigurationException e){
            throw new ServerException(e);
        }catch(SAXException e){
            throw new ServerException(e);
        }catch(IOException e){
            throw new ServerException(e);
        }
    }


    public String getSign(String login, String password) throws ServerException,ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get update");
        }
        sendMessage(authorisationMessage("SIGN", login, password));
        parsingAnswer(reading());
        return access;
    }

    public List getFilters() throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get filters");
        }
        sendMessage(createMessage("SHOW_FILTERS", null, null, null, null, null, null, null, null, null));
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


    public Integer addFaculty( String faculty) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("ADD_FACULTY", faculty, null, null, null, null, null, null, null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return facultyID;
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

 }