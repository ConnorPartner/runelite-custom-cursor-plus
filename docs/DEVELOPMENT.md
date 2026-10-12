# Development

## Run locally

Install a JDK (Java 11–24), then run from the repository root:

```powershell
.\gradlew.bat run
```

For Jagex accounts, follow RuneLite's [development login guide](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts). Never commit or share saved login credentials.

## Build and test

```powershell
.\gradlew.bat clean test jar
```

The JAR is written to `build/libs/`; its version comes from `build.gradle`. Use the development launcher for local testing rather than trying to install an arbitrary JAR through Plugin Hub.

## Manual checks

1. Compare 25%, 100%, 200%, and 400% over the game canvas.
2. Switch styles and smoothing; verify that the top-left click point remains accurate.
3. Test a non-square custom PNG and a missing or invalid PNG.
4. Disable and re-enable the plugin; verify normal cursor restoration.
5. Check the sidebar, window boundaries, and display scaling.
6. Check login, world hopping/loading, and logout at 400%: a cursor must remain visible.
7. With Follow Custom Cursor selected, change the original plugin's saved style while it stays disabled. Check custom PNGs and equipped-weapon changes.
8. Check tooltip readability, right-click menus, and Quest Helper highlights with GPU enabled.

## Branch workflow

Create feature, fix, or documentation branches from the current release branch. Merge their PRs into that release branch, then merge the release into `main` when ready. Update the separate Plugin Hub manifest to the released commit; changing this repository alone does not update the Hub submission.

Build setup follows the [official example plugin](https://github.com/runelite/example-plugin).
