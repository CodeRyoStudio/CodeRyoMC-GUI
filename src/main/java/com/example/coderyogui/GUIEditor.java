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
import java.util.List;

public class GUIEditor {
    public static final Material[] COMMON_ITEMS = {
        Material.GLASS_PANE, Material.STONE_BUTTON, Material.PAPER, Material.BOOK,
        Material.IRON_INGOT, Material.OAK_PLANKS, Material.REDSTONE, Material.FEATHER,
        Material.GUNPOWDER, Material.DIAMOND
    };
    public static final Sound[] COMMON_SOUNDS = {
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.UI_BUTTON_CLICK, Sound.BLOCK_NOTE_BLOCK_PLING,
        Sound.ENTITY_PLAYER_LEVELUP, Sound.BLOCK_ANVIL_LAND, Sound.ENTITY_ARROW_HIT_PLAYER,
        Sound.BLOCK_GLASS_BREAK, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, Sound.ENTITY_ENDERMAN_TELEPORT,
        Sound.BLOCK_WOODEN_BUTTON_CLICK_ON
    };

    public static void openEditor(Player player, CustomGUI gui, int pageId) {
        Inventory editor = Bukkit.createInventory(new EditorHolder(gui, pageId), 54, "編輯: " + gui.name());
        for (int i = 0; i < 54; i++) {
            editor.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        editor.setItem(0, createItem(Material.WRITABLE_BOOK, "§a設置名稱"));
        editor.setItem(1, createItem(Material.PAPER, "§a設置行數: " + gui.rows()));
        editor.setItem(2, createItem(Material.GREEN_WOOL, "§a添加頁面"));
        editor.setItem(3, createItem(Material.RED_WOOL, "§a刪除當前頁面"));
        if (pageId > 1) {
            editor.setItem(4, createItem(Material.ARROW, "§a上一頁 (" + pageId + "/" + gui.pages().size() + ")"));
        } else {
            editor.setItem(4, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7上一頁 (無)"));
        }
        if (pageId < gui.pages().size()) {
            editor.setItem(5, createItem(Material.ARROW, "§a下一頁 (" + pageId + "/" + gui.pages().size() + ")"));
        } else {
            editor.setItem(5, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7下一頁 (無)"));
        }
        editor.setItem(6, createItem(Material.CHEST, "§a設置頁面可交互: " + gui.pages().get(pageId).allowInteract()));
        GUIPage page = gui.pages().getOrDefault(pageId, new GUIPage());
        int maxSlots = Math.min(gui.rows() * 9, 45);
        for (int i = 0; i < maxSlots; i++) {
            editor.setItem(9 + i, page.items().getOrDefault(i, new GUIItem("AIR", null, null, false, List.of())).toItemStack());
        }
        player.openInventory(editor);
    }

    public static void openItemSelect(Player player, CustomGUI gui, int slot, int pageId) {
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), 36, "選擇物品");
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 0; i < COMMON_ITEMS.length; i++) {
            select.setItem(i, createItem(COMMON_ITEMS[i], "§a" + COMMON_ITEMS[i].name()));
        }
        select.setItem(10, createItem(Material.NAME_TAG, "§a自定義物品 ID"));
        player.openInventory(select);
    }

    public static void openActionSelect(Player player, CustomGUI gui, int slot, int pageId) {
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), 36, "選擇動作");
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        select.setItem(0, createItem(Material.COMMAND_BLOCK, "§a執行命令 (玩家)"));
        select.setItem(1, createItem(Material.COMMAND_BLOCK, "§a執行命令 (控制台)"));
        select.setItem(2, createItem(Material.PAPER, "§a發送訊息"));
        select.setItem(3, createItem(Material.BOOK, "§a換頁"));
        select.setItem(4, createItem(Material.NOTE_BLOCK, "§a設置音效"));
        select.setItem(5, createItem(Material.CHEST, "§a設置可拿取: " + gui.pages().get(pageId).items().getOrDefault(slot, new GUIItem("AIR", null, null, false, List.of())).takeable()));
        select.setItem(6, createItem(Material.NAME_TAG, "§a設置物品名稱"));
        select.setItem(7, createItem(Material.WRITTEN_BOOK, "§a設置 Lore"));
        player.openInventory(select);
    }

    public static void openSoundSelect(Player player, CustomGUI gui, int slot, int pageId) {
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), 36, "選擇音效");
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 0; i < COMMON_SOUNDS.length; i++) {
            select.setItem(i, createItem(Material.NOTE_BLOCK, "§a" + COMMON_SOUNDS[i].name()));
        }
        select.setItem(10, createItem(Material.NAME_TAG, "§a自定義音效 ID"));
        player.openInventory(select);
    }

    public static void openRowSelect(Player player, CustomGUI gui, int pageId) {
        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId), 36, "選擇行數");
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 1; i <= 6; i++) {
            ItemStack item = createItem(Material.PAPER, "§a" + i + " 行" + (gui.rows() == i ? " §7(當前)" : ""));
            if (gui.rows() == i) {
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                item.setItemMeta(meta);
            }
            select.setItem(i - 1, item);
        }
        player.openInventory(select);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        item.setItemMeta(meta);
        return item;
    }
}