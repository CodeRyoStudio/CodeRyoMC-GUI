package com.example.coderyogui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public record GUIItem(String material, String name, List<String> lore, boolean takeable, List<GUIAction> actions) {
    public ItemStack toItemStack() {
        Material mat = Material.matchMaterial(material);
        if (mat == null) mat = Material.AIR;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (lore != null) meta.setLore(lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).toList());
        item.setItemMeta(meta);
        return item;
    }
}