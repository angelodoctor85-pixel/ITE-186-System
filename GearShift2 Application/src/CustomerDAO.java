import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setIdNumber(rs.getString("id_number"));
                customer.setDepositBalance(rs.getDouble("deposit_balance"));
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    public static boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, id_number, deposit_balance) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getIdNumber());
            pstmt.setDouble(5, customer.getDepositBalance());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }
}