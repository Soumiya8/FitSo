package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * FitSo Premium Dark Gaming Design System
 */
public class Theme {

    // Modern Ultra-Dark Cyber Palette
    public static final Color BG_DARK = new Color(10, 14, 23);          // #0A0E17 Deep space background
    public static final Color PANEL_BG = new Color(18, 25, 38);        // #121926 Glassmorphic card surface
    public static final Color PANEL_BG_LIGHT = new Color(28, 38, 58);  // #1C263A Elevated surface
    public static final Color CARD_BORDER = new Color(44, 56, 80);     // #2C3850 Subtle border
    public static final Color BORDER_GLOW = new Color(0, 242, 254, 160); // #00F2FE Glowing Cyan border

    // Vibrant Electric Accents
    public static final Color CYAN_NEON = new Color(0, 242, 254);     // #00F2FE Neon Cyan
    public static final Color BLUE_ELECTRIC = new Color(79, 172, 254); // #4FACFE Electric Blue
    public static final Color GREEN_EMERALD = new Color(0, 230, 118);  // #00E676 Emerald Green
    public static final Color GOLD_GLOW = new Color(255, 215, 0);     // #FFD700 Cyber Gold
    public static final Color PURPLE_AURA = new Color(124, 77, 255);  // #7C4DFF Purple Aura
    public static final Color RED_CORAL = new Color(255, 82, 82);      // #FF5252 Coral Red

    // Aliases for Backwards Compatibility Across All Panels
    public static final Color ACCENT_BLUE = BLUE_ELECTRIC;
    public static final Color ACCENT_GREEN = GREEN_EMERALD;
    public static final Color ACCENT_GOLD = GOLD_GLOW;
    public static final Color ACCENT_RED = RED_CORAL;
    public static final Color ACCENT_PURPLE = PURPLE_AURA;

    // Typography Hierarchy
    public static final Color TEXT_PRIMARY = new Color(245, 247, 250);
    public static final Color TEXT_SECONDARY = new Color(160, 174, 192);
    public static final Color TEXT_MUTED = new Color(113, 128, 150);

    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // Setup Global Look and Feel Defaults
    public static void applyGlobalLaf() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("TabbedPane.selected", PANEL_BG_LIGHT);
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.tabsOverlapBorder", true);
            UIManager.put("ToolTip.background", PANEL_BG_LIGHT);
            UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
            UIManager.put("ToolTip.border", BorderFactory.createLineBorder(CYAN_NEON, 1));
        } catch (Exception ignored) {}
    }

    // Glassmorphic Card Panel Creator
    public static JPanel createGlassCard(int cornerRadius) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(22, 30, 46, 235),
                    0, getHeight(), new Color(14, 20, 32, 245)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        return p;
    }

    public static JPanel createRoundedPanel(int radius, Color bg) {
        return createGlassCard(radius);
    }

    // Modern Rounded Gradient Button Creator
    public static JButton createGradientButton(String text, Color color1, Color color2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                Color c1 = pressed ? color1.darker() : (hover ? color1.brighter() : color1);
                Color c2 = pressed ? color2.darker() : (hover ? color2.brighter() : color2);

                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));

                if (hover) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() / 2.0, 12, 12));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    public static JButton createPrimaryButton(String text, Color color1, Color color2) {
        return createGradientButton(text, color1, color2);
    }

    public static JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(CYAN_NEON);
        return l;
    }

    public static JTextField createStyledTextField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(CYAN_NEON);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static JPasswordField createStyledPasswordField(String text) {
        JPasswordField pf = new JPasswordField(text);
        pf.setFont(FONT_BODY);
        pf.setBackground(BG_DARK);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(CYAN_NEON);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }
}
