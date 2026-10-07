package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/**
 * Custom Java2D Bar Chart Component
 * Pure Graphics2D implementation - No external charting dependencies.
 */
public class BarChartPanel extends JPanel {

    private String chartTitle = "Bar Chart";
    private Map<String, Integer> data;
    private Color barColor = Theme.ACCENT_BLUE;

    public BarChartPanel(String title, Color barColor) {
        this.chartTitle = title;
        this.barColor = barColor != null ? barColor : Theme.ACCENT_BLUE;
        setOpaque(false);
        setPreferredSize(new Dimension(360, 220));
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Chart Title
        g2.setColor(Theme.TEXT_PRIMARY);
        g2.setFont(Theme.FONT_BOLD);
        g2.drawString(chartTitle, 15, 22);

        if (data == null || data.isEmpty()) {
            drawEmptyState(g2, w, h);
            g2.dispose();
            return;
        }

        int padLeft = 45;
        int padRight = 20;
        int padTop = 40;
        int padBottom = 35;

        int chartW = w - padLeft - padRight;
        int chartH = h - padTop - padBottom;

        int maxVal = 1;
        for (int val : data.values()) {
            if (val > maxVal) maxVal = val;
        }

        // Draw Axes & Gridlines
        g2.setFont(Theme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();

        int gridSteps = 4;
        for (int i = 0; i <= gridSteps; i++) {
            int y = padTop + chartH - (i * chartH / gridSteps);
            g2.setColor(Theme.CARD_BORDER);
            g2.drawLine(padLeft, y, padLeft + chartW, y);

            int labelVal = (maxVal * i) / gridSteps;
            String lbl = String.valueOf(labelVal);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString(lbl, padLeft - fm.stringWidth(lbl) - 8, y + 4);
        }

        // Draw Bars
        int count = data.size();
        int barWidth = Math.max(10, (chartW / count) - 12);
        int gap = (chartW - (barWidth * count)) / (count + 1);

        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int val = entry.getValue();
            int bHeight = (int) (((double) val / maxVal) * chartH);
            int x = padLeft + gap + index * (barWidth + gap);
            int y = padTop + chartH - bHeight;

            if (bHeight > 0) {
                GradientPaint gp = new GradientPaint(
                    x, y, barColor,
                    x, padTop + chartH, new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 50)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, bHeight, 8, 8));
            }

            g2.setColor(Theme.TEXT_MUTED);
            String xLbl = entry.getKey();
            if (xLbl.length() > 6) xLbl = xLbl.substring(xLbl.length() - 5);
            int tx = x + (barWidth - fm.stringWidth(xLbl)) / 2;
            g2.drawString(xLbl, tx, padTop + chartH + 18);

            index++;
        }

        g2.dispose();
    }

    private void drawEmptyState(Graphics2D g2, int w, int h) {
        g2.setColor(Theme.TEXT_MUTED);
        g2.setFont(Theme.FONT_BODY);
        String msg = "📊  No workout activity logged yet.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2 + 5);
    }
}
