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

import static com.coderyo.coderyogui.Constants.*;

public class MainMenuGUI {
    private final CoderyoGUI plugin;
    private final int page;

    public MainMenuGUI(CoderyoGUI plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }

    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(new MainMenuHolder(page), INVENTORY_SIZE_54, TITLE_MAIN_MENU);

        // 填滿背景
        for (int slotIndex = 0; slotIndex < INVENTORY_SIZE_54; slotIndex++) {
            inv.setItem(slotIndex, createItem(Material.GRAY_STAINED_GLASS_PANE, EMPTY_STRING, List.of()));
        }

        // 設置功能按鈕
        inv.setItem(MAIN_MENU_CREATE_BUTTON, createItem(Material.CRAFTING_TABLE, DISPLAY_CREATE_GUI, List.of(LORE_CLICK_CREATE)));
        inv.setItem(MAIN_MENU_OPEN_BUTTON, createItem(Material.CHEST, DISPLAY_OPEN_GUI, List.of(LORE_CLICK_OPEN)));
        inv.setItem(MAIN_MENU_EDIT_BUTTON, createItem(Material.ANVIL, DISPLAY_EDIT_GUI, List.of(LORE_CLICK_EDIT)));
        inv.setItem(MAIN_MENU_DELETE_BUTTON, createItem(Material.RED_WOOL, DISPLAY_DELETE_GUI, List.of(LORE_CLICK_DELETE)));

        // 獲取並顯示GUI列表
        List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet());
        int startIndex = (page - 1) * ITEMS_PER_PAGE;

        for (int itemIndex = 0; itemIndex < ITEMS_PER_PAGE && startIndex + itemIndex < guiNames.size(); itemIndex++) {
            String guiName = guiNames.get(startIndex + itemIndex);
            int slotPosition = CONTENT_AREA_START + itemIndex;
            inv.setItem(slotPosition, createItem(Material.PAPER, "§a" + guiName, List.of("§7左鍵打開", "§7右鍵編輯")));
        }

        // 設置分頁按鈕
        if (page > 1) {
            inv.setItem(PREVIOUS_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_PREVIOUS_PAGE, List.of()));
        }
        if (startIndex + ITEMS_PER_PAGE < guiNames.size()) {
            inv.setItem(NEXT_PAGE_BUTTON, createItem(Material.ARROW, DISPLAY_NEXT_PAGE, List.of()));
        }

        player.openInventory(inv);
        return inv;
    }

    private ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.RESET + displayName);
            if (lore != null) {
                itemMeta.setLore(lore.stream().map((String loreLine) -> ChatColor.RESET + loreLine).toList());
            }
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }
}