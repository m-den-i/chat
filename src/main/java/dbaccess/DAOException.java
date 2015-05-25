package dbaccess;

/**
 * Created by denis on 4.3.15.
 */
public class DAOException extends Exception {
    public DAOException(String message, Throwable cause){
        super(message, cause);
    }
    public DAOException(String message){
        super(message);
    }
}
