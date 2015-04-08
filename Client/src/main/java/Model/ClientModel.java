package Model;

import Exception.*;

public interface ClientModel {

    void sendMessage(String message) throws ClientException;

    String reading() throws ServerException;

    void parsingAnswer(String xmlResult) throws ServerException;

}
