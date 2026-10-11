package com.cursorsize;

import net.runelite.client.config.*;

@ConfigGroup(CursorSizeConfig.GROUP)
public interface CursorSizeConfig extends Config
{
    String GROUP = "cursorsize";

    @ConfigItem(keyName = "style", name = "Cursor style", description = "Follow Custom Cursor uses the cursor selected in RuneLite's Custom Cursor settings, including custom images and equipped weapons. Keep that plugin disabled.", position = 0)
    default CursorStyle style() { return CursorStyle.FOLLOW_CUSTOM_CURSOR; }

    @Range(min = 8, max = 128)
    @ConfigItem(keyName = "size", name = "Cursor size", description = "Longest image edge in pixels (8–128). Large cursors use an in-game overlay when necessary.", position = 1)
    default int size() { return 48; }

    @ConfigItem(keyName = "smooth", name = "Smooth scaling", description = "Smooth image edges; disable for crisp pixel art.", position = 2)
    default boolean smooth() { return false; }
}
