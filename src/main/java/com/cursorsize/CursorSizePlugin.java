package com.cursorsize;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.runelite.client.util.Filepath;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.customcursor.CustomCursor;
import net.runelite.client.plugins.customcursor.CustomCursorConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name = "Custom Cursor Plus", internalName = "custom-cursor-plus", description = "Make your cursor bigger or smaller", tags = {"mouse", "accessibility"}, conflicts = {"Custom Cursor"})
public class CursorSizePlugin extends Plugin
{
    private static final Logger log = LoggerFactory.getLogger(CursorSizePlugin.class);
    @Inject private ClientUI clientUI;
    @Inject private CursorSizeConfig config;
    @Inject private Client client;
    @Inject private CursorSizeOverlay overlay;
    @Inject private CursorForegroundOverlay foregroundOverlay;
    @Inject private OverlayManager overlayManager;
    @Inject private ConfigManager configManager;
    @Inject private ClientThread clientThread;
    @Inject private ItemManager itemManager;
    private volatile boolean active;
    private volatile boolean loggedIn;
    private volatile BufferedImage weaponImage;
    // EDT-owned. Hide the native cursor only after a software frame really drew.
    private Cursor transparentCursor;
    private Cursor nativeCursor;
    private final Timer visibilityTimer = new Timer(50, event -> updateCanvasVisibility());

    @Provides
    CursorSizeConfig provideConfig(ConfigManager manager) { return manager.getConfig(CursorSizeConfig.class); }

    @Override
    protected void startUp()
    {
        active = true;
        loggedIn = client.getGameState() == GameState.LOGGED_IN;
        overlayManager.add(overlay);
        overlayManager.add(foregroundOverlay);
        updateCursor();
        refreshWeapon();
    }

    @Override
    protected void shutDown() throws Exception
    {
        active = false;
        overlayManager.remove(overlay);
        overlayManager.remove(foregroundOverlay);
        if (SwingUtilities.isEventDispatchThread()) { resetCursor(); }
        else { SwingUtilities.invokeAndWait(this::resetCursor); }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if ((CursorSizeConfig.GROUP.equals(event.getGroup()) || "customcursor".equals(event.getGroup())) && active)
        {
            updateCursor();
            refreshWeapon();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        loggedIn = event.getGameState() == GameState.LOGGED_IN;
        if (active)
        {
            updateCursor();
            refreshWeapon();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (active && event.getContainerId() == InventoryID.WORN) { refreshWeapon(); }
    }

    @Subscribe
    public void onBeforeRender(net.runelite.api.events.BeforeRender event)
    {
        overlay.beginFrame();
    }

    private CustomCursor selectedCursor()
    {
        return config.style().selectedCursor(configManager.getConfig(CustomCursorConfig.class).selectedCursor());
    }

    private void refreshWeapon()
    {
        clientThread.invokeLater(() ->
        {
            if (!active || selectedCursor() != CustomCursor.EQUIPPED_WEAPON) { return true; }
            ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
            Item weapon = equipment == null ? null : equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
            weaponImage = weapon == null || weapon.getQuantity() <= 0 ? null : itemManager.getImage(weapon.getId());
            updateCursor();
            return true;
        });
    }

    static boolean useOverlay(boolean loggedIn, BufferedImage image, Dimension nativeSize)
    {
        return loggedIn && (image.getWidth() > nativeSize.width || image.getHeight() > nativeSize.height);
    }

    private void updateCursor()
    {
        SwingUtilities.invokeLater(() ->
        {
            if (!active) { return; }
            try
            {
                CustomCursor selected = selectedCursor();
                BufferedImage source;
                if (selected == CustomCursor.CUSTOM_IMAGE)
                {
                    Filepath imagePath = getPluginDirectory().join("cursor.png");
                    try (InputStream input = imagePath.openInputStream())
                    {
                        source = CursorImages.read(input);
                    }
                    if (source == null) { throw new IOException("cursor.png is not a supported image"); }
                }
                else if (selected == CustomCursor.EQUIPPED_WEAPON)
                {
                    source = weaponImage == null ? CursorImages.arrow() : weaponImage;
                }
                else { source = selected == null ? CursorImages.arrow() : selected.getCursorImage(); }
                int percentage = config.scalePercent();
                // Preserve the old apparent size once, without treating pixel values as percentages.
                if (!"true".equals(configManager.getConfiguration(CursorSizeConfig.GROUP, "percentageMigrated")))
                {
                    String legacy = configManager.getConfiguration(CursorSizeConfig.GROUP, "size");
                    if (legacy != null && (selected != CustomCursor.EQUIPPED_WEAPON || weaponImage != null))
                    {
                        try
                        {
                            // RuneLite writes defaults before startup, so a null check is insufficient.
                            if (percentage == 150) { percentage = CursorImages.legacyPercentage(source, Integer.parseInt(legacy)); }
                            configManager.setConfiguration(CursorSizeConfig.GROUP, "percentageMigrated", true);
                            configManager.setConfiguration(CursorSizeConfig.GROUP, "scalePercent", percentage);
                        }
                        catch (NumberFormatException e)
                        {
                            log.warn("Ignoring invalid legacy cursor size");
                        }
                    }
                }
                BufferedImage scaled = CursorImages.scale(source, percentage, config.smooth());
                scaled = CursorImages.outline(scaled, config.outlineThickness(), config.outlineColor());
                Dimension nativeSize = Toolkit.getDefaultToolkit().getBestCursorSize(scaled.getWidth(), scaled.getHeight());
                clientUI.setCursor(CursorImages.fitNativeCanvas(scaled, nativeSize), "Custom Cursor Plus");
                nativeCursor = clientUI.getDefaultCursor();
                log.info("Custom Cursor Plus: style={}, selected={}, size={}x{}, native={}x{}, loggedIn={}, software={}",
                    config.style(), selected, scaled.getWidth(), scaled.getHeight(), nativeSize.width,
                    nativeSize.height, loggedIn, useOverlay(loggedIn, scaled, nativeSize));
                if (useOverlay(loggedIn, scaled, nativeSize))
                {
                    overlay.setImage(scaled);
                    BufferedImage blank = new BufferedImage(nativeSize.width, nativeSize.height, BufferedImage.TYPE_INT_ARGB);
                    transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(blank,
                        new java.awt.Point(0, 0), "Custom Cursor Plus overlay");
                    client.getCanvas().setCursor(nativeCursor);
                    visibilityTimer.start();
                }
                else
                {
                    visibilityTimer.stop();
                    transparentCursor = null;
                    overlay.setImage(null);
                    client.getCanvas().setCursor(nativeCursor);
                }
            }
            catch (IOException | RuntimeException e)
            {
                log.warn("Unable to apply Custom Cursor Plus; restoring the system cursor", e);
                resetCursor();
            }
        });
    }

    private void resetCursor()
    {
        visibilityTimer.stop();
        transparentCursor = null;
        nativeCursor = null;
        overlay.setImage(null);
        client.getCanvas().setCursor(null);
        clientUI.resetCursor();
    }

    private void updateCanvasVisibility()
    {
        Cursor desired = active && loggedIn && overlay.isRendering(System.nanoTime()) ? transparentCursor : nativeCursor;
        if (client.getCanvas().getCursor() != desired) { client.getCanvas().setCursor(desired); }
    }
}
