import java.awt.*;
import javax.swing.*;

public class Main {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static JButton fabButton;
    private static JLayeredPane layeredPane;
    
    public static void main(String[] args) {
        System.out.println("Initializing database...");
        DatabaseInitializer.initialize();
        
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    private static void createAndShowGUI() {
        frame = new JFrame("GearShift - Bike Rental Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.setLocationRelativeTo(null);
        
        layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(2, 8, 22));
        
        // Add all pages
        mainPanel.add(LoginPage.create(() -> showHomePage()), "Login");
        mainPanel.add(LandingPage.create(() -> showHomePage()), "Landing");
        mainPanel.add(HomePage.create(), "Home");
        mainPanel.add(MaintenancePage.create(frame), "Maintenance");
        mainPanel.add(DailyReportPage.create(frame), "DailyReport");
        
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        
        // Create FAB button
        fabButton = HomePage.createFabButton(frame);
        layeredPane.add(fabButton, JLayeredPane.PALETTE_LAYER);
        
        // Update layout when window resizes
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = frame.getContentPane().getWidth();
                int height = frame.getContentPane().getHeight();
                mainPanel.setBounds(0, 0, width, height);
                fabButton.setBounds(width - 80, height - 80, 56, 56);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        
        // Start on Home page
        showHomePage();
        frame.setVisible(true);
    }
    
    public static void showLoginPage() {
        cardLayout.show(mainPanel, "Login");
        frame.setTitle("GearShift - Login");
        fabButton.setVisible(false);
    }
    
    public static void showLandingPage() {
        cardLayout.show(mainPanel, "Landing");
        frame.setTitle("GearShift - Welcome");
        fabButton.setVisible(false);
    }
    
    public static void showHomePage() {
        // Refresh Home page when switching to it
        mainPanel.remove(2);
        mainPanel.add(HomePage.create(), "Home", 2);
        cardLayout.show(mainPanel, "Home");
        frame.setTitle("GearShift - Dashboard");
        fabButton.setVisible(true);
    }
    
    public static void showMaintenancePage() {
        // Refresh Maintenance page when switching to it
        mainPanel.remove(3);
        mainPanel.add(MaintenancePage.create(frame), "Maintenance", 3);
        cardLayout.show(mainPanel, "Maintenance");
        frame.setTitle("GearShift - Maintenance");
        fabButton.setVisible(false);
    }
    public static void showDailyReportPage() {
    mainPanel.remove(4);
    mainPanel.add(DailyReportPage.create(frame), "DailyReport", 4);
    cardLayout.show(mainPanel, "DailyReport");
    frame.setTitle("GearShift - Daily Report");
    fabButton.setVisible(false);
    }
}