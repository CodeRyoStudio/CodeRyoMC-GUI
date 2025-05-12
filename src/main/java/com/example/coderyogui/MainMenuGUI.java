package com.coderyo.coderyogui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGUI {
    private final CoderyoGUI plugin;
    private final int page;

    public MainMenuGUI(CoderyoGUI plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }

    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(new MainMenuHolder(page), 54, "CoderyoGUI 主菜單");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", List.of()));
        }
        inv.setItem(0, createItem(Material.CRAFTING_TABLE, "§a創建新 GUI", List.of("§7點擊創建")));
        inv.setItem(1, createItem(Material.CHEST, "§a打開現有 GUI", List.of("§7點擊查看")));
        inv.setItem(2, createItem(Material.ANVIL, "§a編輯 GUI", List.of("§7點擊編輯")));
        inv.setItem(3, createItem(Material.RED_WOOL, "§c刪除 GUI", List.of("§7點擊刪除")));
        List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet());
        int start = (page - 1) * 45;
        for (int i = 0; i < 45 && start + i < guiNames.size(); i++) {
            String name = guiNames.get(start + i);
            inv.setItem(9 + i, createItem(Material.PAPER, "§a" + name, List.of("§7左鍵打開", "§7右鍵編輯")));
        }
        if (page > 1) {
            inv.setItem(52, createItem(Material.ARROW, "§a上一頁", List.of()));
        }
        if (start + 45 < guiNames.size()) {
            inv.setItem(53, createItem(Material.ARROW, "§a下一頁", List.of()));
        }
        player.openInventory(inv);
        return inv;
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        if (lore != null) {
            meta.setLore(lore.stream().map((String s) -> ChatColor.RESET + s).toList());
        }
        item.setItemMeta(meta);
        return item;
    }
}