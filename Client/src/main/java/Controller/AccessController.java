package Controller;

import Model.*;
import View.*;
import Exception.*;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;

public class AccessController{

    /** The JFrame of the LoginView */
    private LoginView view;

    /** The Client */
    private Client client;

    /** The access */
    private String access;

    /** The Logger */
    private static final Logger logger = Logger.getLogger(AccessController.class);

    /**
     * @param client Client
     * @param view JFrame of the LoginView
     */
    public AccessController(final Client client, final LoginView view) {
        this.client = client;
        this.view = view;
        configureLoginView();
    }

    /**
     * Sets a text to the logger.debug
     *
     * @param text Text to the debug
     */
    private static void debugLogger(String text){
        if (logger.isDebugEnabled()){
            logger.debug(text);
        }
    }

    /**
     * Specifies the configuration View
     */
    private void configureLoginView(){
        view.setVisible(true);
        view.getButtonOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAccess(view.getUserName(), view.getUserPassword());
                if (access.equals("allow")) {
                    view.setVisible(false);
                    MainView mainView = new MainView();
                    ClientController controller = new ClientController(client, mainView);
                } else {
                    view.setNullToTF();
                    view.showMessage("An incorrect password or username. Please enter again!");
                }
            }
        });
    }

    /**
     * Reads answer from server
     *
     * @return XmlResult from Server
     * @throws ServerException
     */
    public String reading() throws ServerException {
        debugLogger("Reading InputStream");
        /* Answer from server side. */
        String xmlResult = new String();
        try {
            DataInputStream in = new DataInputStream(SocketSingleton.getSocket().getInputStream());
            xmlResult = in.readUTF();
        } catch (Exception e) {
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
            if ("SIGN".equals(action)){
                this.access = xPath.evaluate("//access", xBody);
            }
            if (access.equals("Exception")){
                String stackTrace = xPath.evaluate("//stackTrace", xBody);
                logger.error(stackTrace);
                throw new ServerException(stackTrace);
            }
        } catch(XPathExpressionException | IOException | ParserConfigurationException | SAXException e){
            throw new ServerException(e);
        }
    }

    /**
     * Gets the Access by login and password.
     *
     * @param login User login
     * @param password User password
     * @return access
     */
    private String getAccess(String login, String password) {
        debugLogger("Getting access from server:" + login + password);
        try{
            client.sendMessage(client.authorisationMessage(login, password));
            try {
                parsingAnswer(reading());
            } catch (ServerException e) {
                e.printStackTrace();
            }
        }catch (ClientException e){
            logger.error("Can't update data form server", e);
            view.showMessage("Can't update data from server!");
        }
        return access;
    }

}
