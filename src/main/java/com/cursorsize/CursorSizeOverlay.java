package com.cursorsize;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

/** Software fallback for displays whose native cursor size is too small. */
@Singleton
public class CursorSizeOverlay extends Overlay
{
    private final Client client;
    private final TooltipManager tooltipManager;
    private volatile List<Tooltip> deferredTooltips = Collections.emptyList();
    private volatile BufferedImage image;
    private volatile long lastRenderNanos;

    @Inject
    CursorSizeOverlay(Client client, TooltipManager tooltipManager)
    {
        this.client = client;
        this.tooltipManager = tooltipManager;
        setPosition(OverlayPosition.DYNAMIC);
        // Capture tooltips before their normal pass; the final pass draws them after the cursor.
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(Float.MAX_VALUE);
    }

    void setImage(BufferedImage image)
    {
        this.image = image;
        lastRenderNanos = 0;
        if (image == null) { deferredTooltips = Collections.emptyList(); }
    }

    boolean isRendering(long now)
    {
        return image != null && lastRenderNanos != 0 && now - lastRenderNanos < 250_000_000L;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (image != null && !tooltipManager.getTooltips().isEmpty())
        {
            List<Tooltip> captured = new ArrayList<>(deferredTooltips);
            captured.addAll(tooltipManager.getTooltips());
            deferredTooltips = captured;
            tooltipManager.clear();
        }
        return null;
    }

    void beginFrame()
    {
        deferredTooltips = Collections.emptyList();
    }

    Dimension renderFinal(Graphics2D graphics, Runnable drawTooltips)
    {
        List<Tooltip> captured = deferredTooltips;
        deferredTooltips = Collections.emptyList();
        try
        {
            return renderCursor(graphics);
        }
        finally
        {
            // Keep tooltip rendering in RuneLite, including components, colours and placement.
            tooltipManager.getTooltips().addAll(0, captured);
            if (!tooltipManager.getTooltips().isEmpty()) { drawTooltips.run(); }
        }
    }

    Dimension renderCursor(Graphics2D graphics)
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
