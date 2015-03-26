package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import Exception.*;

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

    /** The group number. */
    private String groupNumber;

    /** The student. */
    private Element student;

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
    public String getGroupNumber() {
        return groupNumber;
    }

    /**
     * Sets the group number.
     *
     * @param groupNumber the new group number
     */
    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
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



}