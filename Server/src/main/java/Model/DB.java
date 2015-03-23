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
    public ArrayList<Faculty> getFaculties(){
        ArrayList<Faculty> faculties = new ArrayList<Faculty>();
        Faculty faculty;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id,name FROM faculties");
            while (rs.next()){
                faculty = new Faculty();
                faculty.setName(rs.getString("name"));
                faculty.setId(rs.getInt("id"));
                faculties.add(faculty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return faculties;
    }

    /**
     * return all groups of faculty
     *
     * @param facultyId id of faculty
     * @return ArrayList<Group>
     */
    public ArrayList<Group> getGroups(int facultyId) {
        ArrayList<Group> groups = new ArrayList<Group>();
        Group group;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id,number,faculty_id FROM groups WHERE faculty_id = " + facultyId);
            while (rs.next()){
                group = new Group();
                group.setNumber(rs.getString("number"));
                group.setFaculty(facultyId);
                group.setId(rs.getInt("id"));
                groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * return all filters
     *
     * @return ArrayList<Faculty>
     */
    public ArrayList<Faculty> getAllFacultiesWithGroups() {
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
    public ArrayList<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) {
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
     */
    public boolean authorisation(String login, String password){
        if (log.isDebugEnabled())
            log.debug("Authorisation. User: " + login + " Password: " + password);
        boolean result = false;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM users WHERE login = '"
                    + login + "' AND password = '" + password + "'");
            if (rs.next())
                result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Exception", e);
        }

        return result;
    }
}