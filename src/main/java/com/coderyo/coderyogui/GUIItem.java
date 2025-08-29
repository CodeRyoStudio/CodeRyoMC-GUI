package com.coderyo.coderyogui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.coderyo.coderyogui.Constants.*;

public record GUIItem(String material, String name, List<String> lore, boolean takeable, List<GUIAction> actions) {
    public ItemStack toItemStack() {
        Material materialType = Material.matchMaterial(material);
        if (materialType == null) {
            materialType = DEFAULT_MATERIAL_TYPE;
        }
        ItemStack item = new ItemStack(materialType);
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