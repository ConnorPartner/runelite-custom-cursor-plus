package com.cursorsize;

import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorMirrorTest
{
    @Test public void mirrorsEachAxisAndBothWithoutChangingSourceOrAlpha()
    {
        BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = {0xffff0000, 0x8000ff00, 0, 0xff0000ff, 0x40ffffff, 0xff123456};
        source.setRGB(0, 0, 3, 2, pixels, 0, 3);
        assertSame(source, CursorImages.mirror(source, false, false));
        assertArrayEquals(new int[]{pixels[2], pixels[1], pixels[0], pixels[5], pixels[4], pixels[3]},
            pixels(CursorImages.mirror(source, true, false)));
        assertArrayEquals(new int[]{pixels[3], pixels[4], pixels[5], pixels[0], pixels[1], pixels[2]},
            pixels(CursorImages.mirror(source, false, true)));
        assertArrayEquals(new int[]{pixels[5], pixels[4], pixels[3], pixels[2], pixels[1], pixels[0]},
            pixels(CursorImages.mirror(source, true, true)));
        assertArrayEquals(pixels, pixels(source));
        for (boolean horizontal : new boolean[]{false, true})
        {
            for (boolean vertical : new boolean[]{false, true})
            {
                BufferedImage mirrored = CursorImages.mirror(source, horizontal, vertical);
                assertEquals(3, mirrored.getWidth());
                assertEquals(2, mirrored.getHeight());
                assertArrayEquals(pixels, pixels(CursorImages.mirror(mirrored, horizontal, vertical)));
            }
        }
    }

    @Test public void handlesSinglePixelImages()
    {
        BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0x80123456);
        assertEquals(0x80123456, CursorImages.mirror(source, true, true).getRGB(0, 0));
    }

    @Test public void hotspotUsesRenderedImageSizeInsteadOfNativePadding()
    {
        BufferedImage source = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
        assertEquals(new java.awt.Point(0, 0), CursorImages.hotspot(source, new java.awt.Dimension(32, 32), false, false));
        assertEquals(new java.awt.Point(23, 0), CursorImages.hotspot(source, new java.awt.Dimension(32, 32), true, false));
        assertEquals(new java.awt.Point(0, 15), CursorImages.hotspot(source, new java.awt.Dimension(32, 32), false, true));
        assertEquals(new java.awt.Point(11, 7), CursorImages.hotspot(source, new java.awt.Dimension(12, 12), true, true));
    }

    private int[] pixels(BufferedImage image)
    {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }
}
