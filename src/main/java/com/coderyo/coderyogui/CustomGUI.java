package com.coderyo.coderyogui;

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
        if (!pages.containsKey(1)) {
            pages.put(1, new GUIPage());
        }
    }

    public CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
        this.name = name;
        this.rows = rows;
        this.pages = new HashMap<>(pages);
        if (this.pages.isEmpty()) {
            this.pages.put(1, new GUIPage());
        }
    }

    public Inventory getPage(int pageId) {
        GUIPage page = pages.getOrDefault(pageId, new GUIPage());
        if (!pages.containsKey(pageId)) {
            pages.put(pageId, page);
        }
        Inventory inv = org.bukkit.Bukkit.createInventory(new GUIHolder(this, pageId), rows * 9, name);

        // 槽位0：返回主菜單按鈕
        ItemStack returnItem = new ItemStack(Material.BARRIER);
        var meta = returnItem.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "§c返回主菜單");
        meta.setLore(List.of(ChatColor.RESET + "§7點擊返回"));
        returnItem.setItemMeta(meta);
        inv.setItem(0, returnItem);

        // 槽位1-8：灰色玻璃片（不可交互）
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        meta = glassPane.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + " ");
        glassPane.setItemMeta(meta);
        for (int i = 1; i <= 8; i++) {
            inv.setItem(i, glassPane);
        }

        // 槽位9及以上：從GUIPage.items填充（0-based索引）
        for (Map.Entry<Integer, GUIItem> entry : page.items().entrySet()) {
            int slot = entry.getKey();
            if (slot >= 9 && slot < rows * 9) { // 確保不覆蓋槽位0-8
                inv.setItem(slot, entry.getValue().toItemStack());
            }
        }

        return inv;
    }
}