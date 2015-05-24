package dbaccess;

import javax.activation.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by denis on 2.3.15.
 * Database connector
 */
public class JDBCConnector {
    private Connection conn = null;

    /**
     * Establish connection with database
     * @return Connection to database
     * @throws JDBCConnectionException
     */
    public Connection getConnection() throws JDBCConnectionException {
        try {
            ConfigurationManager cfg = ConfigurationManager.getInstance();
            Class.forName(cfg.getDriver());
            conn = DriverManager.getConnection(cfg.getUrl(), cfg.getUser(), cfg.getPass());
            if (conn == null) {
                throw new JDBCConnectionException("Driver type is not correct in URL " + cfg.getUrl() + ".");
            }
        } catch (ClassNotFoundException e) {
            throw new JDBCConnectionException("Can't load database.properties driver.", e);
        } catch (SQLException e) {
            throw new JDBCConnectionException("Can't connect my_dbto database.properties.", e);
        } catch (FileNotFoundException e) {
            throw new JDBCConnectionException("Can't load database.properties.", e);
        } catch (IOException e){
            throw new JDBCConnectionException("Can't load database.properties.", e);
        }
        return conn;
    }

    /**
     * Close connection
     * @throws JDBCConnectionException
     */
    public void close() throws JDBCConnectionException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new JDBCConnectionException("Can't close connection", e);
            }
        }
    }
}
