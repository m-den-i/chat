package dbaccess;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;

/**
 * Created by denis on 4.3.15.
 * Configuration manager, loads Properties file
 */
public class ConfigurationManager {
    private String url, driver, user, pass;
    private static ConfigurationManager instance = null;
    private ConfigurationManager() throws FileNotFoundException, IOException{
        //PropertyResourceBundle = PropertyResourceBundle.
        Properties resource = new Properties();
        resource.load(new FileInputStream("database.properties"));
        url = resource.getProperty("url");
        driver = resource.getProperty("driver");
        user = resource.getProperty("user");
        pass = resource.getProperty("password");

    }

    /**
     * Singleton
     * @return Instance of manager
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static ConfigurationManager getInstance() throws FileNotFoundException, IOException{
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
