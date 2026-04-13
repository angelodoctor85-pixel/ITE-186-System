public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String idNumber;
    private double depositBalance;
    
    public Customer() {
        this.depositBalance = 0.0;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    
    public double getDepositBalance() { return depositBalance; }
    public void setDepositBalance(double depositBalance) { this.depositBalance = depositBalance; }
}