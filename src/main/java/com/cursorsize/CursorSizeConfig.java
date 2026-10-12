package com.cursorsize;

import java.awt.Color;
import net.runelite.client.config.*;

@ConfigGroup(CursorSizeConfig.GROUP)
public interface CursorSizeConfig extends Config
{
    String GROUP = "cursorsize";

    @ConfigItem(keyName = "style", name = "Cursor style", description = "Follow Custom Cursor uses the cursor selected in RuneLite's Custom Cursor settings, including custom images and equipped weapons. Keep that plugin disabled.", position = 0)
    default CursorStyle style() { return CursorStyle.FOLLOW_CUSTOM_CURSOR; }

    @Range(min = 25, max = 400)
    @ConfigItem(keyName = "scalePercent", name = "Cursor scale (%)", description = "25–400%. 100% is the original image size; 200% doubles it. Maximum rendered edge: 512 pixels.", position = 1)
    default int scalePercent() { return 150; }

    @ConfigItem(keyName = "smooth", name = "Smooth scaling", description = "Smooth image edges; disable for crisp pixel art.", position = 2)
    default boolean smooth() { return false; }
    @Range(min = 0, max = 5)
    @ConfigItem(keyName = "outlineThickness", name = "Outline thickness", description = "Outline width in pixels after scaling (0–5). 0 disables it. Drawn inside the visible edge to preserve the click point.", position = 3)
    default int outlineThickness() { return 0; }

    @ConfigItem(keyName = "outlineColor", name = "Outline colour", description = "Colour of the cursor outline. Set Outline thickness above 0 to enable it.", position = 4)
    default Color outlineColor() { return Color.BLACK; }
}
