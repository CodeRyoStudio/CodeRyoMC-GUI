package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIListHolder implements InventoryHolder {
    private final int page;
    private final boolean editMode;
    private final String search;

    public GUIListHolder(int page, boolean editMode) {
        this(page, editMode, null);
    }

    public GUIListHolder(int page, boolean editMode, String search) {
        this.page = page;
        this.editMode = editMode;
        this.search = search;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getPage() {
        return page;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public String getSearch() {
        return search;
    }
}