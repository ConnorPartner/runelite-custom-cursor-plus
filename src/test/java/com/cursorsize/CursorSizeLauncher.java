package com.cursorsize;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CursorSizeLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(CursorSizePlugin.class);
        RuneLite.main(args);
    }
}
