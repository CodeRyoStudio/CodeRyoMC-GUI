package com.example.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EditorHolder implements InventoryHolder {
    private final CustomGUI gui;
    private final int pageId;

    public EditorHolder(CustomGUI gui, int pageId) {
        this.gui = gui;
        this.pageId = pageId;
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
}