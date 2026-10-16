package com.cursorsize;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

final class CursorImages
{
    private CursorImages() {}

    static BufferedImage read(InputStream input) throws IOException
    {
        // Explicit memory cache prevents ImageIO from creating temporary files.
        synchronized (ImageIO.class)
        {
            try (MemoryCacheImageInputStream imageInput = new MemoryCacheImageInputStream(input))
            {
                java.util.Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(imageInput);
                if (!readers.hasNext()) { throw new IOException("cursor.png is not a supported image"); }
                javax.imageio.ImageReader reader = readers.next();
                try
                {
                    reader.setInput(imageInput);
                    return reader.read(0);
                }
                finally { reader.dispose(); }
            }
        }
    }

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

    /** Reflect the image without modifying shared built-in cursor images or losing alpha. */
    static BufferedImage mirror(BufferedImage source, boolean horizontal, boolean vertical)
    {
        if (!horizontal && !vertical) { return source; }
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                result.setRGB(x, y, source.getRGB(horizontal ? width - 1 - x : x,
                    vertical ? height - 1 - y : y));
            }
        }
        return result;
    }

    /** Inward outline: no padding, hotspot shift, or changes to the image's alpha mask. */
    static BufferedImage outline(BufferedImage source, int requestedThickness, Color color)
    {
        int thickness = Math.max(0, Math.min(5, requestedThickness));
        if (thickness == 0) { return source; }
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = source.getRGB(0, 0, width, height, null, 0, width);
        int[] result = pixels.clone();
        int rgb = (color == null ? Color.BLACK : color).getRGB() & 0x00ffffff;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int index = y * width + x;
                if ((pixels[index] >>> 24) == 0) { continue; }
                boolean edge = false;
                for (int dy = -thickness; dy <= thickness && !edge; dy++)
                {
                    for (int dx = -thickness; dx <= thickness; dx++)
                    {
                        if (dx * dx + dy * dy > thickness * thickness) { continue; }
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx < 0 || ny < 0 || nx >= width || ny >= height
                            || (pixels[ny * width + nx] >>> 24) == 0)
                        {
                            edge = true;
                            break;
                        }
                    }
                }
                if (edge) { result[index] = (pixels[index] & 0xff000000) | rgb; }
            }
        }
        BufferedImage outlined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        outlined.setRGB(0, 0, width, height, result, 0, width);
        return outlined;
    }

    static int legacyPercentage(BufferedImage source, int pixels)
    {
        int size = Math.max(8, Math.min(128, pixels));
        return Math.max(25, Math.min(400, (int) Math.round(100.0 * size
            / Math.max(source.getWidth(), source.getHeight()))));
    }

    static Point hotspot(BufferedImage image, Dimension canvas, boolean horizontal, boolean vertical)
    {
        double ratio = Math.min(1.0, Math.min((double) canvas.width / image.getWidth(),
            (double) canvas.height / image.getHeight()));
        int width = Math.max(1, (int) Math.round(image.getWidth() * ratio));
        int height = Math.max(1, (int) Math.round(image.getHeight() * ratio));
        return new Point(horizontal ? width - 1 : 0, vertical ? height - 1 : 0);
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
