package com.cursorsize;

import java.awt.Color;
import net.runelite.client.config.*;

@ConfigGroup(CursorSizeConfig.GROUP)
public interface CursorSizeConfig extends Config
{
    String GROUP = "cursorsize";

    @ConfigSection(name = "Cursor", description = "Choose your cursor style, scale, and smoothing.", position = 0)
    String CURSOR_SECTION = "cursor";

    @ConfigSection(name = "Outline", description = "Make your cursor easier to see with a coloured outline.", position = 1)
    String OUTLINE_SECTION = "outline";

    @ConfigSection(name = "Cursor highlight", description = "Find your cursor with a temporary ring.", position = 2)
    String HIGHLIGHT_SECTION = "highlight";

    @ConfigItem(keyName = "style", name = "Cursor style", description = "Follow Custom Cursor uses the cursor selected in RuneLite's Custom Cursor settings, including custom images and equipped weapons. Keep that plugin disabled.", position = 0, section = CURSOR_SECTION)
    default CursorStyle style() { return CursorStyle.FOLLOW_CUSTOM_CURSOR; }

    @Range(min = 25, max = 400)
    @ConfigItem(keyName = "scalePercent", name = "Cursor scale (%)", description = "25–400%. 100% is the original image size; 200% doubles it. Maximum rendered edge: 512 pixels.", position = 1, section = CURSOR_SECTION)
    default int scalePercent() { return 150; }

    @ConfigItem(keyName = "smooth", name = "Smooth scaling", description = "Smooth image edges; disable for crisp pixel art.", position = 2, section = CURSOR_SECTION)
    default boolean smooth() { return false; }
    @Range(min = 0, max = 5)
    @ConfigItem(keyName = "outlineThickness", name = "Outline thickness", description = "Outline width in pixels after scaling (0–5). 0 disables it. Drawn inside the visible edge to preserve the click point.", position = 3, section = OUTLINE_SECTION)
    default int outlineThickness() { return 0; }

    @ConfigItem(keyName = "outlineColor", name = "Outline colour", description = "Colour of the cursor outline. Set Outline thickness above 0 to enable it.", position = 4, section = OUTLINE_SECTION)
    default Color outlineColor() { return Color.BLACK; }

    @ConfigItem(keyName = "highlightHotkey", name = "Highlight hotkey", description = "Press to highlight the cursor in the game area while logged in. Unassigned by default.", position = 0, section = HIGHLIGHT_SECTION)
    default Keybind highlightHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(keyName = "highlightColor", name = "Highlight colour", description = "Colour of the locator ring.", position = 1, section = HIGHLIGHT_SECTION)
    default Color highlightColor() { return Color.YELLOW; }

    @Range(min = 10, max = 150)
    @ConfigItem(keyName = "highlightRadius", name = "Ring radius", description = "Distance from the click point to the ring in pixels (10-150).", position = 2, section = HIGHLIGHT_SECTION)
    default int highlightRadius() { return 40; }

    @Range(min = 100, max = 5000)
    @ConfigItem(keyName = "highlightDuration", name = "Highlight duration (ms)", description = "Time the ring stays visible (100-5000 milliseconds). Press again to restart the timer.", position = 3, section = HIGHLIGHT_SECTION)
    default int highlightDuration() { return 1000; }
}
