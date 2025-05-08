package com.example.coderyogui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GUIEditor {
    public static void openEditor(Player player, CustomGUI gui, int pageId) {
        LanguageManager lang = ((CoderyoGUI) Bukkit.getPluginManager().getPlugin("CoderyoGUI")).getLanguageManager();
        Inventory editor = Bukkit.createInventory(new EditorHolder(gui, pageId), 54, lang.getTranslation(player, "gui.title.edit", gui.name()));
        for (int i = 0; i < 54; i++) {
            editor.setItem(i, createItem(player, Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        editor.setItem(0, createItem(player, Material.BARRIER, "item.return.name", List.of("item.return.lore")));
        editor.setItem(1, createItem(player, Material.WRITABLE_BOOK, "item.set_name.name", List.of("item.set_name.lore")));
        editor.setItem(2, createItem(player, Material.PAPER, "item.set_rows.name", List.of("item.set_rows.lore"), gui.rows()));
        editor.setItem(3, createItem(player, Material.GREEN_WOOL, "item.add_page.name", List.of("item.add_page.lore")));
        editor.setItem(4, createItem(player, Material.RED_WOOL, "item.delete_page.name", List.of("item.delete_page.lore")));
        if (pageId > 1) {
            editor.setItem(5, createItem(player, Material.ARROW, "item.prev_page.name", List.of("item.prev_page.lore"), pageId, gui.pages().size()));
        } else {
            editor.setItem(5, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.prev_page_none.name", List.of()));
        }
        if (pageId < gui.pages().size()) {
            editor.setItem(6, createItem(player, Material.ARROW, "item.next_page.name", List.of("item.next_page.lore"), pageId, gui.pages().size()));
        } else {
            editor.setItem(6, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.next_page_none.name", List.of()));
        }
        editor.setItem(7, createItem(player, Material.CHEST, "item.set_interact.name", List.of("item.set_interact.lore"), gui.pages().get(pageId).allowInteract()));
        editor.setItem(8, createItem(player, Material.SPYGLASS, "item.preview_gui.name", List.of("item.preview_gui.lore")));
        GUIPage page = gui.pages().getOrDefault(pageId, new GUIPage());
        int maxSlots = Math.min(gui.rows() * 9, 45);
        for (int i = 0; i < maxSlots; i++) {
            editor.setItem(9 + i, page.items().getOrDefault(i, new GUIItem("AIR", null, null, false, List.of())).toItemStack());
        }
        player.openInventory(editor);
    }

    public static void openContextMenu(Player player, CustomGUI gui, int slot, int pageId) {
        LanguageManager lang = ((CoderyoGUI) Bukkit.getPluginManager().getPlugin("CoderyoGUI")).getLanguageManager();
        Inventory menu = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), 9, lang.getTranslation(player, "gui.title.item_settings"));
        menu.setItem(0, createItem(player, Material.PAPER, "item.select_item.name", List.of("item.select_item.lore")));
        menu.setItem(1, createItem(player, Material.COMMAND_BLOCK, "item.set_player_command.name", List.of("item.set_player_command.lore")));
        menu.setItem(2, createItem(player, Material.COMMAND_BLOCK, "item.set_console_command.name", List.of("item.set_console_command.lore")));
        menu.setItem(3, createItem(player, Material.NAME_TAG, "item.set_item_name.name", List.of("item.set_item_name.lore")));
        menu.setItem(4, createItem(player, Material.BARRIER, "item.return.name", List.of("item.return.lore")));
        player.openInventory(menu);
    }

    public static void openItemSelect(Player player, CustomGUI gui, int slot, int pageId, String search, int searchPage) {
        LanguageManager lang = ((CoderyoGUI) Bukkit.getPluginManager().getPlugin("CoderyoGUI")).getLanguageManager();
        String title = search == null ? lang.getTranslation(player, "gui.title.select_item") : lang.getTranslation(player, "gui.title.search_item", search);
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId, slot, searchPage, search), 54, title);
        for (int i = 0; i < 54; i++) {
            select.setItem(i, createItem(player, Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        select.setItem(0, createItem(player, Material.BARRIER, "item.return.name", List.of("item.return.lore")));
        select.setItem(1, createItem(player, Material.COMPASS, "item.search_item.name", List.of("item.search_item.lore")));
        List<Material> materials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(m -> m != Material.AIR)
                .filter(m -> {
                    try {
                        ItemStack item = new ItemStack(m);
                        return item.getItemMeta() != null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(m -> search == null || m.name().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        int start = (searchPage - 1) * 45;
        for (int i = 0; i < 45 && start + i < materials.size(); i++) {
            Material material = materials.get(start + i);
            // 格式化材質名稱，將例如 STONE 轉為 Stone
            String displayName = formatMaterialName(material.name());
            select.setItem(9 + i, createItem(player, material, "item.material.name", List.of("item.material.lore"), displayName));
        }
        if (searchPage > 1) {
            select.setItem(52, createItem(player, Material.ARROW, "item.prev_page.name", List.of("item.prev_page.lore")));
        } else {
            select.setItem(52, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.prev_page_none.name", List.of()));
        }
        if (start + 45 < materials.size()) {
            select.setItem(53, createItem(player, Material.ARROW, "item.next_page.name", List.of("item.next_page.lore")));
        } else {
            select.setItem(53, createItem(player, Material.GRAY_STAINED_GLASS_PANE, "item.next_page_none.name", List.of()));
        }
        if (materials.isEmpty()) {
            select.setItem(9, createItem(player, Material.BOOK, "item.no_matching_items.name", List.of("item.no_matching_items.lore")));
        }
        player.openInventory(select);
    }

    public static void openRowSelect(Player player, CustomGUI gui, int pageId) {
        LanguageManager lang = ((CoderyoGUI) Bukkit.getPluginManager().getPlugin("CoderyoGUI")).getLanguageManager();
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId), 36, lang.getTranslation(player, "gui.title.select_rows"));
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(player, Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 1; i <= 6; i++) {
            ItemStack item = createItem(player, Material.PAPER, "item.row_select.name", null, i, gui.rows() == i ? lang.getTranslation(player, "item.row_select.current") : "");
            if (gui.rows() == i) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    item.setItemMeta(meta);
                }
            }
            select.setItem(i - 1, item);
        }
        select.setItem(0, createItem(player, Material.BARRIER, "item.return.name"));
        player.openInventory(select);
    }

    private static ItemStack createItem(Player player, Material material, String nameKey) {
        return createItem(player, material, nameKey, null);
    }

    private static ItemStack createItem(Player player, Material material, String nameKey, List<String> loreKeys, Object... args) {
        LanguageManager lang = ((CoderyoGUI) Bukkit.getPluginManager().getPlugin("CoderyoGUI")).getLanguageManager();
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + lang.getTranslation(player, nameKey, args));
            if (loreKeys != null) {
                meta.setLore(loreKeys.stream().map(key -> ChatColor.RESET + lang.getTranslation(player, key)).toList());
            }
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.STONE);
            meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RESET + lang.getTranslation(player, nameKey, args));
                if (loreKeys != null) {
                    meta.setLore(loreKeys.stream().map(key -> ChatColor.RESET + lang.getTranslation(player, key)).toList());
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    // 新增方法：格式化材質名稱，例如將 STONE 轉為 Stone
    private static String formatMaterialName(String materialName) {
        String[] words = materialName.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
            }
        }
        return formatted.toString().trim();
    }
}