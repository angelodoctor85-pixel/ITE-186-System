import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

class LandingPage {
    private static final String AFTER_FONT_PATH = "C:\\Users\\Gavin\\AppData\\Local\\Microsoft\\Windows\\Fonts\\after-regular.otf";

    static JPanel create(Runnable onGetStarted) {
        JPanel panel = new LandingGradientPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 40, 40, 40));

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        Font afterBold = loadFont(AFTER_FONT_PATH, Font.BOLD, 48f);
        Font afterBoldItalic = loadFont(AFTER_FONT_PATH, Font.BOLD | Font.ITALIC, 48f);

        JLabel welcome = new JLabel("WELCOME TO");
        welcome.setForeground(new Color(220, 230, 242));
        welcome.setFont(new Font("Helvetica", Font.BOLD, 24));
        welcome.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        JLabel gear = new JLabel("GEAR");
        gear.setForeground(new Color(248, 250, 252));
        gear.setFont(afterBold);

        JLabel shift = new JLabel("SHIFT");
        shift.setForeground(new Color(248, 250, 252));
        shift.setFont(afterBoldItalic);

        brandRow.add(gear);
        brandRow.add(shift);

        RoundedButton getStarted = primaryButton("Get started");
        getStarted.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        getStarted.addActionListener(event -> onGetStarted.run());

        column.add(Box.createVerticalStrut(4));
        column.add(welcome);
        column.add(Box.createVerticalStrut(4));
        column.add(brandRow);
        column.add(Box.createVerticalStrut(26));
        column.add(getStarted);
        column.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(column, gbc);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private static RoundedButton primaryButton(String text) {
        RoundedButton button = new RoundedButton(text, 14);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(20, 26, 36));
        button.setFont(new Font("Helvetica", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 28, 6, 28));
        button.setBaseForeground(button.getForeground());
        return button;
    }

    private static Font loadFont(String path, int style, float size) {
        try {
            Font base = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return base.deriveFont(style, size);
        } catch (FontFormatException | IOException ex) {
            return new Font("SansSerif", style, Math.round(size));
        }
    }

    private static class RoundedButton extends JButton {
        private static final int ANIM_DURATION_MS = 500;
        private final int radius;
        private float hoverProgress;
        private float startProgress;
        private float targetProgress;
        private long animationStart;
        private Color baseForeground = new Color(20, 26, 36);
        private Timer timer;

        RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    startAnimation(1f);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    startAnimation(0f);
                }
            });
        }

        void setBaseForeground(Color color) {
            baseForeground = color;
            updateForeground();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int fillAlpha = Math.round(255f * (1f - hoverProgress));
            if (fillAlpha > 0) {
                g2.setColor(new Color(255, 255, 255, fillAlpha));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            }
            if (hoverProgress > 0f) {
                int borderAlpha = Math.round(255f * hoverProgress);
                g2.setColor(new Color(255, 255, 255, borderAlpha));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius * 2, radius * 2);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        private void startAnimation(float target) {
            targetProgress = target;
            startProgress = hoverProgress;
            animationStart = System.currentTimeMillis();
            if (timer == null) {
                timer = new Timer(16, event -> tickAnimation());
            }
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        private void tickAnimation() {
            float elapsed = (System.currentTimeMillis() - animationStart) / (float) ANIM_DURATION_MS;
            if (elapsed >= 1f) {
                hoverProgress = targetProgress;
                updateForeground();
                timer.stop();
                return;
            }
            float eased = elapsed * elapsed * (3f - 2f * elapsed);
            hoverProgress = startProgress + (targetProgress - startProgress) * eased;
            updateForeground();
            repaint();
        }

        private void updateForeground() {
            int r = Math.round(baseForeground.getRed() + (255 - baseForeground.getRed()) * hoverProgress);
            int g = Math.round(baseForeground.getGreen() + (255 - baseForeground.getGreen()) * hoverProgress);
            int b = Math.round(baseForeground.getBlue() + (255 - baseForeground.getBlue()) * hoverProgress);
            setForeground(new Color(r, g, b));
        }
    }

    private static class LandingGradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(4, 10, 22), 0, h, new Color(8, 26, 52)));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 18));
            drawGear(g2, (int) (w * 0.15), (int) (h * 0.18), 36);
            drawGear(g2, (int) (w * 0.82), (int) (h * 0.22), 28);
            drawGear(g2, (int) (w * 0.74), (int) (h * 0.78), 22);

            g2.dispose();
        }

        private void drawGear(Graphics2D g2, int cx, int cy, int r) {
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.drawOval(cx - r / 2, cy - r / 2, r, r);
            int teeth = 10;
            for (int i = 0; i < teeth; i++) {
                double angle = i * (Math.PI * 2 / teeth);
                int x1 = (int) (cx + Math.cos(angle) * r);
                int y1 = (int) (cy + Math.sin(angle) * r);
                int x2 = (int) (cx + Math.cos(angle) * (r + 6));
                int y2 = (int) (cy + Math.sin(angle) * (r + 6));
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
