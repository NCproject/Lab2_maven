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
    
    public Faculty(String name) {
    	setName(name);
    }
    
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
