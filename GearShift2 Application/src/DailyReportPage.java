import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class DailyReportPage {
    // Colors matching the website
    private static final Color BG_DARK = new Color(2, 8, 22);
    private static final Color BG_SIDEBAR = new Color(2, 8, 22, 200);
    private static final Color ACCENT_BLUE = new Color(74, 142, 255);
    private static final Color TEXT_PRIMARY = new Color(243, 247, 255);
    private static final Color TEXT_SECONDARY = new Color(140, 160, 190);
    private static final Color TEXT_MUTED = new Color(100, 120, 150);
    private static final Color GREEN_STATUS = new Color(46, 204, 113);
    private static final Color RED_STATUS = new Color(231, 76, 60);
    private static final Color CARD_BG = new Color(15, 25, 45);
    private static final Color CARD_BORDER = new Color(50, 60, 80);
    private static final Color TABLE_HEADER_BG = new Color(20, 30, 50);
    private static final Color TABLE_ROW_HOVER = new Color(30, 40, 60);
    
    private static JPanel mainContent;
    private static JFrame parentFrame;
    private static DefaultTableModel tableModel;
    private static JLabel lastUpdatedLabel;
    private static JLabel totalSalesLabel;
    private static Timer refreshTimer;
    
    static JPanel create(JFrame frame) {
        parentFrame = frame;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        
        // ===== LEFT SIDEBAR =====
        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(100, -1));
        sidebar.setMinimumSize(new Dimension(100, -1));
        sidebar.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        
        // ===== CENTER CONTENT =====
        mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top Bar
        JPanel topBar = createTopBar();
        mainContent.add(topBar, BorderLayout.NORTH);
        
        // Main Report Content
        JPanel reportContent = createReportContent();
        mainContent.add(reportContent, BorderLayout.CENTER);
        
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(mainContent, BorderLayout.CENTER);
        
        // Start auto-refresh
        startAutoRefresh();
        
        return panel;
    }
    
    private static void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(5000, e -> refreshReportData());
        refreshTimer.start();
    }
    
    public static void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
    
    private static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 0, 24, 0));
        
        JLabel gear = new JLabel("GEAR");
        gear.setForeground(TEXT_PRIMARY);
        gear.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gear.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel shift = new JLabel("SHIFT");
        shift.setForeground(ACCENT_BLUE);
        shift.setFont(new Font("Segoe UI", Font.BOLD, 16));
        shift.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(gear);
        sidebar.add(shift);
        sidebar.add(Box.createVerticalStrut(30));
        
        // HOME
        sidebar.add(createNavItem("🏠", "HOME", false, () -> {
            stopAutoRefresh();
            Main.showHomePage();
        }));
        sidebar.add(Box.createVerticalStrut(18));
        
        // MAINTENANCE
        sidebar.add(createNavItem("🔧", "MAINTENANCE", false, () -> {
            stopAutoRefresh();
            Main.showMaintenancePage();
        }));
        sidebar.add(Box.createVerticalStrut(18));
        
        // DAILY REPORT (active)
        sidebar.add(createNavItem("📊", "DAILY REPORT", true, () -> {}));
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private static JPanel createNavItem(String icon, String text, boolean active, Runnable onClick) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel iconContainer = new JPanel(new GridBagLayout());
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(52, 52));
        iconContainer.setMaximumSize(new Dimension(52, 52));
        
        if (active) {
            iconContainer.setBackground(new Color(74, 142, 255, 50));
            iconContainer.setBorder(BorderFactory.createLineBorder(new Color(74, 142, 255, 80), 1));
        }
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconLabel.setForeground(active ? ACCENT_BLUE : TEXT_SECONDARY);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconContainer.add(iconLabel);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        textLabel.setForeground(active ? ACCENT_BLUE : TEXT_SECONDARY);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(iconContainer);
        panel.add(Box.createVerticalStrut(4));
        panel.add(textLabel);
        
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        
        return panel;
    }
    
    private static JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel title = new JLabel("📋 History Page");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        leftPanel.add(title);
        
        // Right side - Total Sales & Refresh
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // Total Sales Card
        JPanel totalSalesCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        totalSalesCard.setOpaque(false);
        totalSalesCard.setLayout(new BoxLayout(totalSalesCard, BoxLayout.X_AXIS));
        totalSalesCard.setBorder(new EmptyBorder(10, 15, 10, 15));
        totalSalesCard.setPreferredSize(new Dimension(180, 45));
        totalSalesCard.setMaximumSize(new Dimension(180, 45));
        
        JLabel salesIcon = new JLabel("💰");
        salesIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        salesIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        totalSalesLabel = new JLabel("₱0");
        totalSalesLabel.setForeground(GREEN_STATUS);
        totalSalesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        totalSalesCard.add(salesIcon);
        totalSalesCard.add(totalSalesLabel);
        
        // Last updated
        lastUpdatedLabel = new JLabel("Last updated: Just now");
        lastUpdatedLabel.setForeground(TEXT_MUTED);
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Refresh button
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(74, 142, 255, 30));
        refreshBtn.setForeground(ACCENT_BLUE);
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 142, 255, 60), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshReportData());
        
        rightPanel.add(totalSalesCard);
        rightPanel.add(lastUpdatedLabel);
        rightPanel.add(refreshBtn);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private static JPanel createReportContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        
        // Create table
        String[] columns = {"Customer Name", "Date Rented", "Bike", "Total", "Date Returned", "Receipt"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(20, 30, 50));
                }
                return c;
            }
        };
        
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(40, 50, 70));
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(74, 142, 255, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(10, 0));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Customer Name
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Date Rented
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Bike
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Date Returned
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Receipt
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));
        header.setPreferredSize(new Dimension(0, 40));
        
        // Custom renderer for Receipt button
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_DARK);
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return content;
    }
    
    // Custom Button Renderer for Receipt column
    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View");
            setForeground(ACCENT_BLUE);
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBackground(isSelected ? new Color(74, 142, 255, 30) : new Color(74, 142, 255, 15));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(74, 142, 255, 60), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            return this;
        }
    }
    
    // Custom Button Editor for Receipt column
    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = "View";
            button.setText(label);
            button.setForeground(ACCENT_BLUE);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            button.setBackground(new Color(74, 142, 255, 15));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(74, 142, 255, 60), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            isPushed = true;
            selectedRow = row;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Show receipt dialog
                String customerName = tableModel.getValueAt(selectedRow, 0).toString();
                String bikeName = tableModel.getValueAt(selectedRow, 2).toString();
                String total = tableModel.getValueAt(selectedRow, 3).toString();
                
                JOptionPane.showMessageDialog(button,
                    "🧾 Receipt\n\n" +
                    "Customer: " + customerName + "\n" +
                    "Bike: " + bikeName + "\n" +
                    "Total: " + total + "\n\n" +
                    "Thank you for renting with GearShift!",
                    "Receipt",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private static void refreshReportData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private double totalSales = 0;
            private List<Object[]> activities = new ArrayList<>();
            
            @Override
            protected Void doInBackground() {
                String today = LocalDate.now().toString();
                
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:gearshift.db")) {
                    String rentalSql = """
                        SELECT r.*, b.name as bike_name, b.hourly_rate,
                               c.name as customer_name,
                               r.start_time, r.end_time, r.total_fee
                        FROM rentals r 
                        JOIN bikes b ON r.bike_id = b.id 
                        JOIN customers c ON r.customer_id = c.id 
                        ORDER BY r.start_time DESC
                    """;
                    
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(rentalSql)) {
                        
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                        
                        while (rs.next()) {
                            String customerName = rs.getString("customer_name");
                            String bikeName = rs.getString("bike_name");
                            double fee = rs.getDouble("total_fee");
                            totalSales += fee;
                            
                            String startTimeStr = rs.getString("start_time");
                            String dateRented = "";
                            if (startTimeStr != null && startTimeStr.length() >= 10) {
                                dateRented = startTimeStr.substring(0, 10);
                            }
                            
                            String endTimeStr = rs.getString("end_time");
                            String dateReturned = endTimeStr != null && endTimeStr.length() >= 10 ? 
                                endTimeStr.substring(0, 10) : "Active";
                            
                            String totalDisplay = String.format("₱%.2f", fee);
                            
                            activities.add(new Object[]{
                                customerName,
                                dateRented,
                                bikeName,
                                totalDisplay,
                                dateReturned,
                                "View"
                            });
                        }
                    }
                    
                } catch (SQLException e) {
                    System.err.println("Error fetching report data: " + e.getMessage());
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                // Update total sales
                totalSalesLabel.setText(String.format("₱%.2f", totalSales));
                
                // Update table
                tableModel.setRowCount(0);
                for (Object[] row : activities) {
                    tableModel.addRow(row);
                }
                
                // Update timestamp
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                lastUpdatedLabel.setText("Last updated: " + LocalDateTime.now().format(formatter));
            }
        };
        
        worker.execute();
    }
}