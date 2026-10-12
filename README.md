# Custom Cursor Plus

A RuneLite plugin that makes your mouse cursor bigger or smaller inside the client.

## Features

- Scale from **25% to 400%**, defaulting to **150%**. 100% uses the original image dimensions; 200% doubles width and height.
- Arrow, RS3 Gold/Silver, Dragon Dagger (including poison), Trout, Dragon Scimitar, and custom PNG images.
- Optional smooth scaling or crisp pixel-art scaling.
- Immediate setting changes and normal cursor restoration on disable.
- Preserves proportions and the top-left click point used by RuneLite Custom Cursor.

## Try it locally

Install a JDK (Java 11–24), then run from this folder:

```powershell
.\gradlew.bat run
```

In the development client, disable **Custom Cursor**, enable **Custom Cursor Plus**, and open its settings. Select **Follow Custom Cursor** to resize the style selected in RuneLite's Custom Cursor settings (including Custom Image and Equipped Weapon), or select a specific style here. Adjust **Cursor scale (%)**. Existing installations that have Arrow selected must change the style to Follow Custom Cursor. This plugin replaces Custom Cursor while enabled; the two plugins must not both control the cursor. It supplies its own arrow rather than resizing your Windows cursor theme. With Equipped Weapon selected, an arrow is used when no weapon image is available.

For **Custom Image**, save a PNG at `%USERPROFILE%\.runelite\plugin-data\custom-cursor-plus\cursor.png` on Windows or `~/.runelite/plugin-data/custom-cursor-plus/cursor.png` on Linux/macOS. The plugin creates its managed folder when Custom Image is selected. Existing users must copy their old `.runelite/cursor.png` here once; the shared file is no longer accessed. This also applies when Follow Custom Cursor selects Custom Image. After replacing the file, toggle the plugin or change a setting to reload. Missing or invalid files restore the normal cursor and write a warning to the RuneLite log.

Operating systems can limit native cursor sizes (including Java on Windows). For larger sizes while logged in, the plugin hides the native cursor over the game canvas and draws the full-size image as an overlay. This fallback follows the game's frame rate and is clipped at the canvas edges. RuneLite does not render overlays on login/loading screens, so those screens and the sidebar use a visible cursor limited to the supported native size. Smaller images receive transparent padding to prevent unwanted stretching. Display scaling also affects apparent size. Scaling includes transparent margins. Very large custom images are capped at 512 pixels on the longest edge, preserving their proportions. Saved pixel settings are converted once to the nearest supported percentage for the selected image; after that, switching styles retains the percentage.

## Build and test

```powershell
.\gradlew.bat clean test jar
```

Output: `build/libs/custom-cursor-plus-1.0.0.jar`. RuneLite does not install arbitrary JARs through the normal Plugin Hub interface; use the development launcher for local testing. Public distribution requires a separate Plugin Hub submission and review.

## Manual checks

1. Compare 25%, 100%, 200%, and 400% over the game canvas.
2. Switch styles and smoothing; verify that the top-left tip clicks accurately.
3. Test a non-square custom PNG and a missing/invalid PNG.
4. Disable/re-enable the plugin and verify normal cursor restoration.
5. Check the sidebar, window boundaries, and your normal display scaling.
6. Check login, world hopping/loading, and logout at 400%: the native cursor must remain visible whenever RuneLite suppresses overlays.
7. Choose Follow Custom Cursor and change Custom Cursor's saved style while that plugin is disabled. Verify gold, dagger, custom PNG, and equipped weapon selection and weapon swaps.

Build setup follows the [official example plugin](https://github.com/runelite/example-plugin). Cursor integration uses RuneLite's [ClientUI API](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/ui/ClientUI.java).
