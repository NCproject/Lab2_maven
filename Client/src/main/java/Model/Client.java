package Model;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
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

    private String access;

    private ArrayList filter_faculty;
    private String serverTest = "Server testing";

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

        if ("SHOW".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<facultyID>");
            message.append(facultyID);
            message.append("</facultyID>");
            message.append("<groupID>");
            message.append(groupID);
            message.append("</groupID>");
        }

        if ("ADDFaculty".equals(ACTION)  || "CHANGEFaculty".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<facultyName>");
            message.append(faculty);
            message.append("</facultyName>");
        }

        if ("REMOVEFaculty".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<facultyID>");
            message.append(facultyID);
            message.append("</facultyID>");
        }

        if ("ADDGroup".equals(ACTION)) {
            message.append("<facultyID>");
            message.append(facultyID);
            message.append("</facultyID>");
            message.append("</header><body>");
            message.append("<groupName>");
            message.append(group);
            message.append("</groupName>");
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

        if ("REMOVEGroup".equals(ACTION)) {
            message.append("</header><body>");
            message.append("<groupID>");
            message.append(groupID);
            message.append("</groupID>");
        }

        if ("SEARCHStudent".equals(ACTION)) {
            message.append("</header><body>");
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
            NodeList xBody = (NodeList) xDoc.item(1).getFirstChild();
            String action = xPath.evaluate("//action", xHeader);
            if ("SHOW_FILTERS".equals(action)) {
               facultiesList = new ArrayList<Faculty>();
                studentsList = new ArrayList<Student>();
                //evaluate строка compile узел

                XPathExpression expr2 = xPath.compile("//faculties");
                NodeList xFaculties = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < xFaculties.getLength(); i++) {
                    Faculty faculty = new Faculty();
                    Integer facultyId = Integer.parseInt(xPath.evaluate("//id", xFaculties.item(i)));
                    faculty.setId(facultyId);
                    faculty.setName(xPath.evaluate("//name", xFaculties.item(i)));
                    XPathExpression expr3 = xPath.compile("//groups");
                    NodeList xGroups = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j < xFaculties.getLength(); j++) {
                        Integer id = Integer.parseInt(xPath.evaluate("//id", xGroups.item(j)));
                        String name = xPath.evaluate("//name", xGroups.item(j));
                        Group group = new Group(facultyId, name);
                       faculty.addGroup(group);
                    }
                    this.facultiesList.add(faculty);
                }
            } else  if ("SIGN".equals(action)){
                String access = xPath.evaluate("//access", xBody);
                this.access = access;
            } else {
                serverAnswer = action;
                if ("Exception".equals(action)) {
                    NodeList xException = (NodeList) xPath.evaluate("//envelope/body", is, XPathConstants.NODESET);
                    stackTrace = xPath.evaluate("//stackTrace", xException);
                }
            }
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
        return studentsList;
    }

    public List getStudents(Integer facultyID, Integer groupID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get students");
        }
        sendMessage(createMessage("SHOW", null, null, null, null, null, null, facultyID, groupID, null));
        parsingAnswer(reading());
        return studentsList;
    }

    public List getSearchList(String searchText) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get students after searching");
        }
        sendMessage(createMessage("SEARCHStudent", null, null, null, null, null, null, null, null, searchText));
        parsingAnswer(reading());
        return studentsList;
    }


 }