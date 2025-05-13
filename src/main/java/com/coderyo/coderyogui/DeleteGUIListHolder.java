package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class DeleteGUIListHolder implements InventoryHolder {
    private final int page;
    private final String search;

    public DeleteGUIListHolder(int page) {
        this(page, null);
    }

    public DeleteGUIListHolder(int page, String search) {
        this.page = page;
        this.search = search;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getPage() {
        return page;
    }

    public String getSearch() {
        return search;
    }
}