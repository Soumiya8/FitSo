package ui.components;

import service.GamificationService.CharacterState;
import service.GamificationService.PetState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * AAA Quality Procedural Java2D Rendered Character & Pet View
 */
public class CharacterView extends JPanel implements ActionListener {

    private CharacterState characterState = CharacterState.HAPPY;
    private PetState petState = PetState.HAPPY;
    private String petName = "Rocky";

    private double bounceY = 0;
    private double bounceVel = 0.4;
    private double pulseScale = 1.0;
    private double pulseDir = 0.01;
    private int animTick = 0;

    private final Timer animTimer;

    public CharacterView() {
        setOpaque(false);
        setPreferredSize(new Dimension(360, 240));

        // Smooth 60 FPS animation loop
        animTimer = new Timer(16, this);
        animTimer.start();
    }

    public void updateState(CharacterState charState, PetState petState, String petName) {
        this.characterState = charState;
        this.petState = petState;
        this.petName = petName != null ? petName : "Rocky";
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        animTick++;

        // Smooth sine bounce physics
        if (characterState == CharacterState.CELEBRATION) {
            bounceY += bounceVel * 1.5;
            if (bounceY > 15 || bounceY < 0) bounceVel *= -1;
        } else if (characterState == CharacterState.HAPPY) {
            bounceY += bounceVel * 0.8;
            if (bounceY > 8 || bounceY < 0) bounceVel *= -1;
        } else {
            bounceY = Math.sin(animTick * 0.05) * 3.0;
        }

        pulseScale += pulseDir;
        if (pulseScale > 1.08 || pulseScale < 0.95) pulseDir *= -1;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // 1. Neon Platform / Pod Stand
        int cx = w / 2 - 20;
        int cy = h - 45;

        // Platform Outer Aura Glow
        Color auraColor = (characterState == CharacterState.CELEBRATION) ? Theme.GOLD_GLOW : Theme.CYAN_NEON;
        g2.setColor(new Color(auraColor.getRed(), auraColor.getGreen(), auraColor.getBlue(), 30));
        g2.fill(new Ellipse2D.Double(cx - 110, cy - 20, 220, 45));

        g2.setColor(new Color(0, 0, 0, 100));
        g2.fill(new Ellipse2D.Double(cx - 90, cy - 10, 180, 30));

        g2.setColor(auraColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new Ellipse2D.Double(cx - 90, cy - 10, 180, 30));

        // 2. Render Character Avatar (Procedural Hero)
        int charX = cx - 45;
        int charY = (int) (cy - 145 - bounceY);

        drawCharacterAvatar(g2, charX, charY);

        // 3. Render Pet Companion Avatar (Procedural Cute Dragon / Pet)
        int petX = cx + 45;
        int petY = (int) (cy - 75 - (bounceY * 0.6));

        drawPetAvatar(g2, petX, petY);

        g2.dispose();
    }

    private void drawCharacterAvatar(Graphics2D g2, int x, int y) {
        // Celebration Aura Sparks
        if (characterState == CharacterState.CELEBRATION) {
            g2.setColor(new Color(255, 215, 0, 80));
            g2.fill(new Ellipse2D.Double(x - 15, y - 15, 110, 140));
        }

        // Body / Torso (Athletics Suit)
        GradientPaint suitGp = new GradientPaint(x, y + 40, Theme.PANEL_BG_LIGHT, x + 80, y + 100, Theme.BLUE_ELECTRIC);
        g2.setPaint(suitGp);
        g2.fill(new RoundRectangle2D.Double(x + 15, y + 45, 50, 60, 20, 20));

        // Chest Logo Badge (⚡ FitSo Star)
        g2.setColor(Theme.CYAN_NEON);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("⚡", x + 33, y + 75);

        // Head / Face
        g2.setColor(new Color(255, 220, 180)); // Skin tone
        g2.fill(new Ellipse2D.Double(x + 18, y, 44, 48));

        // Hair (Stylized Haircut)
        g2.setColor(new Color(40, 30, 20));
        g2.fill(new Arc2D.Double(x + 16, y - 2, 48, 30, 0, 180, Arc2D.CHORD));

        // Cap / Headband Accessory
        g2.setColor(Theme.CYAN_NEON);
        g2.fill(new RoundRectangle2D.Double(x + 14, y + 10, 52, 10, 6, 6));

        // Eyes & Expression
        g2.setColor(new Color(30, 30, 30));
        if (characterState == CharacterState.HAPPY || characterState == CharacterState.CELEBRATION) {
            // Happy ^ ^ eyes
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString("^ ^", x + 28, y + 30);
            // Smile
            g2.drawArc(x + 32, y + 30, 16, 10, 190, 160);
        } else if (characterState == CharacterState.TIRED) {
            // Tired - - eyes
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(x + 26, y + 28, x + 34, y + 28);
            g2.drawLine(x + 44, y + 28, x + 52, y + 28);
        } else {
            // Idle Normal Eyes
            g2.fillOval(x + 28, y + 24, 6, 8);
            g2.fillOval(x + 46, y + 24, 6, 8);
            g2.drawArc(x + 34, y + 32, 12, 6, 200, 140);
        }
    }

    private void drawPetAvatar(Graphics2D g2, int x, int y) {
        // Cute Pet Dragon Body
        GradientPaint petGp = new GradientPaint(x, y, Theme.PURPLE_AURA, x + 40, y + 50, Theme.CYAN_NEON);
        g2.setPaint(petGp);
        g2.fill(new Ellipse2D.Double(x, y, 42, 44));

        // Pet Ears / Horns
        g2.setColor(Theme.GOLD_GLOW);
        Polygon leftEar = new Polygon(new int[]{x + 6, x + 12, x + 2}, new int[]{y + 4, y - 8, y + 10}, 3);
        Polygon rightEar = new Polygon(new int[]{x + 36, x + 30, x + 40}, new int[]{y + 4, y - 8, y + 10}, 3);
        g2.fill(leftEar);
        g2.fill(rightEar);

        // Pet Face
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 10, y + 12, 8, 10);
        g2.fillOval(x + 24, y + 12, 8, 10);

        g2.setColor(Color.BLACK);
        g2.fillOval(x + 12, y + 14, 4, 6);
        g2.fillOval(x + 26, y + 14, 4, 6);

        // Cheek blush
        g2.setColor(new Color(255, 100, 150, 180));
        g2.fillOval(x + 6, y + 22, 6, 4);
        g2.fillOval(x + 30, y + 22, 6, 4);

        // Pet Name Badge
        g2.setColor(Theme.PANEL_BG);
        g2.fill(new RoundRectangle2D.Double(x - 5, y + 46, 52, 18, 6, 6));
        g2.setColor(Theme.GOLD_GLOW);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Double(x - 5, y + 46, 52, 18, 6, 6));

        g2.setFont(Theme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int tx = x - 5 + (52 - fm.stringWidth(petName)) / 2;
        g2.drawString(petName, tx, y + 59);
    }
}
