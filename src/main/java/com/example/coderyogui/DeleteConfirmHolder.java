package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class DeleteConfirmHolder implements InventoryHolder {
    private final String guiName;

    public DeleteConfirmHolder(String guiName) {
        this.guiName = guiName;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public String getGuiName() {
        return guiName;
    }
}