package dbaccess;

/**
 * Created by denis on 3.3.15.
 */
public class JDBCConnectionException extends Exception {
    public JDBCConnectionException(String  message, Throwable cause){
        super(message, cause);
    }
    public JDBCConnectionException(String message){
        super(message);
    }
}
