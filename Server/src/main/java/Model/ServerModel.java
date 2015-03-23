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
	 * @throws ServerException 
	 */
	public boolean authorisation(String login, String password) throws ServerException;

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
	public List<Faculty> getFilters() throws ServerException;
	
	/**
	 * Return filters students
	 * @param facultyId id faculty
	 * @param groupId id of group
	 * @param lastName student`s lastname
	 * @return List<Student>
	 * @throws ServerException 
	 */
	public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws ServerException;

	public int addFaculty(Faculty faculty) throws ServerException;
	public int addGroup(Group group) throws ServerException;
	public int addStudent(Student student) throws ServerException;
	public void changeFaculty(Faculty faculty) throws ServerException;
	public void changeGroup(Group group) throws ServerException;
	public void changeStudent(Student student) throws ServerException;
	public void removeFaculty(int id) throws ServerException;
	public void removeGroup(int id) throws ServerException;
	public void removeStudent(int id) throws ServerException;
}
