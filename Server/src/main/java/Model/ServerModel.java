package Model;

import java.util.List;

/**
 * The Interface ServerModel.
 */
public interface ServerModel {

    /**
     * user authorization
     * @param login user login
     * @param password user password
     * @return boolean true if access is allow
     */
    public boolean authorisation(String login, String password);

    /**
     * set the db
     * @param db database
     */
    public void setDb(DB db);

    /**
     * return all filters
     * @return List<Faculty>
     */
    public List<Faculty> getFilters();

    /**
     * Return filters students
     * @param facultyId id faculty
     * @param groupId id of group
     * @param lastName student`s lastname
     * @return List<Student>
     */
    public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName);

}