package com.cursorsize;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.Point;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorSizeOverlayTest
{
    private Point mouse = new Point(12, 14);

    private CursorSizeOverlay overlay()
    {
        Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[]{Client.class},
            (proxy, method, args) ->
            {
                switch (method.getName())
                {
                    case "getMouseCanvasPosition": return mouse;
                    case "getCanvasWidth": return 100;
                    case "getCanvasHeight": return 100;
                    default: throw new UnsupportedOperationException(method.getName());
                }
            });
        return new CursorSizeOverlay(client);
    }

    private BufferedImage render(CursorSizeOverlay overlay)
    {
        BufferedImage output = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        try { overlay.render(graphics); }
        finally { graphics.dispose(); }
        return output;
    }

    @Test public void drawsAtClickPointAndClearsWhenDisabled()
    {
        CursorSizeOverlay overlay = overlay();
        BufferedImage cursor = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        cursor.setRGB(0, 0, 0xffff0000);
        cursor.setRGB(47, 47, 0xff00ff00);
        overlay.setImage(cursor);
        assertFalse(overlay.isRendering(System.nanoTime()));
        BufferedImage output = render(overlay);
        assertTrue(overlay.isRendering(System.nanoTime()));
        assertFalse(overlay.isRendering(System.nanoTime() + 300_000_000L));
        assertEquals(0xffff0000, output.getRGB(12, 14));
        assertEquals(0xff00ff00, output.getRGB(59, 61));
        overlay.setImage(null);
        assertFalse(overlay.isRendering(System.nanoTime()));
        assertEquals(0, render(overlay).getRGB(12, 14));
    }

    @Test public void doesNotDrawWhenMouseLeavesCanvas()
    {
        CursorSizeOverlay overlay = overlay();
        overlay.setImage(CursorImages.arrow());
        mouse = new Point(-1, -1);
        BufferedImage output = render(overlay);
        for (int y = 0; y < 100; y++)
        {
            for (int x = 0; x < 100; x++) { assertEquals(0, output.getRGB(x, y)); }
        }
    }

    @Test public void clippedDrawingDoesNotHideNativeCursor()
    {
        CursorSizeOverlay overlay = overlay();
        overlay.setImage(CursorImages.arrow());
        BufferedImage output = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        try
        {
            graphics.setClip(90, 90, 10, 10);
            overlay.render(graphics);
            assertFalse(overlay.isRendering(System.nanoTime()));
        }
        finally { graphics.dispose(); }
    }
}
