package Model;

import Exception.*;

public interface ClientModel {

    /**
     * Sends message to the server
     *
     * @param message Message
     * @throws ClientException
     */
    void sendMessage(String message) throws ClientException;

    /**
     * Sends a request for the Adding Group
     *
     * @param group Group
     * @throws ClientException
     */
    public void addGroup( Group group) throws ClientException;


    /**
     * Sends a request for the Removing Group
     *
     * @param groupID ID of the Group
     * @throws ClientException
     */
    public void removeGroup( Integer groupID) throws ClientException;

    /**
     * Sends a request for the Adding Faculty
     *
     * @param faculty Faculty
     * @throws ClientException
     */
    public void addFaculty(Faculty faculty) throws ClientException;

    /**
     * Sends a request for the Removing Faculty
     *
     * @param facultyID ID of the Faculty
     * @throws ClientException
     */
    public void removeFaculty( Integer facultyID) throws ClientException;

    /**
     * Sends a request for the list of Students
     *
     * @param facultyID ID of the Faculty
     * @param groupID ID of the Group
     * @param searchText Text for Searching
     * @throws ClientException
     */
    public void showStudents( Integer facultyID, Integer groupID, String searchText) throws ClientException;

    /**
     * Sends a request for the Adding Student
     *
     * @param student Student
     * @throws ClientException
     */
   void addStudent(Student student) throws ClientException;

    /**
     * Sends a request for the Changing Student
     *
     * @param student Student
     * @throws ClientException
     */
   void changeStudent(Student student) throws ClientException ;

    /**
     * Sends a request for the Removing Student
     *
     * @param studentID ID of the Student
     * @throws ClientException
     */
   void removeStudent( Integer studentID) throws ClientException ;
}
