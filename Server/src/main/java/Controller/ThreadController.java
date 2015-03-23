package Controller;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import Model.*;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.*;

public class ThreadController extends Thread {
    private boolean connection;
    private ActionListener controller;
    private ServerModel model;
    private Socket socket;
    private static final Logger log = Logger.getLogger(ThreadController.class);
    private String xmlMessage;

    /**
     * Set model
     */
    public void setModel(ServerModel model) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        this.model = model;
    }

    /**
     * Set controller
     */
    public void setController(ActionListener controller) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        this.controller = controller;
    }

    /**
     * Starting new thread for every client
     */
    public ThreadController(Socket s, ActionListener controller, ServerModel model)
            throws IOException, ServerException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        setController(controller);
        setModel(model);
        socket = s;
        start();
    }

    @Override
    public void run() {
        try {
            connection = true;
            while(connection) {
                reading();
                parsing(xmlMessage);
            }
        } catch (Exception exc) {
            DataOutputStream out = null;
            try {
                log.error("Exception", exc);
                out = new DataOutputStream(socket.getOutputStream());
                exceptionHandling(exc);
                out.writeUTF(createMessage("Server was closed"));
            } catch (IOException e) {
                log.error("Exception", e);
            } finally {
                if (!(out == null)) {
                    try {
                        out.flush();
                    } catch (IOException e) {
                        log.error("Exception", e);
                    }
                }
            }
        }
    }

    /**
     * Creating exception message to answer
     */
    public void exceptionHandling(Exception ex) {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + ex);
    }

    /**
     * Getting message from client throw InputStream exception
     */
    private void reading() throws IOException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        DataInputStream in = new DataInputStream(socket.getInputStream());
        try {
            xmlMessage = in.readUTF();
        } catch (IOException e) {
            log.error("Exception", e);
            throw new IOException(e);
        }
    }

    /**
     * Parsing client message according to action
     *
     * @throws ServerException
     */
    private void parsing(String xmlMessage)
            throws ParserConfigurationException, IOException, SAXException,
            ServerException {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + xmlMessage);
        DataOutputStream out = null;
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlMessage));
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(is);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression expr2 = xPath.compile("//envelope/*");

            Object result = expr2.evaluate(doc, XPathConstants.NODESET);
            NodeList xDoc = (NodeList) result;
            Element xHeader = (Element) xDoc.item(0);
            Element xBody = (Element) xDoc.item(1);
            String action = xPath.evaluate("//action", xHeader);

            out = new DataOutputStream(socket.getOutputStream());

            if ("SIGN".equals(action)) {
                String login = xPath.evaluate("//login", xBody);
                String password = xPath.evaluate("//password", xBody);
                out.writeUTF(authorisationMessage(model.authorisation(login, password)));
            }

            if ("SHOW_FILTERS".equals(action)) {
                out.writeUTF(showMessage(model.getFilters()));
            }

            if ("SEARCH_STUDENTS".equals(action)) {
                int facultyId = Integer.parseInt(xPath.evaluate("//faculty", xBody));
                int groupId = Integer.parseInt(xPath.evaluate("//group", xBody));
                String lastName = xPath.evaluate("//searchText", xBody);
                out.writeUTF(filterMessage(model.getStudentsByFilters(facultyId, groupId, lastName)));
            }
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServerException(e);
        } finally {
            if (!(out == null)) {
                out.flush();
            }
        }
    }

    /**
     * Create response for authorisation request
     * @param isAuthorised access type
     * @return String
     */
    private String authorisationMessage(boolean isAuthorised) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SIGN</action></header><body><access>");
        builder.append(isAuthorised ? "allow" : "deny");
        builder.append("</access>");
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Create response for show filters request
     * @param faculties all filters
     * @return String
     */
    private String showMessage(List<Faculty> faculties) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SHOW_FILTERS</action></header><body><faculties>");
        for (Faculty faculty : faculties) {
            builder.append("<faculty>");
            builder.append("<id>");
            builder.append(faculty.getId());
            builder.append("</id>");
            builder.append("<name>");
            builder.append(faculty.getName());
            builder.append("</name>");
            builder.append("<groups>");
            List<Group> groups = faculty.getGroups();
            for (Group group : groups) {
                builder.append("<group>");
                builder.append("<id>");
                builder.append(group.getId());
                builder.append("</id>");
                builder.append("<number>");
                builder.append(group.getNumber());
                builder.append("<number>");
                builder.append("<facultyId>");
                builder.append(group.getFakulty());
                builder.append("</facultyId>");
                builder.append("</group>");
            }
            builder.append("</groups>");
            builder.append("</faculty>");
        }
        builder.append("</groups>");
        builder.append("</faculties>");
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Create response for student filter request
     * @param students filtered students
     * @return String
     */
    private String filterMessage(List<Student> students) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SEARCH_STUDENTS</action></header><body><students>");
        for (Student student : students) {
            builder.append("<student>");
            builder.append("<id>");
            builder.append(student.getId());
            builder.append("</id>");
            builder.append("<studentName>");
            builder.append(student.getFirstName());
            builder.append("</studentName>");
            builder.append("<studentLastName>");
            builder.append(student.getLastName());
            builder.append("</studentLastName>");
            builder.append("<group>");
            builder.append(student.getGroupId());
            builder.append("</group>");
            builder.append("<faculty>");
            builder.append(student.getFacultyId());
            builder.append("</faculty>");
            builder.append("<enrolledDate>");
            builder.append(student.getEnrolled());
            builder.append("</enrolledDate>");
            builder.append("</student>");
        }
        builder.append("</students>");
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Creating request close command
     */
    private String createMessage(String message) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>CLOSE</action></header><body><message>");
        builder.append(message);
        builder.append("</message></body></envelope>");

        return builder.toString();
    }
}