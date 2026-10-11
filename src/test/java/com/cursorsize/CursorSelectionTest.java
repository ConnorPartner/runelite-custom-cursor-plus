package com.cursorsize;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.customcursor.CustomCursor;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorSelectionTest
{
    @Test public void neverHidesNativeCursorOutsideLoggedInState()
    {
        BufferedImage image = CursorImages.scale(CursorImages.arrow(), 128, false);
        assertFalse(CursorSizePlugin.useOverlay(false, image, new Dimension(32, 32)));
        assertTrue(CursorSizePlugin.useOverlay(true, image, new Dimension(32, 32)));
        assertFalse(CursorSizePlugin.useOverlay(true, image, new Dimension(128, 128)));
    }

    @Test public void followsEveryCustomCursorSelection()
    {
        for (CustomCursor cursor : CustomCursor.values())
        {
            assertSame(cursor, CursorStyle.FOLLOW_CUSTOM_CURSOR.selectedCursor(cursor));
        }
        assertSame(CustomCursor.CUSTOM_IMAGE, CursorStyle.CUSTOM_IMAGE.selectedCursor(CustomCursor.RS3_GOLD));
        assertNull(CursorStyle.ARROW.selectedCursor(CustomCursor.RS3_GOLD));
    }

    @Test public void everyBuiltInCursorCanGrowAndShrink()
    {
        for (CursorStyle style : CursorStyle.values())
        {
            if (style.builtIn == null) { continue; }
            assertSame(style.builtIn, style.selectedCursor(CustomCursor.RS3_SILVER));
            BufferedImage source = style.builtIn.getCursorImage();
            for (int size : new int[]{8, 48, 128})
            {
                BufferedImage scaled = CursorImages.scale(source, size, false);
                assertEquals(size, Math.max(scaled.getWidth(), scaled.getHeight()));
                boolean visible = false;
                for (int y = 0; y < scaled.getHeight(); y++)
                {
                    for (int x = 0; x < scaled.getWidth(); x++)
                    {
                        visible |= (scaled.getRGB(x, y) >>> 24) != 0;
                    }
                }
                assertTrue(style + " must remain visible at " + size, visible);
            }
        }
    }
}
