package com.cursorsize;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorHighlightTest
{
    private long now;
    private Point mouse = new Point(50, 50);
    private GameState state = GameState.LOGGED_IN;
    private final Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[]{Client.class},
        (proxy, method, args) ->
        {
            switch (method.getName())
            {
                case "getGameState": return state;
                case "getMouseCanvasPosition": return mouse;
                case "getCanvasWidth": case "getCanvasHeight": return 200;
                default: throw new UnsupportedOperationException(method.getName());
            }
        });
    private final CursorHighlight highlight = new CursorHighlight(client, new CursorSizeConfig() {}, () -> now);

    private BufferedImage render()
    {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try { highlight.render(graphics); }
        finally { graphics.dispose(); }
        return image;
    }

    @Test public void expiresRetriggersAndClears()
    {
        assertFalse(highlight.isActive());
        highlight.trigger();
        assertTrue(highlight.isActive());
        now = 900_000_000L;
        highlight.trigger();
        now = 1_000_000_000L;
        assertTrue(highlight.isActive());
        now = 1_900_000_000L;
        assertFalse(highlight.isActive());
        highlight.trigger();
        highlight.clear();
        assertFalse(highlight.isActive());
    }

    @Test public void followsMouseAndDoesNotFillClickPoint()
    {
        highlight.trigger();
        BufferedImage first = render();
        assertEquals(Color.YELLOW.getRGB(), first.getRGB(90, 50));
        assertEquals(0, first.getRGB(50, 50));
        mouse = new Point(100, 100);
        BufferedImage next = render();
        assertEquals(Color.YELLOW.getRGB(), next.getRGB(140, 100));
        assertEquals(0, next.getRGB(90, 50));
    }

    @Test public void hidesOutsideCanvasAndWhileLoggedOut()
    {
        highlight.trigger();
        mouse = new Point(-1, 50);
        assertEquals(0, render().getRGB(39, 50));
        mouse = new Point(50, 50);
        state = GameState.LOGIN_SCREEN;
        assertFalse(highlight.isActive());
        assertEquals(0, render().getRGB(90, 50));
    }

    @Test public void preservesGraphicsStateAndKeepsTooltipsAboveNativeCursorHighlight()
    {
        highlight.trigger();
        TooltipManager manager = new TooltipManager();
        CursorSizeOverlay overlay = new CursorSizeOverlay(client, manager, highlight);
        manager.add(new Tooltip("Visible tooltip"));
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            graphics.setColor(Color.MAGENTA);
            java.awt.Stroke stroke = graphics.getStroke();
            overlay.render(graphics);
            assertTrue(manager.getTooltips().isEmpty());
            overlay.renderFinal(graphics, () ->
            {
                assertEquals(Color.MAGENTA, graphics.getColor());
                assertSame(stroke, graphics.getStroke());
                assertEquals(1, manager.getTooltips().size());
                graphics.setColor(Color.WHITE);
                graphics.fillRect(88, 48, 5, 5);
                manager.clear();
            });
            assertEquals(Color.WHITE.getRGB(), image.getRGB(90, 50));
            assertFalse(overlay.isRendering(now));
        }
        finally { graphics.dispose(); }
    }
}
