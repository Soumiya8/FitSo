package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton JDBC Connection Manager for Oracle Database.
 * Supports fail-safe offline mode if Oracle Database is unreachable.
 */
public class DBConnection {

    private static String url;
    private static String user;
    private static String password;
    private static boolean initialized = false;
    private static boolean offlineFallbackMode = false;

    private static synchronized void loadConfig() {
        if (initialized) return;

        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            url = props.getProperty("db.url", "jdbc:oracle:thin:@//localhost:1521/XEPDB1");
            user = props.getProperty("db.user", "fitso_user");
            password = props.getProperty("db.password", "fitso123");

            try {
                Class.forName("oracle.jdbc.OracleDriver");
            } catch (ClassNotFoundException e) {
                System.out.println("INFO: Oracle JDBC Driver not on classpath. FitSo will use interactive local session mode.");
                offlineFallbackMode = true;
            }

            initialized = true;
        } catch (Exception e) {
            url = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
            user = "fitso_user";
            password = "fitso123";
            initialized = true;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            loadConfig();
        }
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            offlineFallbackMode = true;
            throw e;
        }
    }

    public static boolean isOfflineFallbackMode() {
        return offlineFallbackMode;
    }
}
