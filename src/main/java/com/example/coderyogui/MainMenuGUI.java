package com.example.coderyogui;

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
    private final GUIManager guiManager;
    private final int page;

    public MainMenuGUI(CoderyoGUI plugin, int page) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.page = page;
    }

    public Inventory open(Player player) {
        LanguageManager lang = plugin.getLanguageManager();
        try {
            Inventory inv = Bukkit.createInventory(new MainMenuHolder(page), 54, lang.getTranslation(player, "gui.title.main_menu"));
            for (int i = 0; i < 54; i++) {
                inv.setItem(i, createItem(player, Material.GRAY_STAINED_GLASS_PANE, " ", null));
            }
            inv.setItem(0, createItem(player, Material.WRITABLE_BOOK, "item.create_gui.name", List.of("item.create_gui.lore")));
            inv.setItem(1, createItem(player, Material.CHEST, "item.open_gui.name", List.of("item.open_gui.lore")));
            inv.setItem(2, createItem(player, Material.ANVIL, "item.edit_gui.name", List.of("item.edit_gui.lore")));
            inv.setItem(3, createItem(player, Material.RED_WOOL, "item.delete_gui.name", List.of("item.delete_gui.lore")));
            List<String> guiNames = new ArrayList<>(guiManager.getGUIs().keySet());
            int start = (page - 1) * 45;
            for (int i = 0; i < 45 && start + i < guiNames.size(); i++) {
                String name = guiNames.get(start + i);
                inv.setItem(9 + i, createItem(player, Material.PAPER, "item.gui_item.name", List.of("item.gui_item.lore_open", "item.gui_item.lore_edit"), name));
            }
            if (page > 1) {
                inv.setItem(52, createItem(player, Material.ARROW, "item.prev_page.name", List.of()));
            }
            if (start + 45 < guiNames.size()) {
                inv.setItem(53, createItem(player, Material.ARROW, "item.next_page.name", List.of()));
            }
            player.openInventory(inv);
            plugin.getLogger().info("Opening main menu for player " + player.getName() + ", page " + page);
            return inv;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to open main menu: " + e.getMessage());
            player.sendMessage(lang.getTranslation(player, "message.main_menu_failed"));
            return null;
        }
    }

    private ItemStack createItem(Player player, Material material, String nameKey, List<String> loreKeys, Object... args) {
        LanguageManager lang = plugin.getLanguageManager();
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + lang.getTranslation(player, nameKey, args));
        if (loreKeys != null) {
            meta.setLore(loreKeys.stream().map(key -> ChatColor.RESET + lang.getTranslation(player, key)).toList());
        }
        item.setItemMeta(meta);
        return item;
    }
}