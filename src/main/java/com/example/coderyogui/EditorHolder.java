package com.example.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EditorHolder implements InventoryHolder {
    private final CustomGUI gui;
    private final int pageId;
    private final int slot;

    public EditorHolder(CustomGUI gui, int pageId) {
        this(gui, pageId, -1);
    }

    public EditorHolder(CustomGUI gui, int pageId, int slot) {
        this.gui = gui;
        this.pageId = pageId;
        this.slot = slot;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public CustomGUI getGUI() {
        return gui;
    }

    public int getPageId() {
        return pageId;
    }

    public int getSlot() {
        return slot;
    }
}