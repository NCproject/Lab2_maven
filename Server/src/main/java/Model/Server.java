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

	public boolean authorisation(String login, String password) throws ServerException {
		try {
			return db.authorisation(login, password);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}
	
	public List<Faculty> getFilters() throws ServerException {
		try {
			return db.getAllFacultiesWithGroups();
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}
	
	public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws ServerException {
		try {
			return db.getStudentsByFilters(facultyId, groupId, lastName);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public int addFaculty(Faculty faculty) throws ServerException {
		try {
			return db.addFaculty(faculty);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public int addGroup(Group group) throws ServerException {
		try {
			return db.addGroup(group);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public int addStudent(Student student) throws ServerException {
		try {
			return db.addStudent(student);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void changeFaculty(Faculty faculty) throws ServerException {
		try {
			db.changeFaculty(faculty);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void changeGroup(Group group) throws ServerException {
		try {
			db.changeGroup(group);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void changeStudent(Student student) throws ServerException {
		try {
			db.changeStudent(student);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void removeFaculty(int id) throws ServerException {
		try {
			db.removeFaculty(id);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void removeGroup(int id) throws ServerException {
		try {
			db.removeGroup(id);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}

	public void removeStudent(int id) throws ServerException {
		try {
			db.removeStudent(id);
		} catch (SQLException e) {
			throw new ServerException((Exception) e);
		}
	}
}
