package com.cursorsize;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorOutlineTest
{
    private BufferedImage square()
    {
        BufferedImage image = new BufferedImage(9, 9, BufferedImage.TYPE_INT_ARGB);
        for (int y = 1; y < 8; y++)
        {
            for (int x = 1; x < 8; x++) { image.setRGB(x, y, Color.RED.getRGB()); }
        }
        return image;
    }

    @Test public void disabledOutlineLeavesOriginalUntouched()
    {
        BufferedImage source = square();
        assertSame(source, CursorImages.outline(source, 0, Color.BLUE));
        assertSame(source, CursorImages.outline(source, -1, Color.BLUE));
    }

    @Test public void coloursEdgesWithoutMovingImageOrChangingInterior()
    {
        BufferedImage source = square();
        BufferedImage result = CursorImages.outline(source, 1, Color.BLUE);
        assertEquals(9, result.getWidth());
        assertEquals(9, result.getHeight());
        assertEquals(Color.BLUE.getRGB(), result.getRGB(1, 4));
        assertEquals(Color.RED.getRGB(), result.getRGB(2, 4));
        assertEquals(Color.RED.getRGB(), result.getRGB(4, 4));
        assertEquals(0, result.getRGB(0, 4));
        assertEquals(Color.RED.getRGB(), source.getRGB(1, 4));
    }

    @Test public void thickerOutlineReachesFurtherInside()
    {
        BufferedImage result = CursorImages.outline(square(), 2, Color.GREEN);
        assertEquals(Color.GREEN.getRGB(), result.getRGB(2, 4));
        assertEquals(Color.RED.getRGB(), result.getRGB(3, 4));
    }

    @Test public void preservesAntialiasingAndOutlinesHoles()
    {
        BufferedImage source = square();
        source.setRGB(1, 4, 0x80ff0000);
        source.setRGB(4, 4, 0);
        BufferedImage result = CursorImages.outline(source, 1, Color.WHITE);
        assertEquals(0x80ffffff, result.getRGB(1, 4));
        assertEquals(0, result.getRGB(4, 4));
        assertEquals(Color.WHITE.getRGB(), result.getRGB(4, 3));
    }

    @Test public void handlesImageBoundaryTinyAndEmptyImages()
    {
        BufferedImage tiny = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        tiny.setRGB(0, 0, Color.RED.getRGB());
        assertEquals(Color.BLACK.getRGB(), CursorImages.outline(tiny, 5, null).getRGB(0, 0));
        assertEquals(0, CursorImages.outline(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 5, Color.BLUE).getRGB(0, 0));
    }

    @Test public void clampsThicknessToFivePixels()
    {
        BufferedImage source = square();
        BufferedImage expected = CursorImages.outline(source, 5, Color.BLUE);
        BufferedImage actual = CursorImages.outline(source, Integer.MAX_VALUE, Color.BLUE);
        assertArrayEquals(expected.getRGB(0, 0, 9, 9, null, 0, 9), actual.getRGB(0, 0, 9, 9, null, 0, 9));
    }
}
