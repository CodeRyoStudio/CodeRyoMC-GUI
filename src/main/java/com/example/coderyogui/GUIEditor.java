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
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                "頁面 " + pageId + " 不存在於 GUI " + gui.name() + "，已自動創建新頁面"
            );
        }

        Inventory editor = Bukkit.createInventory(new EditorHolder(gui, pageId), 54, "編輯: " + gui.name());
        for (int i = 0; i < 54; i++) {
            editor.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        editor.setItem(0, createItem(Material.BARRIER, "§c返回主菜單", List.of("§7點擊返回")));
        editor.setItem(1, createItem(Material.WRITABLE_BOOK, "§a設置名稱", List.of("§7點擊輸入新名稱")));
        editor.setItem(2, createItem(Material.PAPER, "§a設置行數: " + gui.rows(), List.of("§7點擊選擇行數")));
        editor.setItem(3, createItem(Material.GREEN_WOOL, "§a添加頁面", List.of("§7點擊添加新頁面")));
        editor.setItem(4, createItem(Material.RED_WOOL, "§a刪除當前頁面", List.of("§7點擊刪除當前頁面")));
        if (pageId > 1) {
            editor.setItem(5, createItem(Material.ARROW, "§a上一頁 (" + pageId + "/" + gui.pages().size() + ")", List.of("§7點擊切換")));
        } else {
            editor.setItem(5, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7上一頁 (無)", List.of()));
        }
        if (pageId < gui.pages().size()) {
            editor.setItem(6, createItem(Material.ARROW, "§a下一頁 (" + pageId + "/" + gui.pages().size() + ")", List.of("§7點擊切換")));
        } else {
            editor.setItem(6, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7下一頁 (無)", List.of()));
        }
        GUIPage page = gui.pages().get(pageId);
        editor.setItem(7, createItem(Material.CHEST, "§a設置頁面可交互: " + page.allowInteract(), List.of("§7點擊切換交互狀態")));
        editor.setItem(8, createItem(Material.SPYGLASS, "§a預覽 GUI", List.of("§7查看最終效果")));

        // 槽位9及以上：顯示GUIPage.items（0-based索引）
        int maxSlots = Math.min(gui.rows() * 9, 54);
        for (int i = 9; i < maxSlots; i++) {
            editor.setItem(i, page.items().getOrDefault(i, new GUIItem("AIR", null, null, false, List.of())).toItemStack());
        }
        player.openInventory(editor);
    }

    public static void openContextMenu(Player player, CustomGUI gui, int slot, int pageId) {
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                "頁面 " + pageId + " 不存在於 GUI " + gui.name() + "，已自動創建新頁面"
            );
        }

        Inventory menu = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), 9, "物品設置");
        menu.setItem(0, createItem(Material.PAPER, "§a選擇物品", List.of("§7設置槽位物品")));
        menu.setItem(1, createItem(Material.COMMAND_BLOCK, "§a設置玩家命令", List.of("§7添加執行命令")));
        menu.setItem(2, createItem(Material.COMMAND_BLOCK, "§a設置控制台命令", List.of("§7添加控制台命令")));
        menu.setItem(3, createItem(Material.NAME_TAG, "§a設置名稱", List.of("§7自訂物品名稱")));
        menu.setItem(4, createItem(Material.BARRIER, "§c刪除物品", List.of("§7移除此槽位物品")));
        menu.setItem(5, createItem(Material.WRITABLE_BOOK, "§a管理命令", List.of("§7編輯或刪除綁定命令")));
        menu.setItem(8, createItem(Material.BARRIER, "§c返回", List.of("§7返回主編輯器")));
        player.openInventory(menu);
    }

    public static void openItemSelect(Player player, CustomGUI gui, int slot, int pageId, String search, int searchPage) {
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                "頁面 " + pageId + " 不存在於 GUI " + gui.name() + "，已自動創建新頁面"
            );
        }

        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId, slot, searchPage, search), 54, search == null ? "選擇物品" : "搜尋物品: " + search);
        for (int i = 0; i < 54; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        select.setItem(0, createItem(Material.BARRIER, "§c返回", List.of("§7返回編輯器")));
        select.setItem(1, createItem(Material.COMPASS, "§a搜尋物品", List.of("§7輸入關鍵詞（如 diamond）")));
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
            select.setItem(9 + i, createItem(material, "§a" + material.name(), List.of("§7點擊選擇")));
        }
        if (searchPage > 1) {
            select.setItem(52, createItem(Material.ARROW, "§a上一頁", List.of("§7點擊切換")));
        } else {
            select.setItem(52, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7上一頁 (無)", List.of()));
        }
        if (start + 45 < materials.size()) {
            select.setItem(53, createItem(Material.ARROW, "§a下一頁", List.of("§7點擊切換")));
        } else {
            select.setItem(53, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7下一頁 (無)", List.of()));
        }
        if (materials.isEmpty()) {
            select.setItem(9, createItem(Material.BOOK, "§c無匹配物品", List.of("§7請嘗試其他關鍵詞")));
        }
        player.openInventory(select);
    }

    public static void openRowSelect(Player player, CustomGUI gui, int pageId) {
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                "頁面 " + pageId + " 不存在於 GUI " + gui.name() + "，已自動創建新頁面"
            );
        }

        Inventory select = Bukkit.createInventory(new EditorHolder(gui, pageId), 36, "選擇行數");
        for (int i = 0; i < 36; i++) {
            select.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 1; i <= 6; i++) {
            ItemStack item = createItem(Material.PAPER, "§a" + i + " 行" + (gui.rows() == i ? " §7(當前)" : ""));
            if (gui.rows() == i) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    item.setItemMeta(meta);
                }
            }
            select.setItem(i - 1, item);
        }
        select.setItem(0, createItem(Material.BARRIER, "§c返回"));
        player.openInventory(select);
    }

    public static void openCommandManager(Player player, CustomGUI gui, int slot, int pageId, int commandPage) {
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                "頁面 " + pageId + " 不存在於 GUI " + gui.name() + "，已自動創建新頁面"
            );
        }

        GUIPage page = gui.pages().get(pageId);
        GUIItem item = page.items().get(slot);
        List<GUIAction> actions = item != null ? item.actions() : new ArrayList<>();

        Inventory manager = Bukkit.createInventory(new EditorHolder(gui, pageId, slot, commandPage, null), 54, "管理命令: 槽位 " + slot);
        for (int i = 0; i < 54; i++) {
            manager.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        manager.setItem(0, createItem(Material.BARRIER, "§c返回", List.of("§7返回物品設置")));
        manager.setItem(1, createItem(Material.PAPER, "§a添加新命令", List.of("§7點擊添加命令")));

        int start = (commandPage - 1) * 45;
        for (int i = 0; i < 45 && start + i < actions.size(); i++) {
            GUIAction action = actions.get(start + i);
            Material mat = action.asConsole() ? Material.COMMAND_BLOCK : Material.REPEATING_COMMAND_BLOCK;
            manager.setItem(9 + i, createItem(mat, "§a命令: " + action.value(), List.of(
                "§7類型: " + (action.asConsole() ? "控制台" : "玩家"),
                "§7左鍵編輯，右鍵刪除"
            )));
        }

        if (commandPage > 1) {
            manager.setItem(52, createItem(Material.ARROW, "§a上一頁", List.of("§7點擊切換")));
        } else {
            manager.setItem(52, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7上一頁 (無)", List.of()));
        }
        if (start + 45 < actions.size()) {
            manager.setItem(53, createItem(Material.ARROW, "§a下一頁", List.of("§7點擊切換")));
        } else {
            manager.setItem(53, createItem(Material.GRAY_STAINED_GLASS_PANE, "§7下一頁 (無)", List.of()));
        }
        if (actions.isEmpty()) {
            manager.setItem(9, createItem(Material.BOOK, "§c無綁定命令", List.of("§7請添加新命令")));
        }

        player.openInventory(manager);
    }

    private static ItemStack createItem(Material material, String name) {
        return createItem(material, name, null);
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + name);
            if (lore != null) {
                meta.setLore(lore.stream().map(s -> ChatColor.RESET + s).toList());
            }
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.STONE);
            meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RESET + name);
                if (lore != null) {
                    meta.setLore(lore.stream().map(s -> ChatColor.RESET + s).toList());
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}