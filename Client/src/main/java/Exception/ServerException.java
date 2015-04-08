package Exception;

public class ServerException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model exception.
     *
     * @param message the message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new model exception.
     */
    public ServerException(Exception e) {
        super(e);
    }
}
