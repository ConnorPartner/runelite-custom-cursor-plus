package com.cursorsize;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/** Software fallback for displays whose native cursor size is too small. */
public class CursorSizeOverlay extends Overlay
{
    private final Client client;
    private volatile BufferedImage image;
    private volatile long lastRenderNanos;

    @Inject
    CursorSizeOverlay(Client client)
    {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(Float.MAX_VALUE);
    }

    void setImage(BufferedImage image)
    {
        this.image = image;
        lastRenderNanos = 0;
    }

    boolean isRendering(long now)
    {
        return image != null && lastRenderNanos != 0 && now - lastRenderNanos < 250_000_000L;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        BufferedImage current = image;
        Point mouse = client.getMouseCanvasPosition();
        if (current != null && mouse.getX() >= 0 && mouse.getY() >= 0
            && mouse.getX() < client.getCanvasWidth() && mouse.getY() < client.getCanvasHeight()
            && graphics.hitClip(mouse.getX(), mouse.getY(), current.getWidth(), current.getHeight()))
        {
            graphics.drawImage(current, mouse.getX(), mouse.getY(), null);
            lastRenderNanos = System.nanoTime();
        }
        return null;
    }
}
