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
        this(name, rows, new HashMap<>());
        // 確保至少有一頁
        if (!pages.containsKey(1)) {
            pages.put(1, new GUIPage());
        }
    }

    public CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
        this.name = name;
        this.rows = rows;
        this.pages = new HashMap<>(pages);
        // 確保 pages 不為空並包含至少一頁
        if (this.pages.isEmpty()) {
            this.pages.put(1, new GUIPage());
        }
    }

    public Inventory getPage(int pageId) {
        GUIPage page = pages.getOrDefault(pageId, new GUIPage());
        // 如果頁面不存在，則添加到 pages 中
        if (!pages.containsKey(pageId)) {
            pages.put(pageId, page);
        }
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