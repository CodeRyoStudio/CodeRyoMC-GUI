package com.example.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public record CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
    public CustomGUI(String name, int rows) {
        this(name, rows, new HashMap<>(Map.of(1, new GUIPage())));
    }

    public Inventory getPage(int pageId) {
        GUIPage page = pages.getOrDefault(pageId, new GUIPage());
        Inventory inv = org.bukkit.Bukkit.createInventory(new GUIHolder(this, pageId), rows * 9, name);
        for (Map.Entry<Integer, GUIItem> entry : page.items().entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().toItemStack());
        }
        return inv;
    }
}