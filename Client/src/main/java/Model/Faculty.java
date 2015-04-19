package Model;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class Faculty {

    /** The name. */
    private String name;

    /** The ID of the Faculty. */
    private int id;

    /** The list of Groups. */
    private ArrayList<Group> groups = new ArrayList<>();

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
     * Sets the id
     *
     * @param id id
     */
    public void setId(Integer id){
        this.id = id;
    }

    /**
     * Gets the id
     *
     * @return id
     */
    public Integer getId(){
        return id;
    }

    /**
     * Gets the name
     *
     * @return the name
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
     * Gets the groups
     *
     * @return the groups
     */
    public List<Group> getGroups() {

        return groups;
    }

    public String toString(){
        return name;
    }

    /**
     * Creates the node
     *
     * @param document the document
     */
    public void createNode(Document document, Element body) {
        /* The faculty. */
        Element facultyNode = document.createElement("name");
        facultyNode.setTextContent(getName());
        body.appendChild(facultyNode);
    }
}
