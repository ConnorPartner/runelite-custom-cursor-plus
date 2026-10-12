package com.cursorsize;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursorImageLoadingTest
{
    @Test public void decodesPngWithTransparencyUsingMemoryStreams() throws IOException
    {
        BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(1, 0, 0xffff0000);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(bytes))
        {
            assertTrue(ImageIO.write(source, "png", output));
        }
        BufferedImage decoded = CursorImages.read(new ByteArrayInputStream(bytes.toByteArray()));
        assertEquals(3, decoded.getWidth());
        assertEquals(2, decoded.getHeight());
        assertEquals(0xffff0000, decoded.getRGB(1, 0));
        assertEquals(0, decoded.getRGB(0, 0));
    }

    @Test(expected = IOException.class)
    public void rejectsInvalidImage() throws IOException
    {
        CursorImages.read(new ByteArrayInputStream(new byte[]{1, 2, 3}));
    }
}
