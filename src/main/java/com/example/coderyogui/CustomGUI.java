package com.example.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
    public CustomGUI(String name, int rows) {
        this(name, rows, new HashMap<>(Map.of(1, new GUIPage())));
    }

    public Inventory getPage(int pageId) {
        GUIPage page = pages.getOrDefault(pageId, new GUIPage());
        Inventory inv = org.bukkit.Bukkit.createInventory(new GUIHolder(this, pageId), rows * 9, name);
        // 添加返回主菜單按鈕
        ItemStack returnItem = new ItemStack(Material.BARRIER);
        var meta = returnItem.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "§c返回主菜單");
        meta.setLore(List.of(ChatColor.RESET + "§7點擊返回"));
        returnItem.setItemMeta(meta);
        inv.setItem(0, returnItem);
        // 填充其他項目
        for (Map.Entry<Integer, GUIItem> entry : page.items().entrySet()) {
            if (entry.getKey() != 0) { // 避免覆蓋返回按鈕
                inv.setItem(entry.getKey(), entry.getValue().toItemStack());
            }
        }
        return inv;
    }
}