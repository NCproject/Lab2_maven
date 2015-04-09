package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class Student.
 */
public class Student {

    /** The id. */
    private int id;

    /** The first name. */
    private String firstName;

    /** The last name. */
    private String lastName;

    /** The group id. */
    private int groupId;

    /** The enrolled. */
    private String enrolled;

    public Student(){
        // TODO Auto-generated constructor stub
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the group number.
     *
     * @return the group number
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the group number.
     *
     * @param groupId the new group number
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the enrolled.
     *
     * @return the enrolled
     */
    public String getEnrolled() {
        return enrolled;
    }

    /**
     * Sets the enrolled.
     *
     * @param string the new enrolled
     */
    public void setEnrolled(String string) {
        this.enrolled = string;
    }

    /**
     * Creates the node.
     *
     * @param document the document
     */
    public void createNode(Document document, Element body) {
        appendChildToBody(document, body, "group", Integer.toString(getGroupId()));
        appendChildToBody(document, body, "studentName", getFirstName());
        appendChildToBody(document, body, "studentLastname", getLastName());
        appendChildToBody(document, body, "enrolledDate", getEnrolled());
    }

    private void appendChildToBody(Document document, Element body, String name, String text ){
        Element element = document.createElement(name);
        element.setTextContent(text);
        body.appendChild(element);
    }

    public void createNodeId(Document document, Element body){
        appendChildToBody(document, body, "id", Integer.toString(getId()));
    }

}