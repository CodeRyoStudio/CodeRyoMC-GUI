package com.coderyo.coderyogui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public record GUIItem(String material, String name, List<String> lore, boolean takeable, List<GUIAction> actions) {
    public ItemStack toItemStack() {
        Material mat = Material.matchMaterial(material);
        if (mat == null) {
            mat = Material.AIR;
        }
        ItemStack item = new ItemStack(mat);
        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (name != null) {
                    meta.setDisplayName(name);
                }
                if (lore != null) {
                    meta.setLore(lore);
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}