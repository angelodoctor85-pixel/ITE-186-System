import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalDialog extends JDialog {
    private static final Color BG_DARK = new Color(2, 8, 22);
    private static final Color BG_CARD = new Color(7, 18, 38);
    private static final Color ACCENT_BLUE = new Color(74, 142, 255);
    private static final Color TEXT_PRIMARY = new Color(243, 247, 255);
    private static final Color TEXT_SECONDARY = new Color(160, 175, 195);
    private static final Color GREEN_STATUS = new Color(46, 204, 113);
    
    private JComboBox<BikeItem> bikeSelector;
    private JComboBox<CustomerItem> customerSelector;
    private JTextField depositField;
    private JLabel estimatedCostLabel;
    private JSpinner hoursSpinner;
    private JTextArea conditionArea;
    private Runnable onRentalComplete;
    
    public RentalDialog(Frame owner, Runnable onComplete) {
        super(owner, "New Rental - Check Out", true);
        this.onRentalComplete = onComplete;
        
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("🚲 Bike Rental Check-Out");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Bike Selection
        mainPanel.add(createLabel("Select Bike:"));
        bikeSelector = new JComboBox<>();
        styleComboBox(bikeSelector);
        loadAvailableBikes();
        mainPanel.add(bikeSelector);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Customer Selection
        mainPanel.add(createLabel("Select Customer:"));
        customerSelector = new JComboBox<>();
        styleComboBox(customerSelector);
        loadCustomers();
        mainPanel.add(customerSelector);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Rental Hours
        mainPanel.add(createLabel("Rental Hours:"));
        SpinnerNumberModel hoursModel = new SpinnerNumberModel(1, 1, 24, 0.5);
        hoursSpinner = new JSpinner(hoursModel);
        styleSpinner(hoursSpinner);
        hoursSpinner.addChangeListener(e -> updateEstimate());
        mainPanel.add(hoursSpinner);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Deposit
        mainPanel.add(createLabel("Deposit Amount ($):"));
        depositField = new JTextField("50");
        styleTextField(depositField);
        mainPanel.add(depositField);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Bike Condition
        mainPanel.add(createLabel("Bike Condition Notes:"));
        conditionArea = new JTextArea(3, 20);
        conditionArea.setBackground(new Color(30, 40, 64));
        conditionArea.setForeground(TEXT_PRIMARY);
        conditionArea.setCaretColor(TEXT_PRIMARY);
        conditionArea.setLineWrap(true);
        conditionArea.setWrapStyleWord(true);
        JScrollPane conditionScroll = new JScrollPane(conditionArea);
        conditionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        conditionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(conditionScroll);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Cost Estimate
        estimatedCostLabel = new JLabel("Estimated Cost: $0.00");
        estimatedCostLabel.setForeground(GREEN_STATUS);
        estimatedCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        estimatedCostLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(estimatedCostLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(TEXT_SECONDARY);
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());
        
        JButton checkoutBtn = new JButton("Check Out Bike");
        checkoutBtn.setBackground(ACCENT_BLUE);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.addActionListener(e -> processCheckout());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(checkoutBtn);
        mainPanel.add(buttonPanel);
        
        add(mainPanel);
        
        // Initial estimate
        updateEstimate();
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(30, 40, 64));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(new Color(30, 40, 64));
        combo.setForeground(TEXT_PRIMARY);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ((JComponent) combo.getRenderer()).setBackground(new Color(30, 40, 64));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField field = ((JSpinner.DefaultEditor) editor).getTextField();
            field.setBackground(new Color(30, 40, 64));
            field.setForeground(TEXT_PRIMARY);
            field.setCaretColor(TEXT_PRIMARY);
        }
    }
    
    private void loadAvailableBikes() {
        List<Bike> bikes = BikeDAO.getAllBikes();
        for (Bike bike : bikes) {
            if ("Available".equalsIgnoreCase(bike.getStatus())) {
                bikeSelector.addItem(new BikeItem(bike));
            }
        }
    }
    
    private void loadCustomers() {
        List<Customer> customers = CustomerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customerSelector.addItem(new CustomerItem(customer));
        }
    }
    
    private void updateEstimate() {
        BikeItem selectedBike = (BikeItem) bikeSelector.getSelectedItem();
        if (selectedBike != null) {
            double hours = (Double) hoursSpinner.getValue();
            double rate = selectedBike.bike.getHourlyRate();
            double cost = hours * rate;
            estimatedCostLabel.setText(String.format("Estimated Cost: $%.2f (Rate: $%.2f/hr)", cost, rate));
        }
    }
    
    private void processCheckout() {
        BikeItem selectedBike = (BikeItem) bikeSelector.getSelectedItem();
        CustomerItem selectedCustomer = (CustomerItem) customerSelector.getSelectedItem();
        
        if (selectedBike == null || selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a bike and customer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double deposit;
        try {
            deposit = Double.parseDouble(depositField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid deposit amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double hours = (Double) hoursSpinner.getValue();
        double totalCost = hours * selectedBike.bike.getHourlyRate();
        
        // Create rental record
        Rental rental = new Rental();
        rental.setBikeId(selectedBike.bike.getId());
        rental.setCustomerId(selectedCustomer.customer.getId());
        rental.setStartTime(LocalDateTime.now());
        rental.setDepositPaid(deposit);
        rental.setTotalHours(hours);
        rental.setTotalFee(totalCost);
        rental.setStatus("Active");
        
        if (RentalDAO.createRental(rental)) {
            // Update bike status to Rented
            BikeDAO.updateBikeStatus(selectedBike.bike.getId(), "Rented");
            
            JOptionPane.showMessageDialog(this, 
                String.format("✅ Rental created successfully!\n\nBike: %s\nCustomer: %s\nHours: %.1f\nTotal: $%.2f\nDeposit: $%.2f",
                    selectedBike.bike.getName(),
                    selectedCustomer.customer.getName(),
                    hours,
                    totalCost,
                    deposit),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            if (onRentalComplete != null) {
                onRentalComplete.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create rental", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper classes for ComboBox display
    private class BikeItem {
        Bike bike;
        BikeItem(Bike bike) { this.bike = bike; }
        @Override public String toString() { 
            return String.format("%s - %s ($%.2f/hr)", bike.getName(), bike.getType(), bike.getHourlyRate());
        }
    }
    
    private class CustomerItem {
        Customer customer;
        CustomerItem(Customer customer) { this.customer = customer; }
        @Override public String toString() { 
            return String.format("%s (%s)", customer.getName(), customer.getPhone());
        }
    }
}