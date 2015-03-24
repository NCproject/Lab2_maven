package Model;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class DB {

	/** logger */
	private static final Logger log = Logger.getLogger(Server.class);
	
	/** DB host */
	private String host = "localhost";
	
	/** database name */
	private String dbName = "students";
	
	/** DB login */
	private String user = "root";
	
	/** DB password */
	private String password = "";
	
	/** logger */
	private Connection conn;
	
	/**
	 * Create connection to mySQL DB 
	 */
	public void connectToDB(){
		if (log.isDebugEnabled())
			log.debug("Connection to DB");
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbName, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Exception", e);        
		}
	}
	
	/**
	 * Return connection to mySQL DB 
	 * 
	 * @return Connection
	 */
	public Connection getConnection(){
		return this.conn;
	}
	
	/**
	 * Return all faculties 
	 * 
	 * @return ArrayList<Faculty>
	 */
	public ArrayList<Faculty> getFaculties() throws SQLException {
    	ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    	Faculty faculty;
		 Statement st = conn.createStatement();
		 ResultSet rs = st.executeQuery("SELECT id,name FROM faculties");
		 while (rs.next()){
			 faculty = new Faculty();
			 faculty.setName(rs.getString("name"));
			 faculty.setId(rs.getInt("id"));
			 faculties.add(faculty);
		 }	
    	return faculties;
    }
	
	 /**
	  * return all groups of faculty
	  * 	 
	 * @param facultyId id of faculty
	 * @return ArrayList<Group>
	 */
	public ArrayList<Group> getGroups(int facultyId) throws SQLException {
	    	ArrayList<Group> groups = new ArrayList<Group>();
	    	Group group;	    	
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery("SELECT id,number,faculty_id FROM groups WHERE faculty_id = " + facultyId);
			 while (rs.next()){
				 group = new Group();
				 group.setNumber(rs.getString("number"));
				 group.setFaculty(facultyId);
				 group.setId(rs.getInt("id"));
				 groups.add(group);
			 }	
	    	return groups;
	    }
	 
	 /**
	  * return all filters
	  * 
	 * @return ArrayList<Faculty>
	 */
	public ArrayList<Faculty> getAllFacultiesWithGroups() throws SQLException {
		 ArrayList<Faculty> faculties = getFaculties();
		 for (Faculty faculty : faculties) {
			 faculty.setGroups(getGroups(faculty.getId()));
		 }
		return faculties;
	 }
	 
	 /**
	  * filters students
	  * 
	 * @param facultyId id of faculty
	 * @param groupId id of group
	 * @param lastName student`s lastname
	 * @return ArrayList<Student>
	 */
	public ArrayList<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws SQLException {
		 ArrayList<Student> students = new ArrayList<Student>();
		 Student student;
		 try {
			 Statement st = conn.createStatement();
			 String query = "SELECT * FROM students s JOIN groups g ON s.group_id = g.id "
			 		+ "JOIN faculties f ON g.faculty_id = f.id";
			 if (facultyId != 0 || groupId != 0 || !lastName.equals("")) {
				 query += " WHERE ";
				 query += facultyId != 0 ? "g.faculty_id = " + facultyId : "";
				 query += groupId != 0 ? (facultyId != 0 ? " AND " : "") + "s.group_id = " + groupId : "";
				 query += !lastName.equals("") ? ((facultyId != 0 || groupId != 0)? " AND " : "")
						 + "s.last_name LIKE '%" + lastName + "%'" : "";
			 }
			 ResultSet rs = st.executeQuery(query);
			 while (rs.next()){
				 student = new Student();
				 student.setId(rs.getInt("s.id"));
				 student.setGroupId(rs.getInt("s.group_id"));
				 student.setFacultyId(rs.getInt("g.faculty_id"));
				 student.setFirstName(rs.getString("s.first_name"));
				 student.setLastName(rs.getString("s.last_name"));
				 student.setEnrolled(rs.getString("s.enrolled"));
				 students.add(student);
			 }				 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 return students;
	 }
	
 	/**
	 * Authorization for server operations
	 * 
	 * @param login user login
	 * @param password user password
	 * @return boolean true if access is allow
 	 * @throws SQLException 
	 */
	public boolean authorisation(String login, String password) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Authorisation. User: " + login + " Password: " + password);
		boolean result = false;
		 Statement st = conn.createStatement();
		 ResultSet rs = st.executeQuery("SELECT id FROM users WHERE login = '"
			 + login + "' AND password = '" + password + "'");
		 if (rs.next())
			 result = true;	
		
		return result;
	}
	
	/**
	 * Add faculty to DB
	 * 
	 * @param faculty faculty object
	 * @return int faculty`s id
	 * @throws SQLException
	 */
	public int addFaculty(Faculty faculty) throws SQLException {		
		if (log.isDebugEnabled())
			log.debug("Add faculty. " + faculty.toString());		
		Statement st = conn.createStatement();
		st.execute("INSERT INTO FACULTIES" +
				"(NAME)" +
				" VALUES(" + 
				"'" + faculty.getName() + "'" +
				")");
		return getId("faculties");
	}

	/**
	 * Add group to DB
	 * 
	 * @param group group object
	 * @return int group`s id
	 * @throws SQLException
	 */
	public int addGroup(Group group) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Add group. " + group.toString());
		Statement st = conn.createStatement();
		st.execute("INSERT INTO GROUPS" +
				"(FACULTY_ID, NUMBER)" +
				" VALUES(" +
				group.getFakulty() + "," +
				"'" + group.getNumber() + "'" +
				")");				
		return getId("groups");
	}

	/**
	 * Add student to DB 
	 * 
	 * @param student student object
	 * @return int student`s id
	 * @throws SQLException
	 */
	public int addStudent(Student student) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Add student. " + student.toString());
		Statement st = conn.createStatement();
		st.execute("INSERT INTO STUDENTS" +
				"(GROUP_ID, FIRST_NAME, LAST_NAME, ENROLLED)" +
				" VALUES(" +
				student.getGroupId() + "," +
				"'" + student.getFirstName() + "'" + "," +
				"'" + student.getLastName() + "'" + "," +
				"'" + student.getEnrolled() + "'" +
				")");
		return getId("students");
	}

	/**
	 * Change faculty in DB
	 * 
	 * @param faculty faculty object
	 * @throws SQLException
	 */
	public void changeFaculty(Faculty faculty) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Update faculty. " + faculty.toString());
		Statement st = conn.createStatement();
		st.execute("UPDATE FACULTIES SET " +
				"NAME = '" + faculty.getName() + "'" +
				"WHERE ID = " + faculty.getId());
	}

	/**
	 * Change group in DB
	 * 
	 * @param group group object
	 * @throws SQLException
	 */
	public void changeGroup(Group group) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Update group. " + group.toString());
		Statement st = conn.createStatement();
		st.execute("UPDATE GROUPS SET " +
				"NUMBER = '" + group.getNumber() + "'" +
				"WHERE ID = " + group.getId());
	}

	/**
	 * Change student in DB
	 * 
	 * @param student student object
	 * @throws SQLException
	 */
	public void changeStudent(Student student) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Update student. " + student.toString());
		Statement st = conn.createStatement();
		st.execute("UPDATE STUDENTS SET " +
				"FIRST_NAME = '" + student.getFirstName() + "'" + "," +
				"LAST_NAME = '" + student.getLastName() + "'" + "," +
				"ENROLLED = '" + student.getEnrolled() + "'" +
				"WHERE ID = " + student.getId());
	}

	/**
	 * Remove faculty from DB
	 * 
	 * @param id faculty`s id
	 * @throws SQLException
	 */
	public void removeFaculty(int id) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Remove faculty. Id = " + id);
		Statement st = conn.createStatement();
		st.execute("DELETE FROM FACULTIES WHERE ID = " + id);
	}

	/**
	 * Remove group from DB
	 * 
	 * @param id group`s id
	 * @throws SQLException
	 */
	public void removeGroup(int id) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Remove group. Id = " + id);
		Statement st = conn.createStatement();
		st.execute("DELETE FROM GROUPS WHERE ID = " + id);
	}

	/**
	 * Remove student from DB
	 * 
	 * @param id student`s id
	 * @throws SQLException
	 */
	public void removeStudent(int id) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("Remove student. Id = " + id);
		Statement st = conn.createStatement();
		st.execute("DELETE FROM STUDENTS WHERE ID = " + id);
	}
	
	/**
	 * Get max id from table
	 * 
	 * @param tableName table for search
	 * @return int id max id for table
	 * @throws SQLException
	 */
	private int getId(String tableName) throws SQLException{
		int id = 0;
		Statement st = conn.createStatement();
		 ResultSet rs = st.executeQuery("SELECT MAX(id) as max FROM " + tableName);
		 if (rs.next())
			 id = rs.getInt("max");			 
		 return id;
	}
}
