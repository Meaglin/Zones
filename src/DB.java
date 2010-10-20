import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DB {

	private String driver,username,password,db;
	protected static final Logger log = Logger.getLogger("Minecraft");
	 
	 public Connection getConnection() {
	        try {
	            return DriverManager.getConnection(db + "?autoReconnect=true&user=" + username + "&password=" + password);
	        } catch (SQLException ex) {
	            log.log(Level.SEVERE, "Unable to retreive connection", ex);
	        }
	        return null;
	}
	 
	public DB(){

		PropertiesFile properties = new PropertiesFile("mysql.properties");
        driver = properties.getString("driver", "com.mysql.jdbc.Driver");
        username = properties.getString("user", "root");
        password = properties.getString("pass", "root");
        db = properties.getString("db", "jdbc:mysql://localhost:3306/minecraft");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, "Unable to find class " + driver, ex);
        }
	}

	public static DB getInstance() {
		return SingletonHolder._instance;
	}
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final DB _instance = new DB();
	}
}
