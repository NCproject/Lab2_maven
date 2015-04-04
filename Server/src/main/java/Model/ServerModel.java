package Model;

import java.sql.SQLException;
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
	 * @throws ServerException 
	 */
	public boolean authorisation(String login, String password) throws SQLException;

	/**
	 * set the db
	 * @param db database
	 */
	public void setDb(DB db);
	
	/**
	 * return all filters
	 * @return List<Faculty>
	 * @throws ServerException 
	 */
	public List<Faculty> getFilters() throws SQLException;
	
	/**
	 * Return filters students
	 * @param facultyId id faculty
	 * @param groupId id of group
	 * @param lastName student`s lastname
	 * @return List<Student>
	 * @throws ServerException 
	 */
	public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws SQLException;

	public void addFaculty(Faculty faculty) throws SQLException;
	public void addGroup(Group group) throws SQLException;
	public void addStudent(Student student) throws SQLException;
	public void changeFaculty(Faculty faculty) throws SQLException;
	public void changeGroup(Group group) throws SQLException;
	public void changeStudent(Student student) throws SQLException;
	public void removeFaculty(int id) throws SQLException;
	public void removeGroup(int id) throws SQLException;
	public void removeStudent(int id) throws SQLException;
}
