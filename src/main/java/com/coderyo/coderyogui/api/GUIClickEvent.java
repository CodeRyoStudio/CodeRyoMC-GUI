package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired when a player clicks a slot in a CoderyoGUI custom GUI.
 * This event can be cancelled to prevent the execution of associated {@link com.coderyo.coderyogui.GUIAction}s.
 */
public class GUIClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CustomGUI gui;
    private final int pageId;
    private final int slot;
    private final GUIItem item;
    private final boolean isBackButton;
    private boolean cancelled;

    /**
     * Constructs a new GUIClickEvent.
     *
     * @param player The player who clicked the GUI.
     * @param gui    The CustomGUI that was clicked.
     * @param pageId The ID of the page clicked.
     * @param slot   The slot index clicked.
     * @param item   The GUIItem in the slot, or null if the slot is empty.
     */
    public GUIClickEvent(Player player, CustomGUI gui, int pageId, int slot, GUIItem item) {
        this(player, gui, pageId, slot, item, slot == 0);
    }

    /**
     * Constructs a new GUIClickEvent with back button indicator.
     *
     * @param player      The player who clicked the GUI.
     * @param gui         The CustomGUI that was clicked.
     * @param pageId      The ID of the page clicked.
     * @param slot        The slot index clicked.
     * @param item        The GUIItem in the slot, or null if the slot is empty.
     * @param isBackButton True if the clicked slot is the back button (slot 0).
     */
    public GUIClickEvent(Player player, CustomGUI gui, int pageId, int slot, GUIItem item, boolean isBackButton) {
        this.player = player;
        this.gui = gui;
        this.pageId = pageId;
        this.slot = slot;
        this.item = item;
        this.isBackButton = isBackButton;
        this.cancelled = false;
    }

    /**
     * Gets the player who clicked the GUI.
     *
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the CustomGUI that was clicked.
     *
     * @return The CustomGUI instance.
     */
    public CustomGUI getGui() {
        return gui;
    }

    /**
     * Gets the ID of the page clicked.
     *
     * @return The page ID.
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Gets the slot index clicked.
     *
     * @return The slot index.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets the GUIItem in the clicked slot, if any.
     *
     * @return The GUIItem, or null if the slot is empty.
     */
    public GUIItem getItem() {
        return item;
    }

    /**
     * Checks if the clicked slot is the back button (slot 0).
     *
     * @return True if the slot is the back button, false otherwise.
     */
    public boolean isBackButton() {
        return isBackButton;
    }

    /**
     * Checks if the event has been cancelled.
     *
     * @return true if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether the event is cancelled.
     * If cancelled, associated {@link com.coderyo.coderyogui.GUIAction} executions will be prevented.
     *
     * @param cancelled true to cancel the event, false to allow it.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the list of event handlers.
     *
     * @return The HandlerList for this event.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the static HandlerList for this event type.
     *
     * @return The static HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}