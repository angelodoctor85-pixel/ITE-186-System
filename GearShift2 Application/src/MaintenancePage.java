import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

class MaintenancePage {
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
    
    static JPanel create(JFrame frame) {
        parentFrame = frame;
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
        
        refreshMaintenanceGrid();
        
        return panel;
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
        
        // HOME (inactive)
        sidebar.add(createNavItem("🏠", "HOME", false, () -> {
            Main.showHomePage();
        }));
        sidebar.add(Box.createVerticalStrut(18));
        
        // MAINTENANCE (active)
        sidebar.add(createNavItem("🔧", "MAINTENANCE", true, () -> {}));
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
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 8));
        
        JTextField searchField = new JTextField("Search Bike Name");
        searchField.setForeground(TEXT_MUTED);
        searchField.setBackground(new Color(20, 30, 50));
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setCaretColor(TEXT_PRIMARY);
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        String[] types = {"All Types", "Mountain", "Road", "Hybrid", "Electric", "BMX"};
        JComboBox<String> filterBox = new JComboBox<>(types);
        filterBox.setBackground(new Color(20, 30, 50));
        filterBox.setForeground(TEXT_PRIMARY);
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterBox.setPreferredSize(new Dimension(130, 40));
        filterBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 90), 1),
            new EmptyBorder(0, 16, 0, 16)
        ));
        
        JPanel rightPanelFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanelFilter.setOpaque(false);
        rightPanelFilter.add(filterBox);
        
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
    
    private static void refreshMaintenanceGrid() {
        gridPanel.removeAll();
        List<Bike> allBikes = BikeDAO.getAllBikes();
        List<Bike> maintenanceBikes = allBikes.stream()
            .filter(b -> "Maintenance".equalsIgnoreCase(b.getStatus()))
            .collect(Collectors.toList());
        
        System.out.println("Refreshing maintenance grid - Found " + maintenanceBikes.size() + " bikes under maintenance");
        
        if (maintenanceBikes.isEmpty()) {
            JPanel emptyWrapper = new JPanel(new BorderLayout());
            emptyWrapper.setOpaque(false);
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("No bikes under maintenance.");
            emptyLabel.setForeground(TEXT_MUTED);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyPanel.add(emptyLabel);
            emptyWrapper.add(emptyPanel, BorderLayout.CENTER);
            gridPanel.add(emptyWrapper);
        } else {
            Map<String, List<Bike>> groupedBikes = maintenanceBikes.stream()
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
                g2.setColor(YELLOW_STATUS);
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
                showMaintenanceBikeDetails(bike);
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
    
    private static void showMaintenanceBikeDetails(Bike bike) {
        JDialog dialog = new JDialog(parentFrame, "Bike Details - Maintenance", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 420);
        dialog.setMinimumSize(new Dimension(500, 380));
        dialog.setLocationRelativeTo(mainContent);
        dialog.getContentPane().setBackground(new Color(7, 18, 38));
        
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(7, 18, 38));
        
        // Left - Square Image
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(CARD_BG);
        imagePanel.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        imagePanel.setPreferredSize(new Dimension(220, 220));
        
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
                        Image scaled = cropped.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                        imagePanel.add(imgLabel);
                    } else {
                        JLabel icon = new JLabel("🚲");
                        icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
                        imagePanel.add(icon);
                    }
                } else {
                    JLabel icon = new JLabel("🚲");
                    icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
                    imagePanel.add(icon);
                }
            } catch (Exception e) {
                JLabel icon = new JLabel("🚲");
                icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
                imagePanel.add(icon);
            }
        } else {
            JLabel icon = new JLabel("🚲");
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
            imagePanel.add(icon);
        }
        
        // Right - Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(bike.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(16));
        
        // Info card
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
        infoCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Type row
        JPanel typeRow = new JPanel(new BorderLayout(10, 0));
        typeRow.setOpaque(false);
        JLabel typeTitle = new JLabel("Bike Type");
        typeTitle.setForeground(TEXT_SECONDARY);
        typeTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeTitle.setPreferredSize(new Dimension(80, 22));
        JLabel typeValue = new JLabel(bike.getType());
        typeValue.setForeground(Color.WHITE);
        typeValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeRow.add(typeTitle, BorderLayout.WEST);
        typeRow.add(typeValue, BorderLayout.CENTER);
        infoCard.add(typeRow);
        infoCard.add(Box.createVerticalStrut(8));
        
        // Status row
        JPanel statusRow = new JPanel(new BorderLayout(10, 0));
        statusRow.setOpaque(false);
        JLabel statusTitle = new JLabel("Status");
        statusTitle.setForeground(TEXT_SECONDARY);
        statusTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusTitle.setPreferredSize(new Dimension(80, 22));
        
        JPanel statusValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusValuePanel.setOpaque(false);
        JPanel statusDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(YELLOW_STATUS);
                g2.fillOval(0, 3, 9, 9);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(12, 16));
        statusDot.setOpaque(false);
        JLabel statusText = new JLabel("Maintenance");
        statusText.setForeground(YELLOW_STATUS);
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
        descTitle.setPreferredSize(new Dimension(80, 22));
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
        
        // Status buttons ONLY (no Delete button)
        JPanel statusButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusButtonsPanel.setOpaque(false);
        statusButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton availableBtn = createStatusButton("Available", GREEN_STATUS);
        JButton maintenanceBtn = createStatusButton("Maintenance", YELLOW_STATUS);
        JButton rentedBtn = createStatusButton("Rented", RED_STATUS);
        
        availableBtn.addActionListener(e -> {
            BikeDAO.updateBikeStatus(bike.getId(), "Available");
            dialog.dispose();
            refreshMaintenanceGrid();
        });
        maintenanceBtn.addActionListener(e -> {
            // Already in maintenance
            dialog.dispose();
        });
        rentedBtn.addActionListener(e -> {
            BikeDAO.updateBikeStatus(bike.getId(), "Rented");
            dialog.dispose();
            refreshMaintenanceGrid();
        });
        
        statusButtonsPanel.add(availableBtn);
        statusButtonsPanel.add(maintenanceBtn);
        statusButtonsPanel.add(rentedBtn);
        
        detailsPanel.add(statusButtonsPanel);
        
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