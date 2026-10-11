package com.cursorsize;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorImagesTest
{
    @Test public void scalesUpAndDownWithoutChangingAspectRatio()
    {
        BufferedImage source = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        BufferedImage large = CursorImages.scale(source, 64, false);
        assertEquals(32, large.getWidth());
        assertEquals(64, large.getHeight());
        BufferedImage small = CursorImages.scale(source, 8, true);
        assertEquals(4, small.getWidth());
        assertEquals(8, small.getHeight());
    }

    @Test public void preservesTransparencyAndPadsWithoutEnlarging()
    {
        BufferedImage source = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0xffff0000);
        BufferedImage padded = CursorImages.fitNativeCanvas(source, new Dimension(32, 32));
        assertEquals(0xffff0000, padded.getRGB(0, 0));
        assertEquals(0, padded.getRGB(8, 8));
        assertEquals(0, padded.getRGB(31, 31));
    }

    @Test public void limitsOversizedSettingsAndFitsNativeLimits()
    {
        BufferedImage source = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        assertEquals(128, CursorImages.scale(source, Integer.MAX_VALUE, false).getHeight());
        assertEquals(8, CursorImages.scale(source, -1, false).getHeight());
        BufferedImage fitted = CursorImages.fitNativeCanvas(source, new Dimension(8, 8));
        assertEquals(8, fitted.getWidth());
        assertEquals(8, fitted.getHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnsupportedNativeCursors()
    {
        CursorImages.fitNativeCanvas(CursorImages.arrow(), new Dimension(0, 0));
    }
}
