import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BikeDAO {
    
    public static List<Bike> getAllBikes() {
        List<Bike> bikes = new ArrayList<>();
        String sql = "SELECT * FROM bikes ORDER BY type, name";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bike bike = new Bike();
                bike.setId(rs.getInt("id"));
                bike.setName(rs.getString("name"));
                bike.setBrand(rs.getString("brand"));
                bike.setModel(rs.getString("model"));
                bike.setType(rs.getString("type"));
                bike.setStatus(rs.getString("status"));
                bike.setDescription(rs.getString("description"));
                bike.setImagePath(rs.getString("image_path"));
                bike.setHourlyRate(rs.getDouble("hourly_rate"));
                bikes.add(bike);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching bikes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bikes;
    }
    
    public static boolean addBike(Bike bike) {
        String sql = "INSERT INTO bikes (name, brand, model, type, status, description, image_path, hourly_rate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bike.getName());
            pstmt.setString(2, bike.getBrand() != null ? bike.getBrand() : "");
            pstmt.setString(3, bike.getModel() != null ? bike.getModel() : "");
            pstmt.setString(4, bike.getType());
            pstmt.setString(5, bike.getStatus() != null ? bike.getStatus() : "Available");
            pstmt.setString(6, bike.getDescription() != null ? bike.getDescription() : "");
            pstmt.setString(7, bike.getImagePath() != null ? bike.getImagePath() : "");
            pstmt.setDouble(8, bike.getHourlyRate() > 0 ? bike.getHourlyRate() : 8.0);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Bike added: " + bike.getName() + " - Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding bike: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateBikeStatus(int bikeId, String newStatus) {
        String sql = "UPDATE bikes SET status = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, bikeId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Bike status updated: ID=" + bikeId + " Status=" + newStatus);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating bike status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteBike(int bikeId) {
        String sql = "DELETE FROM bikes WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bikeId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Bike deleted: ID=" + bikeId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting bike: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static Bike getBikeById(int bikeId) {
        String sql = "SELECT * FROM bikes WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bikeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Bike bike = new Bike();
                bike.setId(rs.getInt("id"));
                bike.setName(rs.getString("name"));
                bike.setBrand(rs.getString("brand"));
                bike.setModel(rs.getString("model"));
                bike.setType(rs.getString("type"));
                bike.setStatus(rs.getString("status"));
                bike.setDescription(rs.getString("description"));
                bike.setImagePath(rs.getString("image_path"));
                bike.setHourlyRate(rs.getDouble("hourly_rate"));
                return bike;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching bike: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}