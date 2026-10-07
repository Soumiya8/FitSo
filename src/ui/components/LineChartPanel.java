package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Map;

/**
 * Custom Java2D Line Chart Component for Weight Progression
 */
public class LineChartPanel extends JPanel {

    private String chartTitle = "Line Chart";
    private Map<String, Double> data;
    private Color lineColor = Theme.ACCENT_GREEN;

    public LineChartPanel(String title, Color lineColor) {
        this.chartTitle = title;
        this.lineColor = lineColor != null ? lineColor : Theme.ACCENT_GREEN;
        setOpaque(false);
        setPreferredSize(new Dimension(360, 220));
    }

    public void setData(Map<String, Double> data) {
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

        double minVal = Double.MAX_VALUE;
        double maxVal = Double.MIN_VALUE;

        for (double val : data.values()) {
            if (val < minVal) minVal = val;
            if (val > maxVal) maxVal = val;
        }
        if (minVal == maxVal) {
            minVal = Math.max(0, minVal - 5);
            maxVal += 5;
        } else {
            minVal = Math.max(0, minVal - 2);
            maxVal += 2;
        }

        int gridSteps = 4;
        g2.setFont(Theme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i <= gridSteps; i++) {
            int y = padTop + chartH - (i * chartH / gridSteps);
            g2.setColor(Theme.CARD_BORDER);
            g2.drawLine(padLeft, y, padLeft + chartW, y);

            double labelVal = minVal + ((maxVal - minVal) * i / gridSteps);
            String lbl = String.format("%.1f", labelVal);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString(lbl, padLeft - fm.stringWidth(lbl) - 8, y + 4);
        }

        int count = data.size();
        int stepX = count > 1 ? chartW / (count - 1) : chartW;

        int[] xPts = new int[count];
        int[] yPts = new int[count];

        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = entry.getValue();
            int x = padLeft + (count > 1 ? index * stepX : chartW / 2);
            int y = (int) (padTop + chartH - (((val - minVal) / (maxVal - minVal)) * chartH));

            xPts[index] = x;
            yPts[index] = y;

            g2.setColor(Theme.TEXT_MUTED);
            String xLbl = entry.getKey();
            if (xLbl.length() > 5) xLbl = xLbl.substring(xLbl.length() - 5);
            g2.drawString(xLbl, x - fm.stringWidth(xLbl) / 2, padTop + chartH + 18);

            index++;
        }

        if (count > 1) {
            Path2D.Double path = new Path2D.Double();
            path.moveTo(xPts[0], yPts[0]);
            for (int i = 1; i < count; i++) {
                path.lineTo(xPts[i], yPts[i]);
            }

            Path2D.Double fillPath = (Path2D.Double) path.clone();
            fillPath.lineTo(xPts[count - 1], padTop + chartH);
            fillPath.lineTo(xPts[0], padTop + chartH);
            fillPath.closePath();

            GradientPaint areaGp = new GradientPaint(
                0, padTop, new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 80),
                0, padTop + chartH, new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 0)
            );
            g2.setPaint(areaGp);
            g2.fill(fillPath);

            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(path);
        }

        for (int i = 0; i < count; i++) {
            g2.setColor(Theme.PANEL_BG);
            g2.fill(new Ellipse2D.Double(xPts[i] - 5, yPts[i] - 5, 10, 10));

            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(2.0f));
            g2.draw(new Ellipse2D.Double(xPts[i] - 5, yPts[i] - 5, 10, 10));
        }

        g2.dispose();
    }

    private void drawEmptyState(Graphics2D g2, int w, int h) {
        g2.setColor(Theme.TEXT_MUTED);
        g2.setFont(Theme.FONT_BODY);
        String msg = "📈  No body weight measurements logged yet.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2 + 5);
    }
}
