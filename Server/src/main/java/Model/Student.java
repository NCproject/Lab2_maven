package Model;

import java.text.ParseException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class Student.
 */
public class Student implements Cloneable {

    /** The id. */
    private int id;

    /** The first name. */
    private String firstName;

    /** The last name. */
    private String lastName;

    /** The group number. */
    private int groupId;
    
    /** The enrolled date. */
    private String enrolled;
    
    private int facultyId;

    @Override
    public String toString() {
        StringBuilder studentString = new StringBuilder();
        studentString.append("Student [id=");
        studentString.append(id);
        studentString.append(", firstName=");
        studentString.append(firstName);
        studentString.append(", lastName=");
        studentString.append(lastName);
        studentString.append(", groupId=");
        studentString.append(groupId);
        studentString.append(", enrolled=");
        studentString.append(enrolled);
        studentString.append("]");        
        return studentString.toString();
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
     * @param groupNumber the new group number
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the enrolled date.
     * 
     * @return the enrolled
     */
    public String getEnrolled() {
        return enrolled;
    }

    /**
     * Sets the enrolled date.
     * 
     * @param enrolled the new enrolled
     * @throws ParseException
     */
    public void setEnrolled(String enrolled) {       
        this.enrolled = enrolled;
    }

    /**
     * Instantiates a new student.
     * 
     * @param node the node
     * @throws ServerException if can't read the xml file or data format is wrong
     */
    public Student(Node node) throws ServerException {
        try {
            setId(Integer.parseInt(node.getAttributes().getNamedItem("id")
                    .getNodeValue()));
            setFirstName(node.getAttributes().getNamedItem("firstname")
                    .getNodeValue());
            setLastName(node.getAttributes().getNamedItem("lastname")
                    .getNodeValue());
            setGroupId(Integer.parseInt(node.getAttributes().getNamedItem("groupid")
                    .getNodeValue()));
            setEnrolled(node.getAttributes().getNamedItem("enrolled")
                    .getNodeValue());
        } catch (NumberFormatException e) {
            throw new ServerException(
                    "Can not create a student! Something wrong with id!", e);
        } catch (DOMException e) {
            throw new ServerException(
                    "Can not create a student! DOMException - something wrong with XML-file!",
                    e);
        }
    }

    /**
     * Creates the student node.
     * 
     * @param document the document
     * @param group that contains this student
     */
    public void createNode(Document document, Element group) {
        Element studentNode = document.createElement("student");
        studentNode.setAttribute("id", new Integer(getId()).toString());
        studentNode.setAttribute("firstname", getFirstName());
        studentNode.setAttribute("lastname", getLastName());
        studentNode.setAttribute("groupnumber",  new Integer(getGroupId()).toString());
        studentNode.setAttribute("enrolled", getEnrolled());
        group.appendChild(studentNode);
    }

    /**
     * Instantiates a new student.
     * 
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param groupNumber the group number
     * @param enrolled the enrolled date
     * @throws ServerException
     */
    public Student(String firstName, String lastName, String enrolled, int id) throws ServerException {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setEnrolled(enrolled); 
    }
    
    /**
     * Instantiates a new student.
     * 
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param groupNumber the group number
     * @param enrolled the enrolled date
     * @throws ServerException
     */
    public Student(int groupId, String firstName, String lastName, String enrolled) throws ServerException {        
        setFirstName(firstName);
        setLastName(lastName);
        setGroupId(groupId);
        setEnrolled(enrolled); 
    }

    public Student() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Return the student`s faculty id
     * @return int
     */
    public int getFacultyId() {
    	return this.facultyId;
    }
    
    /**
     * Set the student`s faculty id
     * @param facultyId id of faculty
     */
    public void setFacultyId(int facultyId) {
    	this.facultyId = facultyId;
    }
}
