package com.example.coderyogui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public record EditSession(CustomGUI gui, String state, int slot, int pageId, List<String> tempData) {
    public EditSession(CustomGUI gui, String state, int slot, int pageId) {
        this(gui, state, slot, pageId, new ArrayList<>());
    }

    public String handleInput(Player player, String input, CoderyoGUI plugin) {
        // 驗證輸入長度
        if (input.length() > 100) {
            player.sendMessage("§c輸入過長，最大 100 字符！");
            return null;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI currentGui = gui;
        GUIPage page = currentGui != null ? currentGui.pages().get(pageId) : null;
        switch (state) {
            case "create_gui":
                if (guiManager.getGUIs().containsKey(input)) {
                    player.sendMessage("§cGUI 名稱已存在，請重試");
                    return null;
                }
                if (input.isEmpty() || input.length() > 32) {
                    player.sendMessage("§c名稱無效，需 1-32 字");
                    return null;
                }
                CustomGUI newGui = new CustomGUI(input, 3);
                guiManager.getGUIs().put(input, newGui);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage("§aGUI " + input + " 已創建！");
                return input;
            case "set_item_id":
                if (Material.matchMaterial(input) == null) {
                    player.sendMessage("§c無效物品 ID，請輸入如 STONE");
                    return null;
                }
                page.items().put(slot, new GUIItem(input, null, null, true, new ArrayList<>()));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                return currentGui.name();
            case "set_item_name":
                GUIItem item = page.items().get(slot);
                page.items().put(slot, new GUIItem(item.material(), input, item.lore(), item.takeable(), item.actions()));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Set item name: " + input + " for slot: " + slot);
                return currentGui.name();
            case "set_command":
                GUIItem cmdItem = page.items().get(slot);
                List<GUIAction> actions = new ArrayList<>(cmdItem.actions());
                String command = input.startsWith("/") ? input.substring(1) : input;
                boolean asConsole = tempData != null && tempData.contains("console");
                plugin.getLogger().info("Saving command: " + command + ", asConsole: " + asConsole + ", tempData: " + tempData);
                actions.add(new GUIAction("command", command, asConsole));
                page.items().put(slot, new GUIItem(cmdItem.material(), cmdItem.name(), cmdItem.lore(), true, actions));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage("§a命令已設置！");
                return currentGui.name();
            case "search_item":
                // 搜尋物品，結果由 EventListener 處理
                return input;
            case "search_gui":
                // 搜尋 GUI，結果由 EventListener 處理
                return input;
            default:
                return null;
        }
    }
}