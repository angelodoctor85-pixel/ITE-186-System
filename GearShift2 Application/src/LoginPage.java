import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.awt.GradientPaint;

class LoginPage {
    private static final String AFTER_FONT_PATH = "C:\\Users\\Gavin\\AppData\\Local\\Microsoft\\Windows\\Fonts\\after-regular.otf";
    private static final String LOGO_PATH = "images\\GearShift-Logo.png";

    static JPanel create(Runnable onLogin) {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel left = new LoginBrandPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel logo = new JLabel(loadLogoIcon(140));
        JLabel brand = new JLabel("GEARSHIFT");
        brand.setForeground(Color.WHITE);
        brand.setFont(loadFont(AFTER_FONT_PATH, Font.BOLD | Font.ITALIC, 60f));

        JLabel blurb = new JLabel("<html>Run your bike rental shop with ease. Track every bike, manage<br/>" +
                "bookings, stay on top of maintenance, and keep clear<br/>" +
                "records—all from one simple system.</html>");
        blurb.setForeground(new Color(200, 210, 228));
        blurb.setFont(new Font("Helvetica", Font.PLAIN, 13));

        JLabel footer = new JLabel("\u00A9 2026 GearShift. All rights reserved.");
        footer.setForeground(new Color(130, 145, 165));
        footer.setFont(new Font("Helvetica", Font.PLAIN, 11));

        left.add(Box.createVerticalStrut(30));
        left.add(logo);
        left.add(Box.createVerticalStrut(10));
        left.add(brand);
        left.add(Box.createVerticalStrut(20));
        left.add(Box.createVerticalStrut(30));
        left.add(blurb);
        left.add(Box.createVerticalGlue());
        left.add(footer);

        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new BorderLayout());
        right.setBorder(new EmptyBorder(36, 36, 36, 36));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(36, 0, 0, 0));

        JLabel title = new JLabel("Welcome Back!");
        title.setForeground(new Color(20, 26, 36));
        title.setFont(new Font("Helvetica", Font.BOLD, 22));

        JTextField email = underlineField("Email");
        JTextField password = underlineField("Password");

        JButton login = gradientButton("Login now");
        login.addActionListener(event -> onLogin.run());

        form.add(Box.createVerticalStrut(22));
        form.add(title);
        form.add(Box.createVerticalStrut(24));
        form.add(email);
        form.add(Box.createVerticalStrut(16));
        form.add(password);
        form.add(Box.createVerticalStrut(26));
        form.add(login);

        right.add(logoMark(), BorderLayout.NORTH);
        right.add(form, BorderLayout.CENTER);

        panel.add(left);
        panel.add(right);
        return panel;
    }

    private static JPanel logoMark() {
        JLabel logo = new JLabel("GSHIFT");
        logo.setForeground(new Color(35, 55, 85));
        logo.setFont(loadFont(AFTER_FONT_PATH, Font.BOLD | Font.ITALIC, 20f));

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(logo, BorderLayout.WEST);
        return holder;
    }

    private static JTextField underlineField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(320, 32));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(190, 198, 210)));
        field.setForeground(new Color(40, 45, 55));
        field.setFont(new Font("Helvetica", Font.PLAIN, 14));
        field.setText(placeholder);
        field.setCaretColor(new Color(40, 45, 55));
        return field;
    }

    private static JButton gradientButton(String text) {
        JButton button = new GradientButton(text);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Helvetica", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 30, 10, 30));
        return button;
    }

    private static ImageIcon loadLogoIcon(int size) {
        ImageIcon icon = new ImageIcon(LOGO_PATH);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return new ImageIcon();
        }
        float scale = Math.min(size / (float) icon.getIconWidth(), size / (float) icon.getIconHeight());
        int targetW = Math.max(1, Math.round(icon.getIconWidth() * scale));
        int targetH = Math.max(1, Math.round(icon.getIconHeight() * scale));
        Image scaled = icon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static class GradientButton extends JButton {
        private boolean hover;

        GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (hover) {
                g2.translate(w * 0.025f, h * 0.05f);
                g2.scale(1.05, 1.05);
            }
            g2.setPaint(new GradientPaint(0, 0, new Color(4, 10, 22), 0, h, new Color(8, 26, 52)));
            g2.fillRoundRect(0, 0, w, h, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static Font loadFont(String path, int style, float size) {
        try {
            Font base = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return base.deriveFont(style, size);
        } catch (FontFormatException | IOException ex) {
            return new Font("SansSerif", style, Math.round(size));
        }
    }

    private static class LoginBrandPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(4, 10, 22), 0, h, new Color(8, 26, 52)));
            g2.fillRect(0, 0, w, h);

            g2.dispose();
        }

    }
}
