package Model;

import Exception.*;

public interface ClientModel {

    void sendMessage(String message) throws ClientException;

    String authorisationMessage(String login, String password);

    String createMessage(String ACTION, String faculty, String group,
                         String firstName, String lastName, String  enrolledDate, Integer studentID, Integer facultyID, Integer groupID, String searchText);

    String reading() throws ServerException;

    void parsingAnswer(String xmlResult) throws ServerException;

}
