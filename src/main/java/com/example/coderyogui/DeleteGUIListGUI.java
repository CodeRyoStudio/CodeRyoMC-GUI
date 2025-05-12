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
import java.util.stream.Collectors;

public class DeleteGUIListGUI {
    private final CoderyoGUI plugin;
    private final GUIManager guiManager;
    private final int page;
    private final String search;

    public DeleteGUIListGUI(CoderyoGUI plugin, int page, String search) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.page = page;
        this.search = search;
    }

    public Inventory open(Player player) {
        try {
            String title = search == null ? "選擇要刪除的 GUI" : "搜尋刪除 GUI: " + search;
            Inventory inv = Bukkit.createInventory(new DeleteGUIListHolder(page, search), 54, title);
            // 填充背景
            for (int i = 0; i < 54; i++) {
                inv.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, " ", List.of()));
            }
            // 返回按鈕
            inv.setItem(0, createItem(Material.BARRIER, "§c返回主菜單", List.of("§7點擊返回")));
            // 搜尋按鈕
            inv.setItem(1, createItem(Material.COMPASS, "§a搜尋 GUI", List.of("§7輸入 GUI 名稱關鍵詞")));
            // 顯示 GUI 列表
            List<String> guiNames = new ArrayList<>(guiManager.getGUIs().keySet()).stream()
                    .filter(name -> search == null || name.toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            int start = (page - 1) * 45;
            for (int i = 0; i < 45 && start + i < guiNames.size(); i++) {
                String name = guiNames.get(start + i);
                inv.setItem(9 + i, createItem(Material.PAPER, "§a" + name, List.of("§7點擊刪除")));
            }
            // 分頁控制
            if (page > 1) {
                inv.setItem(52, createItem(Material.ARROW, "§a上一頁", List.of()));
            }
            if (start + 45 < guiNames.size()) {
                inv.setItem(53, createItem(Material.ARROW, "§a下一頁", List.of()));
            }
            // 無結果提示
            if (guiNames.isEmpty()) {
                inv.setItem(9, createItem(Material.BOOK, "§c無可用 GUI", List.of("§7請創建新 GUI 或嘗試其他關鍵詞")));
            }
            player.openInventory(inv);
            plugin.getLogger().info("為玩家 " + player.getName() + " 開啟刪除 GUI 列表，第 " + page + " 頁，搜尋: " + (search != null ? search : "無"));
            return inv;
        } catch (Exception e) {
            plugin.getLogger().severe("開啟刪除 GUI 列表失敗: " + e.getMessage());
            player.sendMessage("§c無法開啟刪除 GUI 列表，請聯繫管理員！");
            return null;
        }
    }

    public static Inventory openConfirm(Player player, String guiName) {
        Inventory confirm = Bukkit.createInventory(new DeleteConfirmHolder(guiName), 9, "確認刪除: " + guiName);
        confirm.setItem(3, createItem(Material.GREEN_WOOL, "§a確認刪除", List.of("§7點擊確認")));
        confirm.setItem(5, createItem(Material.RED_WOOL, "§c取消", List.of("§7點擊取消")));
        player.openInventory(confirm);
        return confirm;
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
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