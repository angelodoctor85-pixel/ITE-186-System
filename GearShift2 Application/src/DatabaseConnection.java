import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:gearshift.db";
    
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}