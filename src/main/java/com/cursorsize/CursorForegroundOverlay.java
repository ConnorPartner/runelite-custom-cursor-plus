package com.cursorsize;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.TooltipOverlay;

/** Final pass: highlights and menus, then cursor, then readable tooltips. */
public class CursorForegroundOverlay extends Overlay
{
    private final CursorSizeOverlay cursor;
    private final TooltipOverlay tooltips;

    @Inject
    CursorForegroundOverlay(CursorSizeOverlay cursor, TooltipOverlay tooltips)
    {
        this.cursor = cursor;
        this.tooltips = tooltips;
        // TOOLTIP sorts after dynamic and fixed overlays. Lower priority draws last here.
        setPosition(OverlayPosition.TOOLTIP);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(-Float.MAX_VALUE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        return cursor.renderFinal(graphics, () -> tooltips.render(graphics));
    }
}
