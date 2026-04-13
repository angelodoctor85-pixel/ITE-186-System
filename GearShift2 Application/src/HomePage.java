import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


class HomePage {
    // Colors matching the website
    private static final Color BG_DARK = new Color(2, 8, 22);
    private static final Color BG_SIDEBAR = new Color(2, 8, 22, 200);
    private static final Color BG_DASHBOARD = new Color(7, 18, 38);
    private static final Color ACCENT_BLUE = new Color(74, 142, 255);
    private static final Color TEXT_PRIMARY = new Color(243, 247, 255);
    private static final Color TEXT_SECONDARY = new Color(140, 160, 190);
    private static final Color TEXT_MUTED = new Color(100, 120, 150);
    private static final Color GREEN_STATUS = new Color(46, 204, 113);
    private static final Color YELLOW_STATUS = new Color(241, 196, 15);
    private static final Color RED_STATUS = new Color(231, 76, 60);
    private static final Color DANGER_RED = new Color(231, 76, 60);
    private static final Color CARD_BG = new Color(15, 25, 45);
    private static final Color CARD_BORDER = new Color(50, 60, 80);
    
    private static JPanel mainContent;
    private static JPanel gridPanel;
    private static JLabel availableCount;
    private static JLabel maintenanceCount;
    private static JLabel rentedCount;
    private static JPanel availableNamesPanel;
    private static JPanel maintenanceNamesPanel;
    private static JPanel rentedNamesPanel;
    private static JFrame parentFrame;
    private static JScrollPane availableScroll;
    private static JScrollPane maintenanceScroll;
    private static JScrollPane rentedScroll;
    private static JPanel cardsPanel;
    
    private static JLabel imagePreviewLabel;
    private static JLabel fileNameLabel;
    private static File selectedImageFile = null;
    private static String currentSearchText = "";
    private static String currentFilterType = "All Types";
    
    static JPanel create() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        
        // ===== LEFT SIDEBAR - Fixed 100px =====
        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(100, -1));
        sidebar.setMinimumSize(new Dimension(100, -1));
        sidebar.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        
        // ===== CENTER CONTENT - Expands =====
        mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        mainContent.add(topBar, BorderLayout.NORTH);
        
        gridPanel = new JPanel(new BorderLayout());
        gridPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        // ===== RIGHT PANEL - Fixed 280px =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 0, 20, 20));
        rightPanel.setPreferredSize(new Dimension(280, -1));
        rightPanel.setMinimumSize(new Dimension(280, -1));
        rightPanel.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        
        JPanel dashboard = createDashboard();
        dashboard.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(dashboard);
        rightPanel.add(Box.createVerticalGlue());
        
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(mainContent, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        
        refreshBikeGrid();
        
        return panel;
    }
    
    public static JButton createFabButton(JFrame frame) {
        parentFrame = frame;
        JButton fabButton = new JButton("+");
        fabButton.setFont(new Font("Segoe UI", Font.BOLD, 32));
        fabButton.setForeground(Color.WHITE);
        fabButton.setBackground(ACCENT_BLUE);
        fabButton.setFocusPainted(false);
        fabButton.setBorderPainted(false);
        fabButton.setContentAreaFilled(false);
        fabButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fabButton.setPreferredSize(new Dimension(56, 56));
        fabButton.setSize(new Dimension(56, 56));
        fabButton.addActionListener(e -> showAddBikeDialog());
        
        fabButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(c.getWidth(), c.getHeight());
                int x = (c.getWidth() - size) / 2;
                int y = (c.getHeight() - size) / 2;
                
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillOval(x + 2, y + 3, size - 4, size - 4);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(74, 142, 255), 
                                                      size, size, new Color(123, 179, 255));
                g2.setPaint(gp);
                g2.fillOval(x, y, size, size);
                
                ButtonModel model = ((JButton) c).getModel();
                if (model.isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillOval(x, y, size, size);
                }
                
                if (model.isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillOval(x, y, size, size);
                }
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String plus = "+";
                int textWidth = fm.stringWidth(plus);
                int textHeight = fm.getAscent();
                int textX = x + (size - textWidth) / 2;
                int textY = y + (size + textHeight) / 2 - fm.getDescent() - 1;
                g2.drawString(plus, textX, textY);
                
                g2.dispose();
            }
        });
        
        return fabButton;
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
        
        // HOME (active)
        sidebar.add(createNavItem("🏠", "HOME", true, () -> {}));
        sidebar.add(Box.createVerticalStrut(18));
        
        // MAINTENANCE (inactive)
        sidebar.add(createNavItem("🔧", "MAINTENANCE", false, () -> {
            Main.showMaintenancePage();
        }));
        sidebar.add(Box.createVerticalStrut(18));
        
        // DAILY REPORT (inactive)
       sidebar.add(createNavItem("📊", "DAILY REPORT", false, () -> {
        Main.showDailyReportPage();
     }));
        
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
    JPanel topBar = new JPanel(new BorderLayout(15, 0));
    topBar.setOpaque(false);
    topBar.setBorder(new EmptyBorder(0, 0, 20, 0));
    
    JPanel searchPanel = new JPanel(new BorderLayout());
    searchPanel.setBackground(new Color(20, 30, 50));
    searchPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
        new EmptyBorder(10, 16, 10, 16)
    ));
    
    JLabel searchIcon = new JLabel("🔍");
    searchIcon.setForeground(TEXT_MUTED);
    searchIcon.setBorder(new EmptyBorder(0, 8, 0, 8));
    
    JTextField searchField = new JTextField();
    searchField.setForeground(TEXT_PRIMARY);
    searchField.setBackground(new Color(20, 30, 50));
    searchField.setBorder(null);
    searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    searchField.setCaretColor(TEXT_PRIMARY);
    searchField.putClientProperty("JTextField.placeholderText", "Search bike name...");
    
    searchField.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            filterBikes(searchField.getText());
        }
    });
    
    searchPanel.add(searchIcon, BorderLayout.WEST);
    searchPanel.add(searchField, BorderLayout.CENTER);
    
    String[] types = {"All Types", "Mountain", "Road", "Hybrid", "Electric", "BMX"};
    JComboBox<String> filterBox = new JComboBox<>(types);
    filterBox.setBackground(new Color(20, 30, 50));
    filterBox.setForeground(TEXT_PRIMARY);
    filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    filterBox.setPreferredSize(new Dimension(140, 42));
    filterBox.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
        new EmptyBorder(0, 12, 0, 12)
    ));
    
    filterBox.addActionListener(e -> {
        filterBikesByType((String) filterBox.getSelectedItem(), searchField.getText());
    });
    
    JButton rentalBtn = new JButton("🚲 New Rental");
    rentalBtn.setBackground(ACCENT_BLUE);
    rentalBtn.setForeground(Color.WHITE);
    rentalBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    rentalBtn.setFocusPainted(false);
    rentalBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    rentalBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    rentalBtn.addActionListener(e -> {
        RentalDialog dialog = new RentalDialog(parentFrame, () -> refreshBikeGrid());
        dialog.setVisible(true);
    });
    
    JPanel rightPanelFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    rightPanelFilter.setOpaque(false);
    rightPanelFilter.add(filterBox);
    rightPanelFilter.add(rentalBtn);
    
    topBar.add(searchPanel, BorderLayout.CENTER);
    topBar.add(rightPanelFilter, BorderLayout.EAST);
    
    return topBar;
  }


    private static JPanel createDashboard() {
        JPanel dashboard = new JPanel();
        dashboard.setBackground(new Color(7, 18, 38, 200));
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 15), 1),
            new EmptyBorder(20, 18, 20, 18)
        ));
        
        JLabel title = new JLabel("AVAILABILITY DASHBOARD");
        title.setForeground(new Color(255, 255, 255, 150));
        title.setFont(new Font("Segoe UI", Font.BOLD, 10));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboard.add(title);
        dashboard.add(Box.createVerticalStrut(16));
        
        // Initialize panels
        availableNamesPanel = new JPanel();
        availableNamesPanel.setLayout(new BoxLayout(availableNamesPanel, BoxLayout.Y_AXIS));
        availableNamesPanel.setOpaque(false);
        availableNamesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        availableScroll = new JScrollPane(availableNamesPanel);
        availableScroll.setOpaque(false);
        availableScroll.getViewport().setOpaque(false);
        availableScroll.setBorder(null);
        availableScroll.setPreferredSize(new Dimension(240, 70));
        availableScroll.setMaximumSize(new Dimension(240, 70));
        availableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        availableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        availableScroll.getVerticalScrollBar().setUnitIncrement(12);

        maintenanceNamesPanel = new JPanel();
        maintenanceNamesPanel.setLayout(new BoxLayout(maintenanceNamesPanel, BoxLayout.Y_AXIS));
        maintenanceNamesPanel.setOpaque(false);
        maintenanceNamesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        maintenanceScroll = new JScrollPane(maintenanceNamesPanel);
        maintenanceScroll.setOpaque(false);
        maintenanceScroll.getViewport().setOpaque(false);
        maintenanceScroll.setBorder(null);
        maintenanceScroll.setPreferredSize(new Dimension(240, 70));
        maintenanceScroll.setMaximumSize(new Dimension(240, 70));
        maintenanceScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        maintenanceScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        maintenanceScroll.getVerticalScrollBar().setUnitIncrement(12);

        rentedNamesPanel = new JPanel();
        rentedNamesPanel.setLayout(new BoxLayout(rentedNamesPanel, BoxLayout.Y_AXIS));
        rentedNamesPanel.setOpaque(false);
        rentedNamesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rentedScroll = new JScrollPane(rentedNamesPanel);
        rentedScroll.setOpaque(false);
        rentedScroll.getViewport().setOpaque(false);
        rentedScroll.setBorder(null);
        rentedScroll.setPreferredSize(new Dimension(240, 70));
        rentedScroll.setMaximumSize(new Dimension(240, 70));
        rentedScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rentedScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        rentedScroll.getVerticalScrollBar().setUnitIncrement(12);

        // Available Section
        JPanel availableLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        availableLabelPanel.setOpaque(false);
        availableLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel greenDot = createDot(GREEN_STATUS);
        JLabel availableLabel = new JLabel("Available");
        availableLabel.setForeground(TEXT_SECONDARY);
        availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availableLabelPanel.add(greenDot);
        availableLabelPanel.add(availableLabel);
        dashboard.add(availableLabelPanel);
        
        availableCount = new JLabel("0");
        availableCount.setForeground(Color.WHITE);
        availableCount.setFont(new Font("Segoe UI", Font.BOLD, 26));
        availableCount.setAlignmentX(Component.LEFT_ALIGNMENT);
        availableCount.setBorder(new EmptyBorder(2, 0, 4, 0));
        dashboard.add(availableCount);
        dashboard.add(availableScroll);
        dashboard.add(Box.createVerticalStrut(12));

        // Maintenance Section
        JPanel maintenanceLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        maintenanceLabelPanel.setOpaque(false);
        maintenanceLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel yellowDot = createDot(YELLOW_STATUS);
        JLabel maintenanceLabel = new JLabel("Under Maintenance");
        maintenanceLabel.setForeground(TEXT_SECONDARY);
        maintenanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        maintenanceLabelPanel.add(yellowDot);
        maintenanceLabelPanel.add(maintenanceLabel);
        dashboard.add(maintenanceLabelPanel);
        
        maintenanceCount = new JLabel("0");
        maintenanceCount.setForeground(Color.WHITE);
        maintenanceCount.setFont(new Font("Segoe UI", Font.BOLD, 26));
        maintenanceCount.setAlignmentX(Component.LEFT_ALIGNMENT);
        maintenanceCount.setBorder(new EmptyBorder(2, 0, 4, 0));
        dashboard.add(maintenanceCount);
        dashboard.add(maintenanceScroll);
        dashboard.add(Box.createVerticalStrut(12));

        // Rented Section
        JPanel rentedLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rentedLabelPanel.setOpaque(false);
        rentedLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel redDot = createDot(RED_STATUS);
        JLabel rentedLabel = new JLabel("Rented");
        rentedLabel.setForeground(TEXT_SECONDARY);
        rentedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rentedLabelPanel.add(redDot);
        rentedLabelPanel.add(rentedLabel);
        dashboard.add(rentedLabelPanel);
        
        rentedCount = new JLabel("0");
        rentedCount.setForeground(Color.WHITE);
        rentedCount.setFont(new Font("Segoe UI", Font.BOLD, 26));
        rentedCount.setAlignmentX(Component.LEFT_ALIGNMENT);
        rentedCount.setBorder(new EmptyBorder(2, 0, 4, 0));
        dashboard.add(rentedCount);
        dashboard.add(rentedScroll);

        return dashboard;
    }
    
    private static JPanel createDot(Color color) {
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 10, 10);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawOval(0, 0, 9, 9);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setOpaque(false);
        return dot;
    }
    
    private static void refreshBikeGrid() {
        gridPanel.removeAll();
        List<Bike> bikes = BikeDAO.getAllBikes();
        System.out.println("Refreshing grid - Found " + bikes.size() + " bikes");
        
        if (bikes.isEmpty()) {
            JPanel emptyWrapper = new JPanel(new BorderLayout());
            emptyWrapper.setOpaque(false);
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("No bikes yet. Click the + button to add your first bike.");
            emptyLabel.setForeground(TEXT_MUTED);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyPanel.add(emptyLabel);
            emptyWrapper.add(emptyPanel, BorderLayout.CENTER);
            gridPanel.add(emptyWrapper);
        } else {
            Map<String, List<Bike>> groupedBikes = bikes.stream()
                .collect(Collectors.groupingBy(Bike::getType));
            
            String[] typeOrder = {"Mountain", "Road", "Hybrid", "Electric", "BMX"};
            
            JPanel contentWrapper = new JPanel();
            contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
            contentWrapper.setOpaque(false);
            contentWrapper.setAlignmentY(Component.TOP_ALIGNMENT);
            contentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            for (String type : typeOrder) {
                List<Bike> typeBikes = groupedBikes.get(type);
                if (typeBikes != null && !typeBikes.isEmpty()) {
                    JPanel headerPanel = new JPanel(new BorderLayout());
                    headerPanel.setOpaque(false);
                    headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
                    headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    JLabel typeLabel = new JLabel(type + " Bikes");
                    typeLabel.setForeground(TEXT_PRIMARY);
                    typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    
                    JLabel countLabel = new JLabel(typeBikes.size() + " bike" + (typeBikes.size() != 1 ? "s" : ""));
                    countLabel.setForeground(TEXT_MUTED);
                    countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    
                    headerPanel.add(typeLabel, BorderLayout.WEST);
                    headerPanel.add(countLabel, BorderLayout.EAST);
                    
                    contentWrapper.add(headerPanel);
                    
                    cardsPanel = new JPanel(new ResponsiveGridLayout(3, 16, 12));
                    cardsPanel.setOpaque(false);
                    cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    for (Bike bike : typeBikes) {
                        cardsPanel.add(createBikeCard(bike));
                    }
                    
                    contentWrapper.add(cardsPanel);
                    contentWrapper.add(Box.createVerticalStrut(8));
                }
            }
            
            JPanel topAlignWrapper = new JPanel(new BorderLayout());
            topAlignWrapper.setOpaque(false);
            topAlignWrapper.add(contentWrapper, BorderLayout.NORTH);
            gridPanel.add(topAlignWrapper);
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
        updateDashboard();
    }

    private static void filterBikes(String searchText) {
    currentSearchText = searchText.toLowerCase();
    applyFilter();
}

private static void filterBikesByType(String type, String searchText) {
    currentFilterType = type;
    currentSearchText = searchText.toLowerCase();
    applyFilter();
}

private static void applyFilter() {
    gridPanel.removeAll();
    List<Bike> allBikes = BikeDAO.getAllBikes();
    
    List<Bike> filteredBikes = allBikes.stream()
        .filter(bike -> {
            if (!currentFilterType.equals("All Types") && !bike.getType().equals(currentFilterType)) {
                return false;
            }
            if (!currentSearchText.isEmpty() && !bike.getName().toLowerCase().contains(currentSearchText)) {
                return false;
            }
            return true;
        })
        .collect(Collectors.toList());
    
    if (filteredBikes.isEmpty()) {
        JPanel emptyWrapper = new JPanel(new BorderLayout());
        emptyWrapper.setOpaque(false);
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);
        JLabel emptyLabel = new JLabel("No bikes match your search.");
        emptyLabel.setForeground(TEXT_MUTED);
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyPanel.add(emptyLabel);
        emptyWrapper.add(emptyPanel, BorderLayout.CENTER);
        gridPanel.add(emptyWrapper);
    } else {
        Map<String, List<Bike>> groupedBikes = filteredBikes.stream()
            .collect(Collectors.groupingBy(Bike::getType));
        
        String[] typeOrder = {"Mountain", "Road", "Hybrid", "Electric", "BMX"};
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.setAlignmentY(Component.TOP_ALIGNMENT);
        contentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (String type : typeOrder) {
            List<Bike> typeBikes = groupedBikes.get(type);
            if (typeBikes != null && !typeBikes.isEmpty()) {
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setOpaque(false);
                headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
                headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel typeLabel = new JLabel(type + " Bikes");
                typeLabel.setForeground(TEXT_PRIMARY);
                typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                
                JLabel countLabel = new JLabel(typeBikes.size() + " bike" + (typeBikes.size() != 1 ? "s" : ""));
                countLabel.setForeground(TEXT_MUTED);
                countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                headerPanel.add(typeLabel, BorderLayout.WEST);
                headerPanel.add(countLabel, BorderLayout.EAST);
                
                contentWrapper.add(headerPanel);
                
                cardsPanel = new JPanel(new ResponsiveGridLayout(3, 16, 12));
                cardsPanel.setOpaque(false);
                cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                for (Bike bike : typeBikes) {
                    cardsPanel.add(createBikeCard(bike));
                }
                
                contentWrapper.add(cardsPanel);
                contentWrapper.add(Box.createVerticalStrut(8));
            }
        }
        
        JPanel topAlignWrapper = new JPanel(new BorderLayout());
        topAlignWrapper.setOpaque(false);
        topAlignWrapper.add(contentWrapper, BorderLayout.NORTH);
        gridPanel.add(topAlignWrapper);
    }
    
    gridPanel.revalidate();
    gridPanel.repaint();
    updateDashboard();
}
    
    private static JPanel createBikeCard(Bike bike) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status dot
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        statusBar.setOpaque(false);
        JPanel statusDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color statusColor = switch (bike.getStatus().toLowerCase()) {
                    case "available" -> GREEN_STATUS;
                    case "maintenance" -> YELLOW_STATUS;
                    case "rented" -> RED_STATUS;
                    default -> TEXT_MUTED;
                };
                g2.setColor(statusColor);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(8, 8));
        statusDot.setOpaque(false);
        statusBar.add(statusDot);
        card.add(statusBar);
        
        // Square image panel
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 35, 55));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setLayout(new GridBagLayout());
        imagePanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        
        if (bike.getImagePath() != null && !bike.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(bike.getImagePath());
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img != null) {
                        int size = Math.min(img.getWidth(), img.getHeight());
                        int x = (img.getWidth() - size) / 2;
                        int y = (img.getHeight() - size) / 2;
                        BufferedImage cropped = img.getSubimage(x, y, size, size);
                        Image scaled = cropped.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                        JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                        imagePanel.add(imgLabel);
                    } else {
                        JLabel icon = new JLabel("🚲");
                        icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
                        imagePanel.add(icon);
                    }
                } else {
                    JLabel icon = new JLabel("🚲");
                    icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
                    imagePanel.add(icon);
                }
            } catch (Exception e) {
                JLabel icon = new JLabel("🚲");
                icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
                imagePanel.add(icon);
            }
        } else {
            JLabel icon = new JLabel("🚲");
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            imagePanel.add(icon);
        }
        
        JLabel nameLabel = new JLabel(bike.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(bike.getType().toUpperCase());
        typeLabel.setForeground(TEXT_MUTED);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(Box.createVerticalStrut(4));
        card.add(imagePanel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(typeLabel);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showBikeDetails(bike);
            }
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(11, 11, 11, 11),
                    BorderFactory.createLineBorder(new Color(74, 142, 255, 80), 1)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(12, 12, 12, 12));
            }
        });
        
        return card;
    }
    
    private static void updateDashboard() {
        List<Bike> bikes = BikeDAO.getAllBikes();
        
        long available = bikes.stream().filter(b -> "Available".equalsIgnoreCase(b.getStatus())).count();
        long maintenance = bikes.stream().filter(b -> "Maintenance".equalsIgnoreCase(b.getStatus())).count();
        long rented = bikes.stream().filter(b -> "Rented".equalsIgnoreCase(b.getStatus())).count();
        
        availableCount.setText(String.valueOf(available));
        maintenanceCount.setText(String.valueOf(maintenance));
        rentedCount.setText(String.valueOf(rented));
        
        availableNamesPanel.removeAll();
        bikes.stream()
            .filter(b -> "Available".equalsIgnoreCase(b.getStatus()))
            .map(Bike::getName)
            .forEach(name -> {
                JLabel nameLabel = new JLabel("• " + name);
                nameLabel.setForeground(TEXT_MUTED);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                availableNamesPanel.add(nameLabel);
            });
        if (available == 0) {
            JLabel noneLabel = new JLabel("• None");
            noneLabel.setForeground(TEXT_MUTED);
            noneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            availableNamesPanel.add(noneLabel);
        }
        
        maintenanceNamesPanel.removeAll();
        bikes.stream()
            .filter(b -> "Maintenance".equalsIgnoreCase(b.getStatus()))
            .map(Bike::getName)
            .forEach(name -> {
                JLabel nameLabel = new JLabel("• " + name);
                nameLabel.setForeground(TEXT_MUTED);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                maintenanceNamesPanel.add(nameLabel);
            });
        if (maintenance == 0) {
            JLabel noneLabel = new JLabel("• None");
            noneLabel.setForeground(TEXT_MUTED);
            noneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            maintenanceNamesPanel.add(noneLabel);
        }
        
        rentedNamesPanel.removeAll();
        bikes.stream()
            .filter(b -> "Rented".equalsIgnoreCase(b.getStatus()))
            .map(Bike::getName)
            .forEach(name -> {
                JLabel nameLabel = new JLabel("• " + name);
                nameLabel.setForeground(TEXT_MUTED);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                rentedNamesPanel.add(nameLabel);
            });
        if (rented == 0) {
            JLabel noneLabel = new JLabel("• None");
            noneLabel.setForeground(TEXT_MUTED);
            noneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            rentedNamesPanel.add(noneLabel);
        }
        
        availableNamesPanel.revalidate();
        availableNamesPanel.repaint();
        maintenanceNamesPanel.revalidate();
        maintenanceNamesPanel.repaint();
        rentedNamesPanel.revalidate();
        rentedNamesPanel.repaint();
    }
    
    private static void showAddBikeDialog() {
        JDialog dialog = new JDialog(parentFrame, "Add New Bike", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 680);
        dialog.setMinimumSize(new Dimension(450, 600));
        dialog.setLocationRelativeTo(parentFrame);
        dialog.getContentPane().setBackground(new Color(7, 18, 38));
        
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 28, 28, 28));
        form.setBackground(new Color(7, 18, 38));
        
        selectedImageFile = null;
        
        // Bike Name
        JLabel nameLabel = new JLabel("Bike Name");
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel requiredStar1 = new JLabel("*");
        requiredStar1.setForeground(DANGER_RED);
        requiredStar1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel nameHeader = new JPanel(new BorderLayout());
        nameHeader.setOpaque(false);
        nameHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameHeader.add(nameLabel, BorderLayout.WEST);
        nameHeader.add(requiredStar1, BorderLayout.EAST);
        
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        styleTextField(nameField);
        nameField.putClientProperty("JTextField.placeholderText", "e.g., Mountain Pro");
        
        // Bike Type
        JLabel typeLabel = new JLabel("Bike Type");
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel requiredStar2 = new JLabel("*");
        requiredStar2.setForeground(DANGER_RED);
        requiredStar2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel typeHeader = new JPanel(new BorderLayout());
        typeHeader.setOpaque(false);
        typeHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeHeader.add(typeLabel, BorderLayout.WEST);
        typeHeader.add(requiredStar2, BorderLayout.EAST);
        
        String[] types = {"Select type...", "Mountain", "Road", "Hybrid", "Electric", "BMX"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        typeBox.setBackground(new Color(20, 30, 50));
        typeBox.setForeground(TEXT_PRIMARY);
        typeBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Description
        JLabel descLabel = new JLabel("Description");
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBackground(new Color(20, 30, 50));
        descArea.setForeground(TEXT_PRIMARY);
        descArea.setCaretColor(TEXT_PRIMARY);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        descArea.setText("Describe the bike...");
        descArea.setForeground(TEXT_MUTED);
        descArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (descArea.getText().equals("Describe the bike...")) {
                    descArea.setText("");
                    descArea.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (descArea.getText().isEmpty()) {
                    descArea.setText("Describe the bike...");
                    descArea.setForeground(TEXT_MUTED);
                }
            }
        });
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descScroll.setBackground(new Color(20, 30, 50));
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        
        // Bike Image
        JLabel imageLabel = new JLabel("Bike Image");
        imageLabel.setForeground(TEXT_SECONDARY);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel requiredStar3 = new JLabel("*");
        requiredStar3.setForeground(DANGER_RED);
        requiredStar3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel imageHeader = new JPanel(new BorderLayout());
        imageHeader.setOpaque(false);
        imageHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageHeader.add(imageLabel, BorderLayout.WEST);
        imageHeader.add(requiredStar3, BorderLayout.EAST);
        
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filePanel.setOpaque(false);
        filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton chooseFileBtn = new JButton("Choose File");
        chooseFileBtn.setBackground(new Color(74, 142, 255, 40));
        chooseFileBtn.setForeground(ACCENT_BLUE);
        chooseFileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chooseFileBtn.setFocusPainted(false);
        chooseFileBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 142, 255, 80), 1),
            new EmptyBorder(10, 18, 10, 18)
        ));
        chooseFileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        fileNameLabel = new JLabel("No file chosen");
        fileNameLabel.setForeground(TEXT_MUTED);
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fileNameLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
        
        filePanel.add(chooseFileBtn);
        filePanel.add(fileNameLabel);
        
        // Square image preview
        JPanel previewWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        previewWrapper.setOpaque(false);
        previewWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        imagePreviewLabel = new JLabel("🚲") {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 140);
            }
        };
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        imagePreviewLabel.setForeground(TEXT_MUTED);
        imagePreviewLabel.setBackground(new Color(20, 30, 50));
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90), 1));
        
        previewWrapper.add(imagePreviewLabel);
        
        chooseFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                fileNameLabel.setText(selectedImageFile.getName());
                
                try {
                    BufferedImage img = ImageIO.read(selectedImageFile);
                    if (img != null) {
                        int size = Math.min(img.getWidth(), img.getHeight());
                        int x = (img.getWidth() - size) / 2;
                        int y = (img.getHeight() - size) / 2;
                        BufferedImage cropped = img.getSubimage(x, y, size, size);
                        Image scaled = cropped.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                        imagePreviewLabel.setIcon(new ImageIcon(scaled));
                        imagePreviewLabel.setText("");
                    }
                } catch (Exception ex) {
                    imagePreviewLabel.setIcon(null);
                    imagePreviewLabel.setText("🚲");
                }
            }
        });
        
        form.add(nameHeader);
        form.add(Box.createVerticalStrut(6));
        form.add(nameField);
        form.add(Box.createVerticalStrut(16));
        
        form.add(typeHeader);
        form.add(Box.createVerticalStrut(6));
        form.add(typeBox);
        form.add(Box.createVerticalStrut(16));
        
        form.add(descLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(descScroll);
        form.add(Box.createVerticalStrut(16));
        
        form.add(imageHeader);
        form.add(Box.createVerticalStrut(6));
        form.add(filePanel);
        form.add(Box.createVerticalStrut(10));
        form.add(previewWrapper);
        form.add(Box.createVerticalStrut(24));
        
        JPanel buttonPanel = new JPanel(new BorderLayout(12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(60, 70, 90));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton addBtn = new JButton("Add Bike");
        addBtn.setBackground(ACCENT_BLUE);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            String desc = descArea.getText().trim();
            if (desc.equals("Describe the bike...")) desc = "";
            
            boolean hasError = false;
            
            if (name.isEmpty()) {
                nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_RED, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                hasError = true;
            } else {
                styleTextField(nameField);
            }
            
            if (type == null || type.equals("Select type...")) {
                typeBox.setBorder(BorderFactory.createLineBorder(DANGER_RED, 2));
                hasError = true;
            } else {
                typeBox.setBorder(BorderFactory.createEmptyBorder());
            }
            
            if (selectedImageFile == null) {
                imagePreviewLabel.setBorder(BorderFactory.createLineBorder(DANGER_RED, 2));
                hasError = true;
            } else {
                imagePreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90), 1));
            }
            
            if (hasError) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Bike bike = new Bike();
            bike.setName(name);
            bike.setType(type);
            bike.setDescription(desc);
            bike.setStatus("Available");
            bike.setImagePath(selectedImageFile.getAbsolutePath());
            
            if (BikeDAO.addBike(bike)) {
                dialog.dispose();
                refreshBikeGrid();
                JOptionPane.showMessageDialog(mainContent, "✅ Bike added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Failed to add bike!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn, BorderLayout.WEST);
        buttonPanel.add(addBtn, BorderLayout.EAST);
        
        form.add(buttonPanel);
        
        dialog.add(form);
        dialog.setVisible(true);
    }
    
    private static void styleTextField(JTextField field) {
        field.setBackground(new Color(20, 30, 50));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
    }
    
    private static void showBikeDetails(Bike bike) {
        JDialog dialog = new JDialog(parentFrame, "Bike Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 480);
        dialog.setMinimumSize(new Dimension(550, 400));
        dialog.setLocationRelativeTo(mainContent);
        dialog.getContentPane().setBackground(new Color(7, 18, 38));
        
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(7, 18, 38));
        
        // Left - Square Image
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(CARD_BG);
        imagePanel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        imagePanel.setPreferredSize(new Dimension(260, 260));
        
        if (bike.getImagePath() != null && !bike.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(bike.getImagePath());
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img != null) {
                        int size = Math.min(img.getWidth(), img.getHeight());
                        int x = (img.getWidth() - size) / 2;
                        int y = (img.getHeight() - size) / 2;
                        BufferedImage cropped = img.getSubimage(x, y, size, size);
                        Image scaled = cropped.getScaledInstance(240, 240, Image.SCALE_SMOOTH);
                        JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                        imagePanel.add(imgLabel);
                    } else {
                        JLabel icon = new JLabel("🚲");
                        icon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
                        imagePanel.add(icon);
                    }
                } else {
                    JLabel icon = new JLabel("🚲");
                    icon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
                    imagePanel.add(icon);
                }
            } catch (Exception e) {
                JLabel icon = new JLabel("🚲");
                icon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
                imagePanel.add(icon);
            }
        } else {
            JLabel icon = new JLabel("🚲");
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
            imagePanel.add(icon);
        }
        
        // Right - Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(bike.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(20));
        
        // Info card (compact, single card)
        JPanel infoCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(new EmptyBorder(16, 18, 16, 18));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Type row
        JPanel typeRow = new JPanel(new BorderLayout(10, 0));
        typeRow.setOpaque(false);
        JLabel typeTitle = new JLabel("Bike Type");
        typeTitle.setForeground(TEXT_SECONDARY);
        typeTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeTitle.setPreferredSize(new Dimension(85, 22));
        JLabel typeValue = new JLabel(bike.getType());
        typeValue.setForeground(Color.WHITE);
        typeValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeRow.add(typeTitle, BorderLayout.WEST);
        typeRow.add(typeValue, BorderLayout.CENTER);
        infoCard.add(typeRow);
        infoCard.add(Box.createVerticalStrut(8));
        
        // Status row
        Color statusColor = switch (bike.getStatus().toLowerCase()) {
            case "available" -> GREEN_STATUS;
            case "maintenance" -> YELLOW_STATUS;
            case "rented" -> RED_STATUS;
            default -> TEXT_MUTED;
        };
        JPanel statusRow = new JPanel(new BorderLayout(10, 0));
        statusRow.setOpaque(false);
        JLabel statusTitle = new JLabel("Status");
        statusTitle.setForeground(TEXT_SECONDARY);
        statusTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusTitle.setPreferredSize(new Dimension(85, 22));
        
        JPanel statusValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusValuePanel.setOpaque(false);
        JPanel statusDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(statusColor);
                g2.fillOval(0, 3, 9, 9);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(12, 16));
        statusDot.setOpaque(false);
        JLabel statusText = new JLabel(bike.getStatus());
        statusText.setForeground(statusColor);
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusValuePanel.add(statusDot);
        statusValuePanel.add(statusText);
        
        statusRow.add(statusTitle, BorderLayout.WEST);
        statusRow.add(statusValuePanel, BorderLayout.CENTER);
        infoCard.add(statusRow);
        infoCard.add(Box.createVerticalStrut(8));
        
        // Description row
        String descText = (bike.getDescription() != null && !bike.getDescription().isEmpty()) ? bike.getDescription() : "No description";
        JPanel descRow = new JPanel(new BorderLayout(10, 0));
        descRow.setOpaque(false);
        JLabel descTitle = new JLabel("Description");
        descTitle.setForeground(TEXT_SECONDARY);
        descTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descTitle.setPreferredSize(new Dimension(85, 22));
        JTextArea descValue = new JTextArea(descText);
        descValue.setEditable(false);
        descValue.setOpaque(false);
        descValue.setForeground(TEXT_PRIMARY);
        descValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descValue.setLineWrap(true);
        descValue.setWrapStyleWord(true);
        descRow.add(descTitle, BorderLayout.WEST);
        descRow.add(descValue, BorderLayout.CENTER);
        infoCard.add(descRow);
        
        detailsPanel.add(infoCard);
        detailsPanel.add(Box.createVerticalStrut(20));
        
        // Action buttons
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel statusButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusButtonsPanel.setOpaque(false);
        
        JButton availableBtn = createStatusButton("Available", GREEN_STATUS);
        JButton maintenanceBtn = createStatusButton("Maintenance", YELLOW_STATUS);
        JButton rentedBtn = createStatusButton("Rented", RED_STATUS);
        
        availableBtn.addActionListener(e -> {
            BikeDAO.updateBikeStatus(bike.getId(), "Available");
            dialog.dispose();
            refreshBikeGrid();
        });
        maintenanceBtn.addActionListener(e -> {
            BikeDAO.updateBikeStatus(bike.getId(), "Maintenance");
            dialog.dispose();
            refreshBikeGrid();
        });
        rentedBtn.addActionListener(e -> {
            BikeDAO.updateBikeStatus(bike.getId(), "Rented");
            dialog.dispose();
            refreshBikeGrid();
        });
        
        statusButtonsPanel.add(availableBtn);
        statusButtonsPanel.add(maintenanceBtn);
        statusButtonsPanel.add(rentedBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(RED_STATUS);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            dialog.dispose();
            int confirm = JOptionPane.showConfirmDialog(mainContent, "Delete this bike?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                BikeDAO.deleteBike(bike.getId());
                refreshBikeGrid();
            }
        });
        
        actionPanel.add(statusButtonsPanel, BorderLayout.WEST);
        actionPanel.add(deleteBtn, BorderLayout.EAST);
        
        detailsPanel.add(actionPanel);
        
        content.add(imagePanel, BorderLayout.WEST);
        content.add(detailsPanel, BorderLayout.CENTER);
        
        dialog.add(content);
        dialog.setVisible(true);
    }
    
    private static JButton createStatusButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        button.setForeground(color);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            }
        });
        
        return button;
    }
    
    // Responsive grid layout
    private static class ResponsiveGridLayout implements LayoutManager {
        private int minColumns;
        private int hgap;
        private int vgap;
        
        public ResponsiveGridLayout(int minColumns, int hgap, int vgap) {
            this.minColumns = minColumns;
            this.hgap = hgap;
            this.vgap = vgap;
        }
        
        @Override
        public void addLayoutComponent(String name, Component comp) {}
        
        @Override
        public void removeLayoutComponent(Component comp) {}
        
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }
        
        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }
        
        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int ncomponents = parent.getComponentCount();
                if (ncomponents == 0) return new Dimension(0, 0);
                
                int parentWidth = parent.getWidth();
                if (parentWidth == 0) parentWidth = parent.getParent().getWidth();
                if (parentWidth == 0) parentWidth = 800;
                
                int availableWidth = parentWidth - 30;
                int cardWidth = 180;
                
                int columns = Math.max(minColumns, availableWidth / (cardWidth + hgap));
                
                int rows = (int) Math.ceil((double) ncomponents / columns);
                
                int width = columns * cardWidth + (columns - 1) * hgap;
                int height = rows * (cardWidth + 80) + (rows - 1) * vgap;
                
                return new Dimension(width, height);
            }
        }
        
        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int ncomponents = parent.getComponentCount();
                if (ncomponents == 0) return;
                
                int parentWidth = parent.getWidth();
                if (parentWidth == 0) parentWidth = parent.getParent().getWidth();
                
                int availableWidth = parentWidth - insets.left - insets.right;
                int cardWidth = 180;
                
                int columns = Math.max(minColumns, availableWidth / (cardWidth + hgap));
                int cardHeight = cardWidth + 80;
                
                int startX = insets.left;
                int x = startX;
                int y = insets.top;
                int col = 0;
                
                for (int i = 0; i < ncomponents; i++) {
                    Component comp = parent.getComponent(i);
                    if (comp.isVisible()) {
                        comp.setSize(cardWidth, cardHeight);
                        comp.setLocation(x, y);
                        
                        col++;
                        x += cardWidth + hgap;
                        
                        if (col >= columns) {
                            col = 0;
                            x = startX;
                            y += cardHeight + vgap;
                        }
                    }
                }
            }
        }
    }
}