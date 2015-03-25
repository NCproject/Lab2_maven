package Model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Exception.*;

/**
 * The Class Group.
 */
public class Group {

    /** The faculty. */
    private String facultyName;

    /** The number. */
    private String number;

    /** The group. */
    private Element group;

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
    public String getFaculty() {
        return facultyName;
    }

    /**
     * Sets the faculty.
     *
     * @param facultyName the new faculty
     */
    public void setFaculty(String facultyName) {
        this.facultyName = facultyName;
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

    public void setFaculty(int facultyId) {
        this.facultyId = facultyId;
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
}