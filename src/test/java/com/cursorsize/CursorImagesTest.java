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
        BufferedImage large = CursorImages.scale(source, 200, false);
        assertEquals(32, large.getWidth());
        assertEquals(64, large.getHeight());
        BufferedImage small = CursorImages.scale(source, 25, true);
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

    @Test public void originalSizeAndFractionalScale()
    {
        BufferedImage source = new BufferedImage(17, 31, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0xffff0000);
        BufferedImage original = CursorImages.scale(source, 100, false);
        assertEquals(17, original.getWidth());
        assertEquals(31, original.getHeight());
        assertEquals(0xffff0000, original.getRGB(0, 0));
        BufferedImage scaled = CursorImages.scale(source, 150, true);
        assertEquals(26, scaled.getWidth());
        assertEquals(47, scaled.getHeight());
    }

    @Test public void limitsLargeCustomImagesAndKeepsTinyImagesValid()
    {
        BufferedImage large = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_ARGB);
        BufferedImage scaled = CursorImages.scale(large, 400, false);
        assertEquals(512, scaled.getWidth());
        assertEquals(256, scaled.getHeight());
        BufferedImage tiny = CursorImages.scale(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 25, false);
        assertEquals(1, tiny.getWidth());
        assertEquals(1, tiny.getHeight());
    }

    @Test public void migratesLegacyPixelsToPercentage()
    {
        assertEquals(150, CursorImages.legacyPercentage(CursorImages.arrow(), 48));
        assertEquals(400, CursorImages.legacyPercentage(CursorImages.arrow(), 128));
        assertEquals(25, CursorImages.legacyPercentage(CursorImages.arrow(), 8));
        assertEquals(100, CursorImages.legacyPercentage(new BufferedImage(48, 24, BufferedImage.TYPE_INT_ARGB), 48));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnsupportedNativeCursors()
    {
        CursorImages.fitNativeCanvas(CursorImages.arrow(), new Dimension(0, 0));
    }
}
