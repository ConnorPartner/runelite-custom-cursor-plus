package com.cursorsize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.LongSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;

/** Temporary locator ring drawn below the cursor and tooltips. */
@Singleton
class CursorHighlight
{
    private final Client client;
    private final CursorSizeConfig config;
    private final LongSupplier clock;
    private volatile Long expiresAt;
    private volatile Point cursorCentre = new Point(0, 0);

    @Inject
    CursorHighlight(Client client, CursorSizeConfig config)
    {
        this(client, config, System::nanoTime);
    }

    CursorHighlight(Client client, CursorSizeConfig config, LongSupplier clock)
    {
        this.client = client;
        this.config = config;
        this.clock = clock;
    }

    // Ignore transparent padding, including the padding required by native cursors.
    void setCursorImage(BufferedImage image)
    {
        int minX = image == null ? 0 : image.getWidth();
        int minY = image == null ? 0 : image.getHeight();
        int maxX = -1;
        int maxY = -1;
        if (image != null)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                for (int x = 0; x < image.getWidth(); x++)
                {
                    if ((image.getRGB(x, y) >>> 24) == 0) { continue; }
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        cursorCentre = maxX < 0 ? new Point(0, 0) : new Point((minX + maxX + 1) / 2, (minY + maxY + 1) / 2);
    }

    void trigger()
    {
        expiresAt = clock.getAsLong() + Math.max(100, Math.min(5000, config.highlightDuration())) * 1_000_000L;
    }

    void clear() { expiresAt = null; }

    boolean isActive()
    {
        Long end = expiresAt;
        return end != null && clock.getAsLong() - end < 0 && client.getGameState() == GameState.LOGGED_IN;
    }

    void render(Graphics2D graphics)
    {
        if (!isActive()) { return; }
        Point mouse = client.getMouseCanvasPosition();
        int width = client.getCanvasWidth();
        int height = client.getCanvasHeight();
        if (mouse.getX() < 0 || mouse.getY() < 0 || mouse.getX() >= width || mouse.getY() >= height) { return; }
        int radius = Math.max(10, Math.min(150, config.highlightRadius()));
        Point centre = cursorCentre;
        int centreX = mouse.getX() + centre.getX();
        int centreY = mouse.getY() + centre.getY();
        Graphics2D ring = (Graphics2D) graphics.create();
        try
        {
            ring.clipRect(0, 0, width, height);
            ring.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // A dark border keeps the ring visible against bright backgrounds.
            ring.setColor(Color.BLACK);
            ring.setStroke(new BasicStroke(5));
            ring.drawOval(centreX - radius, centreY - radius, radius * 2, radius * 2);
            ring.setColor(config.highlightColor());
            ring.setStroke(new BasicStroke(3));
            ring.drawOval(centreX - radius, centreY - radius, radius * 2, radius * 2);
        }
        finally { ring.dispose(); }
    }
}
