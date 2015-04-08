package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class Group.
 */
public class Group {

    /** The number. */
    private String number;

    private Integer id;

    /** The faculty. */
    private int facultyId;

    public Group(){
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new group.
     *
     * @param facultyId the faculty
     * @param number the number
     */

    public Group(int facultyId, String number) {
        setFaculty(facultyId);
        setNumber(number);
    }
    /**
     * Gets the faculty.
     *
     * @return the faculty
     */
    public int getFaculty() {
        return facultyId;
    }

    /**
     * Sets the faculty.
     *
     * @param facultyId the new faculty
     */
    public void setFaculty(int facultyId) {
        this.facultyId = facultyId;
    }

    /**
     * Gets the number.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    public String toString(){
        return number;
    }

    public void setID(Integer id){
        this.id = id;
    }

    public Integer getID(){
        return id;
    }

    /**
     * Creates the node.
     *
     * @param document the document
     */
    public void createNode(Document document, Element body) {
        Element nodeFaculty = document.createElement("faculty");
        nodeFaculty.setTextContent(Integer.toString(getFaculty()));
        body.appendChild(nodeFaculty);
        Element nodeNumber = document.createElement("number");
        nodeNumber.setTextContent(getNumber());
        body.appendChild(nodeNumber);
    }
}