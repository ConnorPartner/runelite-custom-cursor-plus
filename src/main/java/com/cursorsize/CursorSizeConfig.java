package com.cursorsize;

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
}
