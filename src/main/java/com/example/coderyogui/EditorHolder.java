package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EditorHolder implements InventoryHolder {
    private final CustomGUI gui;
    private final int pageId;
    private final int slot;
    private final int searchPage;
    private final String search;

    public EditorHolder(CustomGUI gui, int pageId) {
        this(gui, pageId, -1, 1, null);
    }

    public EditorHolder(CustomGUI gui, int pageId, int slot) {
        this(gui, pageId, slot, 1, null);
    }

    public EditorHolder(CustomGUI gui, int pageId, int slot, int searchPage) {
        this(gui, pageId, slot, searchPage, null);
    }

    public EditorHolder(CustomGUI gui, int pageId, int slot, int searchPage, String search) {
        this.gui = gui;
        this.pageId = pageId;
        this.slot = slot;
        this.searchPage = searchPage;
        this.search = search;
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

    public int getSearchPage() {
        return searchPage;
    }

    public String getSearch() {
        return search;
    }
}