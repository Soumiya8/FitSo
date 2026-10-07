package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Animated Gradient Energy / XP Bar with Level Badge
 */
public class EnergyBar extends JPanel {

    private int level = 1;
    private int currentEnergy = 0;
    private int progressPercent = 0;

    public EnergyBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 42));
    }

    public void updateProgress(int level, int currentEnergy, int progressPercent) {
        this.level = level;
        this.currentEnergy = currentEnergy;
        this.progressPercent = Math.min(100, Math.max(0, progressPercent));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Level Badge Circle
        int badgeSize = 36;
        g2.setColor(Theme.PANEL_BG_LIGHT);
        g2.fill(new Ellipse2D.Double(0, (h - badgeSize) / 2.0, badgeSize, badgeSize));
        g2.setColor(Theme.GOLD_GLOW);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new Ellipse2D.Double(0, (h - badgeSize) / 2.0, badgeSize, badgeSize));

        g2.setColor(Theme.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String lvlStr = "L" + level;
        FontMetrics fm = g2.getFontMetrics();
        int tx = (badgeSize - fm.stringWidth(lvlStr)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(lvlStr, tx, ty);

        // Progress Track
        int trackX = badgeSize + 12;
        int trackWidth = w - trackX - 10;
        int trackHeight = 18;
        int trackY = (h - trackHeight) / 2;

        g2.setColor(Theme.BG_DARK);
        g2.fill(new RoundRectangle2D.Double(trackX, trackY, trackWidth, trackHeight, 10, 10));
        g2.setColor(Theme.CARD_BORDER);
        g2.draw(new RoundRectangle2D.Double(trackX, trackY, trackWidth, trackHeight, 10, 10));

        // Progress Fill Gradient
        int fillWidth = (trackWidth * progressPercent) / 100;
        if (fillWidth > 0) {
            GradientPaint gp = new GradientPaint(
                trackX, 0, new Color(255, 107, 107),
                trackX + fillWidth, 0, new Color(255, 142, 83)
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(trackX, trackY, fillWidth, trackHeight, 10, 10));

            g2.setColor(new Color(255, 255, 255, 50));
            g2.fill(new RoundRectangle2D.Double(trackX, trackY, fillWidth, trackHeight / 2.0, 10, 10));
        }

        // Energy Label Text
        g2.setColor(Theme.TEXT_PRIMARY);
        g2.setFont(Theme.FONT_BOLD);
        String energyText = currentEnergy + " XP (" + progressPercent + "%)";
        fm = g2.getFontMetrics();
        int etX = trackX + (trackWidth - fm.stringWidth(energyText)) / 2;
        int etY = trackY + (trackHeight + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(energyText, etX, etY);

        g2.dispose();
    }
}
