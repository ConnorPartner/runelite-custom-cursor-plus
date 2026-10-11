# Custom Cursor Plus

A RuneLite plugin that makes your mouse cursor bigger or smaller inside the client.

## Features

- Size from **8 to 128 pixels**, defaulting to 48.
- Arrow, RS3 Gold/Silver, Dragon Dagger (including poison), Trout, Dragon Scimitar, and custom PNG images.
- Optional smooth scaling or crisp pixel-art scaling.
- Immediate setting changes and normal cursor restoration on disable.
- Preserves proportions and the top-left click point used by RuneLite Custom Cursor.

## Try it locally

Install a JDK (Java 11–24), then run from this folder:

```powershell
.\gradlew.bat run
```

In the development client, disable **Custom Cursor**, enable **Custom Cursor Plus**, and open its settings. Select **Follow Custom Cursor** to resize the style selected in RuneLite's Custom Cursor settings (including Custom Image and Equipped Weapon), or select a specific style here. Adjust **Cursor size**. Existing installations that have Arrow selected must change the style to Follow Custom Cursor. This plugin replaces Custom Cursor while enabled; the two plugins must not both control the cursor. It supplies its own arrow rather than resizing your Windows cursor theme. With Equipped Weapon selected, an arrow is used when no weapon image is available.

For **Custom Image**, save a PNG at `%USERPROFILE%\.runelite\cursor.png` on Windows or `~/.runelite/cursor.png` on Linux/macOS. After replacing the file, toggle the plugin or change a setting to reload. Missing or invalid files restore the normal cursor and write a warning to the RuneLite log.

Operating systems can limit native cursor sizes (including Java on Windows). For larger sizes while logged in, the plugin hides the native cursor over the game canvas and draws the full-size image as an overlay. This fallback follows the game's frame rate and is clipped at the canvas edges. RuneLite does not render overlays on login/loading screens, so those screens and the sidebar use a visible cursor limited to the supported native size. Smaller images receive transparent padding to prevent unwanted stretching. Display scaling also affects apparent size. The setting measures the longest image edge, including transparent margins.

## Build and test

```powershell
.\gradlew.bat clean test jar
```

Output: `build/libs/custom-cursor-plus-1.0.0.jar`. RuneLite does not install arbitrary JARs through the normal Plugin Hub interface; use the development launcher for local testing. Public distribution requires a separate Plugin Hub submission and review.

## Manual checks

1. Compare 8, 48, and 128 pixels over the game canvas.
2. Switch styles and smoothing; verify that the top-left tip clicks accurately.
3. Test a non-square custom PNG and a missing/invalid PNG.
4. Disable/re-enable the plugin and verify normal cursor restoration.
5. Check the sidebar, window boundaries, and your normal display scaling.
6. Check login, world hopping/loading, and logout at 128 pixels: the native cursor must remain visible whenever RuneLite suppresses overlays.
7. Choose Follow Custom Cursor and change Custom Cursor's saved style while that plugin is disabled. Verify gold, dagger, custom PNG, and equipped weapon selection and weapon swaps.

Build setup follows the [official example plugin](https://github.com/runelite/example-plugin). Cursor integration uses RuneLite's [ClientUI API](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/ui/ClientUI.java).
