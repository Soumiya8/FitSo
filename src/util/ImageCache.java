package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Image Cache with Java2D procedural fallbacks to ensure application never crashes on missing assets.
 */
public class ImageCache {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage getImage(String relativePath, int width, int height) {
        String key = relativePath + "_" + width + "x" + height;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        BufferedImage img = null;
        File file = new File(relativePath);
        if (file.exists()) {
            try {
                img = ImageIO.read(file);
            } catch (IOException ignored) {}
        }

        if (img == null) {
            img = createPlaceholderImage(relativePath, width, height);
        } else if (width > 0 && height > 0) {
            img = resizeImage(img, width, height);
        }

        cache.put(key, img);
        return img;
    }

    public static ImageIcon getIcon(String relativePath, int width, int height) {
        return new ImageIcon(getImage(relativePath, width, height));
    }

    private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return resized;
    }

    private static BufferedImage createPlaceholderImage(String path, int w, int h) {
        int width = Math.max(w, 80);
        int height = Math.max(h, 80);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Stylish gradient placeholder
        Color c1 = new Color(30, 41, 59);
        Color c2 = new Color(51, 65, 85);
        GradientPaint gp = new GradientPaint(0, 0, c1, width, height, c2);
        g2.setPaint(gp);
        g2.fillRoundRect(2, 2, width - 4, height - 4, 16, 16);

        g2.setColor(new Color(88, 166, 255, 180));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(2, 2, width - 4, height - 4, 16, 16);

        // Icon text tag
        g2.setColor(new Color(240, 246, 252));
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, width / 8)));
        String tag = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        if (tag.length() > 10) tag = tag.substring(0, 10) + "...";
        
        FontMetrics fm = g2.getFontMetrics();
        int tx = (width - fm.stringWidth(tag)) / 2;
        int ty = (height + fm.getAscent()) / 2 - 2;
        g2.drawString(tag, tx, ty);

        g2.dispose();
        return img;
    }
}
