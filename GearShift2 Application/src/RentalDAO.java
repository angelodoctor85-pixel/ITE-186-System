import java.sql.*;
import java.time.LocalDateTime;

public class RentalDAO {
    
    public static boolean createRental(Rental rental) {
        String sql = "INSERT INTO rentals (bike_id, customer_id, start_time, total_hours, total_fee, deposit_paid, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rental.getBikeId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setString(3, rental.getStartTime().toString());
            pstmt.setDouble(4, rental.getTotalHours());
            pstmt.setDouble(5, rental.getTotalFee());
            pstmt.setDouble(6, rental.getDepositPaid());
            pstmt.setString(7, rental.getStatus());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating rental: " + e.getMessage());
            return false;
        }
    }
}