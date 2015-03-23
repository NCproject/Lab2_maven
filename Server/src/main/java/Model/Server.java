package Model;

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
            log.debug("Constructor call");
    }

    public boolean authorisation(String login, String password) {
        return db.authorisation(login, password);
    }

    public List<Faculty> getFilters() {
        return db.getAllFacultiesWithGroups();
    }

    public List<Student> getStudentsByFilters(int facultyId, int groupId, String lastName) {
        return db.getStudentsByFilters(facultyId, groupId, lastName);
    }
}