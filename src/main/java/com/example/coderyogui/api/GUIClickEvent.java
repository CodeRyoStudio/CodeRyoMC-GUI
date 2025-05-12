package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GUIClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CustomGUI gui;
    private final int pageId;
    private final int slot;
    private final GUIItem item;
    private boolean cancelled;

    public GUIClickEvent(Player player, CustomGUI gui, int pageId, int slot, GUIItem item) {
        this.player = player;
        this.gui = gui;
        this.pageId = pageId;
        this.slot = slot;
        this.item = item;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CustomGUI getGui() {
        return gui;
    }

    public int getPageId() {
        return pageId;
    }

    public int getSlot() {
        return slot;
    }

    public GUIItem getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}