package Model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class Faculty.
 */
public class Faculty {

    /** The name of faculty, unique. */
    private String name;
    private int id;

    private ArrayList<Group> groups = new ArrayList<Group>();

    public Faculty() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Adds the group.
     *
     * @param group the group
     */
    public void addGroup(Group group) {
        groups.add(group);
    }

    /**
     * Gets the number.
     *
     * @return the number
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setName(String name) {
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public ArrayList<Group> getGroups(){
        return this.groups;
    }



}