package Model;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class DB {

    /**
     * logger
     */
    private static final Logger log = Logger.getLogger(DB.class);

    /**
     * DB host
     */
    private String host;

    /**
     * database name
     */
    private String dbName;

    /**
     * DB login
     */
    private String user;

    /**
     * DB password
     */
    private String password;

    /**
     * logger
     */
    private Connection conn;

    public DB(String host, String dbName, String user, String password) {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    /**
     * Create connection to mySQL DB
     */
    public void connectToDB() {
        if (log.isDebugEnabled())
            log.debug("Connection to DB");
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbName, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Exception", e);
            System.exit(0);
        }
    }

    /**
     * Return connection to mySQL DB
     *
     * @return Connection
     */
    public Connection getConnection() {
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
        PreparedStatement st = conn.prepareStatement("SELECT id, name FROM faculties");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
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
        PreparedStatement st = conn.prepareStatement("SELECT id, number, faculty_id FROM groups WHERE faculty_id = ?");
        st.setInt(1, facultyId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
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
     * @param groupId   id of group
     * @param lastName  student`s lastname
     * @return ArrayList<Student>
     */
    public ArrayList<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) throws SQLException {
        ArrayList<Student> students = new ArrayList<Student>();
        Student student;
        try {
            String query = "SELECT * FROM students s JOIN groups g ON s.group_id = g.id "
                    + "JOIN faculties f ON g.faculty_id = f.id";
            if (facultyId != 0 || groupId != 0 || !lastName.equals("")) {
                query += " WHERE ";
                if (facultyId != 0)
                    query += "g.faculty_id = ?";
                if (groupId != 0)
                    query += (facultyId != 0 ? " AND " : "") + "s.group_id = ?";
                if (!lastName.equals(""))
                    query += ((facultyId != 0 || groupId != 0) ? " AND " : "")
                            + "s.last_name LIKE '%?%'";
            }

            PreparedStatement st = conn.prepareStatement(query);
            int i = 1;
            if (facultyId != 0)
                st.setInt(i++, facultyId);
            if (groupId != 0)
                st.setInt(i++, groupId);
            if (!lastName.equals(""))
                st.setString(i, lastName);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
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
     * @param login    user login
     * @param password user password
     * @return boolean true if access is allow
     * @throws SQLException
     */
    public boolean authorisation(String login, String password) throws SQLException {
        if (log.isDebugEnabled())
            log.debug("Authorisation. User: " + login + " Password: " + password);
        boolean result = false;
        PreparedStatement st = conn.prepareStatement("SELECT id FROM users WHERE login = ? AND password = ?");
        st.setString(1, login);
        st.setString(2, password);
        ResultSet rs = st.executeQuery();
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
    public void addFaculty(Faculty faculty) throws SQLException {
        if (log.isDebugEnabled())
            log.debug("Add faculty. " + faculty.toString());
        PreparedStatement st = conn.prepareStatement("INSERT INTO FACULTIES(NAME) VALUES(?)");
        st.setString(1, faculty.getName());
        st.execute();
    }

    /**
     * Add group to DB
     *
     * @param group group object
     * @return int group`s id
     * @throws SQLException
     */
    public void addGroup(Group group) throws SQLException {
        if (log.isDebugEnabled())
            log.debug("Add group. " + group.toString());
        PreparedStatement st = conn.prepareStatement("INSERT INTO GROUPS(FACULTY_ID, NUMBER) VALUES(?,?)");
        st.setInt(1, group.getFakulty());
        st.setString(2, group.getNumber());
        st.execute();
    }

    /**
     * Add student to DB
     *
     * @param student student object
     * @return int student`s id
     * @throws SQLException
     */
    public void addStudent(Student student) throws SQLException {
        if (log.isDebugEnabled())
            log.debug("Add student. " + student.toString());
        PreparedStatement st = conn.prepareStatement("INSERT INTO STUDENTS(GROUP_ID, FIRST_NAME, LAST_NAME, ENROLLED) VALUES(?,?,?,?)");
        st.setInt(1, student.getGroupId());
        st.setString(2, student.getFirstName());
        st.setString(3, student.getLastName());
        st.setString(4, student.getEnrolled());
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("UPDATE FACULTIES SET NAME = ? WHERE ID = ?");
        st.setString(1,faculty.getName());
        st.setInt(2,faculty.getId());
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("UPDATE GROUPS SET NUMBER = ? WHERE ID = ?");
        st.setString(1,group.getNumber());
        st.setInt(2, group.getId());
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("UPDATE STUDENTS SET FIRST_NAME = ?, LAST_NAME = ?, ENROLLED = ? WHERE ID = ?");
        st.setString(1,student.getFirstName());
        st.setString(2,student.getLastName());
        st.setString(3,student.getEnrolled());
        st.setInt(4,student.getId());
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("DELETE FROM FACULTIES WHERE ID = ?");
        st.setInt(1, id);
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("DELETE FROM GROUPS WHERE ID = ?");
        st.setInt(1, id);
        st.execute();
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
        PreparedStatement st = conn.prepareStatement("DELETE FROM STUDENTS WHERE ID = ?");
        st.setInt(1, id);
        st.execute();
    }

    /**
     * Get max id from table
     *
     * @param tableName table for search
     * @return int id max id for table
     * @throws SQLException
     */
    private int getId(String tableName) throws SQLException {
        int id = 0;
        PreparedStatement st = conn.prepareStatement("SELECT MAX(id) as max FROM ?");
        st.setString(1, tableName);
        ResultSet rs = st.executeQuery();
        if (rs.next())
            id = rs.getInt("max");
        return id;
    }
}
