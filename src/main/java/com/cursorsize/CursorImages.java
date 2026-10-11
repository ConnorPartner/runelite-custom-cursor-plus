package com.cursorsize;

import java.awt.*;
import java.awt.image.BufferedImage;

final class CursorImages
{
    private CursorImages() {}

    static BufferedImage arrow()
    {
        BufferedImage image = new BufferedImage(24, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try
        {
            Polygon outline = new Polygon(new int[]{0, 0, 6, 11, 16, 11, 22},
                new int[]{0, 25, 20, 31, 29, 18, 18}, 7);
            g.setColor(Color.WHITE);
            g.fillPolygon(outline);
            g.setColor(Color.BLACK);
            g.drawPolygon(outline);
        }
        finally { g.dispose(); }
        return image;
    }

    static BufferedImage scale(BufferedImage source, int requestedPercent, boolean smooth)
    {
        int percent = Math.max(25, Math.min(400, requestedPercent));
        double ratio = Math.min(percent / 100.0, 512.0 / Math.max(source.getWidth(), source.getHeight()));
        int width = Math.max(1, (int) Math.round(source.getWidth() * ratio));
        int height = Math.max(1, (int) Math.round(source.getHeight() * ratio));
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try
        {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, smooth
                ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(source, 0, 0, width, height, null);
        }
        finally { g.dispose(); }
        return result;
    }

    static int legacyPercentage(BufferedImage source, int pixels)
    {
        int size = Math.max(8, Math.min(128, pixels));
        return Math.max(25, Math.min(400, (int) Math.round(100.0 * size
            / Math.max(source.getWidth(), source.getHeight()))));
    }

    // Padding prevents AWT from stretching small images to its native cursor size.
    static BufferedImage fitNativeCanvas(BufferedImage image, Dimension canvas)
    {
        if (canvas.width <= 0 || canvas.height <= 0)
        {
            throw new IllegalArgumentException("Custom cursors are unsupported by this display");
        }
        double ratio = Math.min(1.0, Math.min((double) canvas.width / image.getWidth(),
            (double) canvas.height / image.getHeight()));
        BufferedImage result = new BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try
        {
            g.drawImage(image, 0, 0, Math.max(1, (int) Math.round(image.getWidth() * ratio)),
                Math.max(1, (int) Math.round(image.getHeight() * ratio)), null);
        }
        finally { g.dispose(); }
        return result;
    }
}
