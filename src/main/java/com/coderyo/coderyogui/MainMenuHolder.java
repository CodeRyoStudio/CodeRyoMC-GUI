package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MainMenuHolder implements InventoryHolder {
    private final int page;

    public MainMenuHolder(int page) {
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getPage() {
        return page;
    }
}