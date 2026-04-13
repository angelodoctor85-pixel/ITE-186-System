import java.time.LocalDateTime;

public class Rental {
    private int id;
    private int bikeId;
    private int customerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalHours;
    private double totalFee;
    private double depositPaid;
    private double refundAmount;
    private String status;
    
    public Rental() {
        this.startTime = LocalDateTime.now();
        this.status = "Active";
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getBikeId() { return bikeId; }
    public void setBikeId(int bikeId) { this.bikeId = bikeId; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }
    
    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }
    
    public double getDepositPaid() { return depositPaid; }
    public void setDepositPaid(double depositPaid) { this.depositPaid = depositPaid; }
    
    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}