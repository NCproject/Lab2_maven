package Model;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import Exception.*;

import java.util.ArrayList;
import java.util.List;

public class Faculty {

    /** The name. */
    private String name;

    /** The faculty. */
    private Element facultyNode;

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



    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }
    /**
     * Gets the name.
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
     * Gets the groups.
     *
     * @return the groups
     * @throws Exception the exception
     */
    public List<Group> getGroups() {

        return groups;
    }


    public String toString(){
        return name;
    }

    /**
     * Creates the node.
     *
     * @param document the document
     */
    public Element createNode(Document document) {
        facultyNode = document.createElement("name");
        facultyNode.setTextContent(getName());
        return facultyNode;
    }
}
