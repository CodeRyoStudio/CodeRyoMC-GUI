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
import java.util.stream.Collectors;

public class GUIListGUI {
    private final CoderyoGUI plugin;
    private final GUIManager guiManager;
    private final int page;
    private final boolean editMode;
    private final String search;

    public GUIListGUI(CoderyoGUI plugin, int page, boolean editMode, String search) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.page = page;
        this.editMode = editMode;
        this.search = search;
    }

    public Inventory open(Player player) {
        LanguageManager lang = plugin.getLanguageManager();
        try {
            String title = search == null ? lang.getTranslation(player, editMode ? "gui.title.edit_gui_list" : "gui.title.open_gui_list") :
                    lang.getTranslation(player, editMode ? "gui.title.search_edit_gui" : "gui.title.search_open_gui", search);
            Inventory inv = Bukkit.createInventory(new GUIListHolder(page, editMode, search), 54, title);
            for (int i = 0; i < 54; i++) {
                inv.setItem(i, createItem(player, Material.GRAY_STAINED_GLASS_PANE, " ", null));
            }
            inv.setItem(0, createItem(player, Material.BARRIER, "item.return.name", List.of("item.return.lore")));
            inv.setItem(1, createItem(player, Material.COMPASS, "item.search_gui.name", List.of("item.search_gui.lore")));
            List<String> guiNames = new ArrayList<>(guiManager.getGUIs().keySet()).stream()
                    .filter(name -> search == null || name.toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            int start = (page - 1) * 45;
            for (int i = 0; i < 45 && start + i < guiNames.size(); i++) {
                String name = guiNames.get(start + i);
                inv.setItem(9 + i, createItem(player, Material.PAPER, "item.gui_item.name", List.of(editMode ? "item.gui_item.lore_edit" : "item.gui_item.lore_open"), name));
            }
            if (page > 1) {
                inv.setItem(52, createItem(player, Material.ARROW, "item.prev_page.name", List.of()));
            } else {
                inv.setItem(52, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.prev_page_none.name", List.of()));
            }
            if (start + 45 < guiNames.size()) {
                inv.setItem(53, createItem(player, Material.ARROW, "item.next_page.name", List.of()));
            } else {
                inv.setItem(53, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.next_page_none.name", List.of()));
            }
            if (guiNames.isEmpty()) {
                inv.setItem(9, createItem(player, Material.BOOK, "item.no_matching_gui.name", List.of("item.no_matching_gui.lore")));
            }
            player.openInventory(inv);
            plugin.getLogger().info("Opening GUI list for player " + player.getName() + ", page " + page + ", mode: " + (editMode ? "edit" : "open") + ", search: " + (search != null ? search : "none"));
            return inv;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to open GUI list: " + e.getMessage());
            player.sendMessage(lang.getTranslation(player, "message.gui_list_failed"));
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