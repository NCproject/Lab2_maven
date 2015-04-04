package Model;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The Class Server, using XML as DB.
 */
public class Server implements ServerModel {
	/** logger */
	private static final Logger log = Logger.getLogger(Server.class);
	
	/** database */
	private DB db;
	
	public void setDb(DB db) {
		this.db = db;
	}
	
	/**
	 * Instantiates a new server.
	 * 
	 * @throws ServerException
	 *             the server exception
	 */
	public Server() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Construktor call");
	}

	public boolean authorisation(String login, String password) throws SQLException {
		return db.authorisation(login, password);
	}
	
	public List<Faculty> getFilters() throws SQLException {
		return db.getAllFacultiesWithGroups();
	}
	
	public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws SQLException {
		return db.getStudentsByFilters(facultyId, groupId, lastName);
	}

	public void addFaculty(Faculty faculty) throws SQLException {
		db.addFaculty(faculty);
	}

	public void addGroup(Group group) throws SQLException {
		db.addGroup(group);
	}

	public void addStudent(Student student) throws SQLException {
		db.addStudent(student);
	}

	public void changeFaculty(Faculty faculty) throws SQLException {
		db.changeFaculty(faculty);
	}

	public void changeGroup(Group group) throws SQLException {
		db.changeGroup(group);
	}

	public void changeStudent(Student student) throws SQLException {
		db.changeStudent(student);
	}

	public void removeFaculty(int id) throws SQLException {
		db.removeFaculty(id);
	}

	public void removeGroup(int id) throws SQLException {
		db.removeGroup(id);
	}

	public void removeStudent(int id) throws SQLException {
		db.removeStudent(id);
	}
}
