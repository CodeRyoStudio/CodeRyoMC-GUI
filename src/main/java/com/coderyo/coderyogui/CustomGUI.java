package com.coderyo.coderyogui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coderyo.coderyogui.Constants.*;

public record CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
    public CustomGUI(String name, int rows) {
        this(name, rows, new HashMap<>());
        if (!pages.containsKey(DEFAULT_GUI_PAGE)) {
            pages.put(DEFAULT_GUI_PAGE, new GUIPage());
        }
    }

    public CustomGUI(String name, int rows, Map<Integer, GUIPage> pages) {
        this.name = name;
        this.rows = rows;
        this.pages = new HashMap<>(pages);
        if (this.pages.isEmpty()) {
            this.pages.put(DEFAULT_GUI_PAGE, new GUIPage());
        }
    }

    public Inventory getPage(int pageId) {
        GUIPage page = pages.getOrDefault(pageId, new GUIPage());
        if (!pages.containsKey(pageId)) {
            pages.put(pageId, page);
        }
        Inventory inventory = org.bukkit.Bukkit.createInventory(new GUIHolder(this, pageId), rows * 9, name);

        // 槽位0：返回主菜單按鈕
        ItemStack returnItem = new ItemStack(Material.BARRIER);
        var itemMeta = returnItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + DISPLAY_RETURN_MAIN_MENU);
        itemMeta.setLore(List.of(ChatColor.RESET + LORE_CLICK_RETURN));
        returnItem.setItemMeta(itemMeta);
        inventory.setItem(0, returnItem);

        // 槽位1-8：灰色玻璃片（不可交互）
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        itemMeta = glassPane.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + EMPTY_STRING);
        glassPane.setItemMeta(itemMeta);
        for (int slotIndex = 1; slotIndex <= 8; slotIndex++) {
            inventory.setItem(slotIndex, glassPane);
        }

        // 槽位9及以上：從GUIPage.items填充（0-based索引）
        for (Map.Entry<Integer, GUIItem> entry : page.items().entrySet()) {
            int slot = entry.getKey();
            if (slot >= CONTENT_AREA_START && slot < rows * 9) { // 確保不覆蓋槽位0-8
                inventory.setItem(slot, entry.getValue().toItemStack());
            }
        }

        return inventory;
    }
}