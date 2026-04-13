import java.sql.*;

public class DatabaseInitializer {
    
    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
            return;
        }
        
        String url = "jdbc:sqlite:gearshift.db";
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            // Create Bikes table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bikes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    brand TEXT,
                    model TEXT,
                    type TEXT NOT NULL,
                    status TEXT DEFAULT 'Available',
                    description TEXT,
                    image_path TEXT,
                    hourly_rate REAL DEFAULT 8.0
                )
            """);
            
            // Create Customers table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT,
                    email TEXT,
                    id_number TEXT,
                    deposit_balance REAL DEFAULT 0.0
                )
            """);
            
            // Create Rentals table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rentals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    bike_id INTEGER NOT NULL,
                    customer_id INTEGER NOT NULL,
                    start_time TIMESTAMP NOT NULL,
                    end_time TIMESTAMP,
                    total_hours REAL,
                    total_fee REAL,
                    deposit_paid REAL,
                    refund_amount REAL,
                    status TEXT DEFAULT 'Active'
                )
            """);
            
            // Create Maintenance table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS maintenance (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    bike_id INTEGER NOT NULL,
                    maintenance_date DATE NOT NULL,
                    maintenance_type TEXT NOT NULL,
                    description TEXT,
                    mechanic_name TEXT,
                    parts_replaced TEXT,
                    cost REAL,
                    next_due_date DATE
                )
            """);
            
            // Create Damage Logs table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS damage_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    bike_id INTEGER NOT NULL,
                    rental_id INTEGER,
                    damage_date TIMESTAMP NOT NULL,
                    description TEXT NOT NULL,
                    photo_path TEXT,
                    repair_cost REAL,
                    customer_charged BOOLEAN DEFAULT 0
                )
            """);
            
            System.out.println("✅ Database initialized successfully! (Empty - no sample data)");
            
        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}