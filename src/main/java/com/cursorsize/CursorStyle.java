package com.cursorsize;

import net.runelite.client.plugins.customcursor.CustomCursor;

public enum CursorStyle
{
    FOLLOW_CUSTOM_CURSOR("Follow Custom Cursor", null),
    ARROW("Arrow", null),
    GOLD("RS3 Gold", CustomCursor.RS3_GOLD),
    SILVER("RS3 Silver", CustomCursor.RS3_SILVER),
    DAGGER("Dragon Dagger", CustomCursor.DRAGON_DAGGER),
    POISON_DAGGER("Dragon Dagger (p)", CustomCursor.DRAGON_DAGGER_POISON),
    TROUT("Trout", CustomCursor.TROUT),
    SCIMITAR("Dragon Scimitar", CustomCursor.DRAGON_SCIMITAR),
    CUSTOM_IMAGE("Custom Image", null);

    private final String label;
    final CustomCursor builtIn;

    CursorStyle(String label, CustomCursor builtIn)
    {
        this.label = label;
        this.builtIn = builtIn;
    }

    @Override
    public String toString() { return label; }

    CustomCursor selectedCursor(CustomCursor customCursor)
    {
        if (this == FOLLOW_CUSTOM_CURSOR) { return customCursor; }
        if (this == CUSTOM_IMAGE) { return CustomCursor.CUSTOM_IMAGE; }
        return builtIn;
    }
}
