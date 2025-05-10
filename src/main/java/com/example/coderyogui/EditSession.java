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
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("設置物品 ID: " + input + " 於槽位: " + slot);
                return currentGui.name();
            case "set_item_name":
                GUIItem item = page.items().get(slot);
                if (item == null) {
                    player.sendMessage("§c請先為該槽位設置一個物品！");
                    plugin.getLogger().warning("玩家 " + player.getName() + " 嘗試為 GUI " + currentGui.name() + " 的頁面 " + pageId + " 槽位 " + slot + " 設置名稱，但槽位為空");
                    return null;
                }
                page.items().put(slot, new GUIItem(item.material(), input, item.lore(), item.takeable(), item.actions()));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("設置物品名稱: " + input + " 於槽位: " + slot);
                return currentGui.name();
            case "set_command":
                GUIItem cmdItem = page.items().get(slot);
                List<GUIAction> actions;
                String material;
                String itemName;
                List<String> lore;
                boolean takeable;
                if (cmdItem == null) {
                    plugin.getLogger().warning("槽位 " + slot + " 無物品，為設置命令創建默認 GUIItem（材質: AIR）");
                    material = "AIR";
                    itemName = null;
                    lore = new ArrayList<>();
                    takeable = true;
                    actions = new ArrayList<>();
                } else {
                    material = cmdItem.material();
                    itemName = cmdItem.name();
                    lore = cmdItem.lore();
                    takeable = cmdItem.takeable();
                    actions = new ArrayList<>(cmdItem.actions());
                }
                String command = input.startsWith("/") ? input.substring(1) : input;
                boolean asConsole = tempData != null && tempData.contains("console");
                plugin.getLogger().info("保存命令: " + command + ", asConsole: " + asConsole + ", 槽位: " + slot);
                actions.add(new GUIAction("command", command, asConsole));
                page.items().put(slot, new GUIItem(material, itemName, lore, takeable, actions));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage("§a命令已設置！");
                return currentGui.name();
            case "edit_command":
                GUIItem editItem = page.items().get(slot);
                if (editItem == null || editItem.actions().isEmpty()) {
                    player.sendMessage("§c該槽位無命令可編輯！");
                    return null;
                }
                try {
                    int commandIndex = Integer.parseInt(tempData.get(0));
                    List<GUIAction> updatedActions = new ArrayList<>(editItem.actions());
                    GUIAction oldAction = updatedActions.get(commandIndex);
                    String newCommand = input.startsWith("/") ? input.substring(1) : input;
                    updatedActions.set(commandIndex, new GUIAction("command", newCommand, oldAction.asConsole()));
                    page.items().put(slot, new GUIItem(editItem.material(), editItem.name(), editItem.lore(), editItem.takeable(), updatedActions));
                    currentGui.pages().put(pageId, page);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§a命令已更新！");
                    return currentGui.name();
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    player.sendMessage("§c無效命令索引！");
                    plugin.getLogger().warning("編輯命令失敗，無效索引: " + e.getMessage());
                    return null;
                }
            case "delete_command":
                GUIItem delItem = page.items().get(slot);
                if (delItem == null || delItem.actions().isEmpty()) {
                    player.sendMessage("§c該槽位無命令可刪除！");
                    return null;
                }
                try {
                    int commandIndex = Integer.parseInt(tempData.get(0));
                    List<GUIAction> delActions = new ArrayList<>(delItem.actions());
                    delActions.remove(commandIndex);
                    page.items().put(slot, new GUIItem(delItem.material(), delItem.name(), delItem.lore(), delItem.takeable(), delActions));
                    currentGui.pages().put(pageId, page);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    player.sendMessage("§a命令已刪除！");
                    return currentGui.name();
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    player.sendMessage("§c無效命令索引！");
                    plugin.getLogger().warning("刪除命令失敗，無效索引: " + e.getMessage());
                    return null;
                }
            case "delete_item":
                page.items().remove(slot);
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.sendMessage("§a物品已移除！");
                return currentGui.name();
            case "search_item":
                return input;
            case "search_gui":
                return input;
            default:
                return null;
        }
    }
}