package Model;

import java.util.ArrayList;

/**
 * The Class Faculty.
 */
public class Faculty {

    /** The name of faculty, unique. */
    private String name;
    private int id;

    private ArrayList<Group> groups = new ArrayList<Group>();  
    
    public Faculty() {
    }

    /**
     * Instantiates a new faculty.
     *
     * @param name the faculty name
     */
    public Faculty(String name) {
    	setName(name);
    }

    /**
     * Instantiates a new faculty.
     *
     * @param name the faculty name
     * @param id the faculty id
     */
    public Faculty(String name, int id) {
    	setName(name);
    	setId(id);
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
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId(){
    	return this.id;
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
     * Sets the list of groups.
     *
     * @param groups the list of groups
     */
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    /**
     * Gets the list of groups.
     *
     * @return the list of groups
     */
    public ArrayList<Group> getGroups(){
    	return this.groups;
    }
}
