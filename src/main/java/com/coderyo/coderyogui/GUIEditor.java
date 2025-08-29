package com.coderyo.coderyogui;

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

import static com.coderyo.coderyogui.Constants.*;

public class GUIEditor {
    public static void openEditor(Player player, CustomGUI gui, int pageId) {
        // 確保頁面存在
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                String.format("頁面 %d 不存在於 GUI %s，已自動創建新頁面", pageId, gui.name())
            );
        }

        // 創建編輯器介面
        String editorTitle = "編輯: " + gui.name();
        Inventory editor = Bukkit.createInventory(new EditorHolder(gui, pageId), INVENTORY_SIZE_54, editorTitle);

        // 設置背景
        for (int slotIndex = 0; slotIndex < INVENTORY_SIZE_54; slotIndex++) {
            editor.setItem(slotIndex, createItem(Material.GRAY_STAINED_GLASS_PANE, EMPTY_STRING));
        }

        // 設置功能按鈕
        editor.setItem(EDITOR_BACK_BUTTON, createItem(Material.BARRIER, DISPLAY_RETURN_MAIN_MENU, List.of(LORE_CLICK_RETURN)));
        editor.setItem(EDITOR_SET_NAME_BUTTON, createItem(Material.WRITABLE_BOOK, DISPLAY_SET_NAME, List.of(LORE_CLICK_SET_NAME)));
        editor.setItem(EDITOR_SET_ROWS_BUTTON, createItem(Material.PAPER, "§a設置行數: " + gui.rows(), List.of(LORE_CLICK_SELECT_ROWS)));
        editor.setItem(EDITOR_ADD_PAGE_BUTTON, createItem(Material.GREEN_WOOL, "§a添加頁面", List.of(LORE_CLICK_ADD_PAGE)));
        editor.setItem(EDITOR_DELETE_PAGE_BUTTON, createItem(Material.RED_WOOL, "§a刪除當前頁面", List.of(LORE_CLICK_DELETE_PAGE)));

        // 設置分頁按鈕
        int totalPages = gui.pages().size();
        if (pageId > MIN_GUI_ROWS) {
            editor.setItem(EDITOR_PREVIOUS_PAGE_BUTTON, createItem(Material.ARROW,
                String.format("§a上一頁 (%d/%d)", pageId, totalPages), List.of(LORE_CLICK_SWITCH_PAGE)));
        } else {
            editor.setItem(EDITOR_PREVIOUS_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_PREVIOUS_PAGE_DISABLED, List.of()));
        }

        if (pageId < totalPages) {
            editor.setItem(EDITOR_NEXT_PAGE_BUTTON, createItem(Material.ARROW,
                String.format("§a下一頁 (%d/%d)", pageId, totalPages), List.of(LORE_CLICK_SWITCH_PAGE)));
        } else {
            editor.setItem(EDITOR_NEXT_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_NEXT_PAGE_DISABLED, List.of()));
        }

        // 設置其他功能按鈕
        GUIPage currentPage = gui.pages().get(pageId);
        editor.setItem(EDITOR_TOGGLE_INTERACT_BUTTON, createItem(Material.CHEST,
            "§a設置頁面可交互: " + currentPage.allowInteract(), List.of(LORE_CLICK_TOGGLE_INTERACT)));
        editor.setItem(EDITOR_PREVIEW_BUTTON, createItem(Material.SPYGLASS, "§a預覽 GUI", List.of(LORE_CLICK_PREVIEW)));

        // 顯示GUI項目
        int maxSlots = Math.min(gui.rows() * 9, INVENTORY_SIZE_54);
        for (int slotIndex = CONTENT_AREA_START; slotIndex < maxSlots; slotIndex++) {
            GUIItem guiItem = currentPage.items().getOrDefault(slotIndex,
                new GUIItem(DEFAULT_MATERIAL, null, null, false, List.of()));
            editor.setItem(slotIndex, guiItem.toItemStack());
        }

        player.openInventory(editor);
    }

    public static void openContextMenu(Player player, CustomGUI gui, int slot, int pageId) {
        // 確保頁面存在
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                String.format("頁面 %d 不存在於 GUI %s，已自動創建新頁面", pageId, gui.name())
            );
        }

        // 創建上下文菜單
        Inventory contextMenu = Bukkit.createInventory(new EditorHolder(gui, pageId, slot), INVENTORY_SIZE_9, TITLE_ITEM_SETTINGS);

        // 設置菜單項目
        contextMenu.setItem(CONTEXT_SELECT_ITEM_BUTTON, createItem(Material.PAPER, DISPLAY_SELECT_ITEM, List.of(LORE_CLICK_SET_ITEM)));
        contextMenu.setItem(CONTEXT_SET_PLAYER_COMMAND_BUTTON, createItem(Material.COMMAND_BLOCK, DISPLAY_SET_PLAYER_COMMAND, List.of(LORE_CLICK_ADD_COMMAND)));
        contextMenu.setItem(CONTEXT_SET_CONSOLE_COMMAND_BUTTON, createItem(Material.COMMAND_BLOCK, DISPLAY_SET_CONSOLE_COMMAND, List.of(LORE_CLICK_CONSOLE_COMMAND)));
        contextMenu.setItem(CONTEXT_SET_NAME_BUTTON, createItem(Material.NAME_TAG, DISPLAY_SET_ITEM_NAME, List.of(LORE_CLICK_CUSTOM_NAME)));
        contextMenu.setItem(CONTEXT_DELETE_ITEM_BUTTON, createItem(Material.BARRIER, DISPLAY_DELETE_ITEM, List.of(LORE_CLICK_REMOVE_ITEM)));
        contextMenu.setItem(CONTEXT_MANAGE_COMMANDS_BUTTON, createItem(Material.WRITABLE_BOOK, DISPLAY_MANAGE_COMMANDS, List.of(LORE_CLICK_EDIT_COMMANDS)));
        contextMenu.setItem(CONTEXT_BACK_BUTTON, createItem(Material.BARRIER, DISPLAY_BACK, List.of(LORE_CLICK_RETURN)));

        player.openInventory(contextMenu);
    }

    public static void openItemSelect(Player player, CustomGUI gui, int slot, int pageId, String search, int searchPage) {
        // 確保頁面存在
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                String.format("頁面 %d 不存在於 GUI %s，已自動創建新頁面", pageId, gui.name())
            );
        }

        // 創建物品選擇介面
        String interfaceTitle = search == null ? TITLE_SELECT_ITEM : TITLE_SEARCH_ITEM_PREFIX + search;
        Inventory itemSelectInventory = Bukkit.createInventory(
            new EditorHolder(gui, pageId, slot, searchPage, search),
            INVENTORY_SIZE_54,
            interfaceTitle
        );

        // 設置背景
        for (int slotIndex = 0; slotIndex < INVENTORY_SIZE_54; slotIndex++) {
            itemSelectInventory.setItem(slotIndex, createItem(Material.GRAY_STAINED_GLASS_PANE, EMPTY_STRING));
        }

        // 設置功能按鈕
        itemSelectInventory.setItem(ITEM_SELECT_BACK_BUTTON, createItem(Material.BARRIER, DISPLAY_BACK, List.of(LORE_CLICK_RETURN)));
        itemSelectInventory.setItem(ITEM_SELECT_SEARCH_BUTTON, createItem(Material.COMPASS, DISPLAY_SEARCH_ITEM, List.of(LORE_CLICK_SEARCH)));

        // 過濾並獲取材質列表
        List<Material> filteredMaterials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> material != Material.AIR)
                .filter(material -> {
                    try {
                        ItemStack testItem = new ItemStack(material);
                        return testItem.getItemMeta() != null;
                    } catch (Exception exception) {
                        return false;
                    }
                })
                .filter(material -> search == null || material.name().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        // 計算分頁
        int startIndex = (searchPage - 1) * ITEMS_PER_PAGE;

        // 顯示材質項目
        for (int itemIndex = 0; itemIndex < ITEMS_PER_PAGE && startIndex + itemIndex < filteredMaterials.size(); itemIndex++) {
            Material material = filteredMaterials.get(startIndex + itemIndex);
            int slotPosition = CONTENT_AREA_START + itemIndex;
            itemSelectInventory.setItem(slotPosition, createItem(material, "§a" + material.name(), List.of(LORE_CLICK_SELECT)));
        }

        // 設置分頁按鈕
        if (searchPage > 1) {
            itemSelectInventory.setItem(PREVIOUS_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_PREVIOUS_PAGE, List.of(LORE_CLICK_SWITCH)));
        } else {
            itemSelectInventory.setItem(PREVIOUS_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_PREVIOUS_PAGE_DISABLED, List.of()));
        }

        if (startIndex + ITEMS_PER_PAGE < filteredMaterials.size()) {
            itemSelectInventory.setItem(NEXT_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_NEXT_PAGE, List.of(LORE_CLICK_SWITCH)));
        } else {
            itemSelectInventory.setItem(NEXT_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_NEXT_PAGE_DISABLED, List.of()));
        }

        // 無匹配物品時的提示
        if (filteredMaterials.isEmpty()) {
            itemSelectInventory.setItem(CONTENT_AREA_START, createItem(Material.BOOK, DISPLAY_NO_MATCHING_ITEMS, List.of(LORE_TRY_OTHER_KEYWORD)));
        }

        player.openInventory(itemSelectInventory);
    }

    public static void openRowSelect(Player player, CustomGUI gui, int pageId) {
        // 確保頁面存在
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                String.format("頁面 %d 不存在於 GUI %s，已自動創建新頁面", pageId, gui.name())
            );
        }

        // 創建行數選擇介面
        Inventory rowSelectInventory = Bukkit.createInventory(new EditorHolder(gui, pageId), INVENTORY_SIZE_36, TITLE_SELECT_ROWS);

        // 設置背景
        for (int slotIndex = 0; slotIndex < INVENTORY_SIZE_36; slotIndex++) {
            rowSelectInventory.setItem(slotIndex, createItem(Material.GRAY_STAINED_GLASS_PANE, EMPTY_STRING));
        }

        // 設置行數選項
        for (int rowNumber = MIN_GUI_ROWS; rowNumber <= MAX_GUI_ROWS; rowNumber++) {
            String displayText = "§a" + rowNumber + " 行" + (gui.rows() == rowNumber ? " §7(當前)" : "");
            ItemStack rowItem = createItem(Material.PAPER, displayText);

            // 當前行數添加附魔效果
            if (gui.rows() == rowNumber) {
                ItemMeta itemMeta = rowItem.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.addEnchant(Enchantment.UNBREAKING, DEFAULT_ENCHANTMENT_LEVEL, true);
                    rowItem.setItemMeta(itemMeta);
                }
            }

            rowSelectInventory.setItem(rowNumber - 1, rowItem);
        }

        // 返回按鈕（覆蓋第一個行數按鈕）
        rowSelectInventory.setItem(0, createItem(Material.BARRIER, DISPLAY_BACK));

        player.openInventory(rowSelectInventory);
    }

    public static void openCommandManager(Player player, CustomGUI gui, int slot, int pageId, int commandPage) {
        // 確保頁面存在
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
            player.getServer().getPluginManager().getPlugin("CoderyoGUI").getLogger().warning(
                String.format("頁面 %d 不存在於 GUI %s，已自動創建新頁面", pageId, gui.name())
            );
        }

        // 獲取頁面和項目
        GUIPage currentPage = gui.pages().get(pageId);
        GUIItem guiItem = currentPage.items().get(slot);
        List<GUIAction> actions = guiItem != null ? guiItem.actions() : new ArrayList<>();

        // 創建命令管理介面
        String managerTitle = TITLE_MANAGE_COMMANDS_PREFIX + slot;
        Inventory commandManager = Bukkit.createInventory(
            new EditorHolder(gui, pageId, slot, commandPage, null),
            INVENTORY_SIZE_54,
            managerTitle
        );

        // 設置背景
        for (int slotIndex = 0; slotIndex < INVENTORY_SIZE_54; slotIndex++) {
            commandManager.setItem(slotIndex, createItem(Material.GRAY_STAINED_GLASS_PANE, EMPTY_STRING));
        }

        // 設置功能按鈕
        commandManager.setItem(COMMAND_MANAGER_BACK_BUTTON, createItem(Material.BARRIER, DISPLAY_BACK, List.of(LORE_CLICK_RETURN)));
        commandManager.setItem(COMMAND_MANAGER_ADD_BUTTON, createItem(Material.PAPER, DISPLAY_ADD_NEW_COMMAND, List.of(LORE_CLICK_ADD_NEW_COMMAND)));

        // 計算分頁並顯示命令
        int startIndex = (commandPage - 1) * ITEMS_PER_PAGE;
        for (int itemIndex = 0; itemIndex < ITEMS_PER_PAGE && startIndex + itemIndex < actions.size(); itemIndex++) {
            GUIAction action = actions.get(startIndex + itemIndex);
            Material material = action.asConsole() ? Material.COMMAND_BLOCK : Material.REPEATING_COMMAND_BLOCK;
            String commandDisplay = "§a命令: " + action.value();
            String commandType = "§7類型: " + (action.asConsole() ? "控制台" : "玩家");

            int slotPosition = CONTENT_AREA_START + itemIndex;
            commandManager.setItem(slotPosition, createItem(material, commandDisplay, List.of(
                commandType,
                LORE_CLICK_EDIT_OR_DELETE
            )));
        }

        // 設置分頁按鈕
        if (commandPage > 1) {
            commandManager.setItem(PREVIOUS_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_PREVIOUS_PAGE, List.of(LORE_CLICK_SWITCH)));
        } else {
            commandManager.setItem(PREVIOUS_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_PREVIOUS_PAGE_DISABLED, List.of()));
        }

        if (startIndex + ITEMS_PER_PAGE < actions.size()) {
            commandManager.setItem(NEXT_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_NEXT_PAGE, List.of(LORE_CLICK_SWITCH)));
        } else {
            commandManager.setItem(NEXT_PAGE_BUTTON, createItem(Material.GRAY_STAINED_GLASS_PANE, DISPLAY_NEXT_PAGE_DISABLED, List.of()));
        }

        // 無命令時的提示
        if (actions.isEmpty()) {
            commandManager.setItem(CONTENT_AREA_START, createItem(Material.BOOK, DISPLAY_NO_COMMANDS, List.of(LORE_NO_BOUND_COMMANDS)));
        }

        player.openInventory(commandManager);
    }

    private static ItemStack createItem(Material material, String displayName) {
        return createItem(material, displayName, null);
    }

    private static ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.RESET + displayName);
            if (lore != null) {
                itemMeta.setLore(lore.stream().map(loreLine -> ChatColor.RESET + loreLine).toList());
            }
            itemStack.setItemMeta(itemMeta);
        } else {
            // 備用物品當原始材質無效時
            itemStack = new ItemStack(Material.STONE);
            itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.RESET + displayName);
                if (lore != null) {
                    itemMeta.setLore(lore.stream().map(loreLine -> ChatColor.RESET + loreLine).toList());
                }
                itemStack.setItemMeta(itemMeta);
            }
        }

        return itemStack;
    }
}