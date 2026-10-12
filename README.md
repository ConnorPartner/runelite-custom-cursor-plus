# Custom Cursor Plus

Keep your cursor easy to spot while you play. Custom Cursor Plus lets you choose a RuneLite cursor style or your own image, then make it bigger or smaller to suit your screen.

## Features

- **Cursor outline:** choose a colour and thickness from 1–5 pixels to make your cursor easier to see. Set thickness to 0 (the default) to disable it. The outline is drawn inside the visible edge after scaling, so the click point and cursor dimensions stay unchanged. Thick outlines can cover fine details on small cursors.


- **Adjustable scale:** choose from 25% to 400%, with 150% as the default.
- **Familiar cursor styles:** Arrow, RS3 Gold, RS3 Silver, Dragon Dagger, Dragon Dagger (p), Trout, and Dragon Scimitar.
- **Your own cursor:** use a custom PNG image.
- **Equipped weapon:** follow the equipped-weapon option from RuneLite's Custom Cursor settings.
- **Smooth or crisp edges:** enable Smooth scaling for softer edges, or leave it off for pixel art.

Changes apply immediately. Disabling the plugin restores your normal cursor.

## Getting started

1. Open the **Plugin Hub** in RuneLite and search for **Custom Cursor Plus**.
2. Install the plugin and open its settings using the cog icon.
3. Keep RuneLite's original **Custom Cursor** plugin disabled to avoid conflicts.
4. Choose a **Cursor style** and adjust **Cursor scale (%)**.

| Scale | Appearance |
| --- | --- |
| 50% | Half the original width and height |
| 100% | Original image size |
| 150% | One and a half times the original size (default) |
| 200% | Double the original width and height |

Each style scales relative to its own image, so different cursors can look different sizes at the same percentage.

## Find your cursor with a hotkey

In **Cursor highlight**, assign a **Highlight hotkey**. While logged in, press it to show a ring around your cursor's click point. The ring follows your mouse in the game area and disappears automatically; press again to restart the timer.

Choose a **Highlight colour**, **Ring radius** (10-150 pixels), and **Highlight duration (ms)** (100-5000 milliseconds). Defaults are a yellow ring with a 40-pixel radius lasting one second. The hotkey is unassigned until you choose one. The ring works with all cursor styles and scales, and clears when you switch away from the client or log out.

## Follow your existing cursor choice

Select **Follow Custom Cursor** to use the style saved in RuneLite's original **Custom Cursor** settings. This includes **Equipped Weapon** and **Custom Image**.

You can change that saved selection while the original plugin remains disabled. If Equipped Weapon is selected but no weapon image is available, an arrow is shown instead.

## Use a custom image

Select **Custom Image**, then place a PNG named `cursor.png` in the folder below. The folder is created when you select this option.

**Windows** — paste this folder into File Explorer's address bar:

```text
%USERPROFILE%\.runelite\plugin-data\custom-cursor-plus
```

**macOS / Linux:**

```text
~/.runelite/plugin-data/custom-cursor-plus
```

After adding or replacing the image, change a plugin setting or toggle the plugin off and on to reload it. This location also applies when using **Follow Custom Cursor → Custom Image**.

If you previously used `.runelite/cursor.png`, copy it into the new folder. A missing or unreadable image falls back to the normal cursor.

For best results, use a transparent background with little empty space around the image. The click point is the image's top-left corner. Very large images are limited to 512 pixels on their longest edge, keeping their proportions.

## Why does the cursor look smaller on the login screen or sidebar?

Your operating system limits the size of native cursors. While logged in, the plugin can display larger cursors over the game area. The login screen, loading screens, and sidebar remain subject to the native size limit.

Large in-game cursors follow the game's frame rate and may be clipped at the edges of the game area. Display scaling and transparent space in custom images can also affect their apparent size.

## Help and feedback

Found a problem or have a feature suggestion? [Open an issue on GitHub](https://github.com/ConnorPartner/runelite-custom-cursor-plus/issues). For display problems, include your cursor style, scale, and whether GPU or 117 HD is enabled.
