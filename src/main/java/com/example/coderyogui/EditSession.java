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
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI currentGui = gui;
        GUIPage page = currentGui.pages().get(pageId);
        switch (state) {
            case "set_name" -> {
                guiManager.getGUIs().put(input, new CustomGUI(input, currentGui.rows(), currentGui.pages()));
                guiManager.getGUIs().remove(currentGui.name());
                return input;
            }
            case "set_item_id" -> {
                if (Material.matchMaterial(input) == null) return null;
                page.items().put(slot, new GUIItem(input, null, null, true, new ArrayList<>()));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                return currentGui.name();
            }
            case "set_item_name" -> {
                GUIItem item = page.items().get(slot);
                page.items().put(slot, new GUIItem(item.material(), input, item.lore(), item.takeable(), item.actions()));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Set item name: " + input + " for slot: " + slot);
                return currentGui.name();
            }
            case "set_lore_line" -> {
                tempData.add(input);
                player.sendMessage("§a輸入下一行 Lore（輸入 /coderyogui cancel 結束）");
                return currentGui.name();
            }
            case "set_command" -> {
                GUIItem item = page.items().get(slot);
                List<GUIAction> actions = new ArrayList<>(item.actions());
                // 移除前導 /（如果有），保存不包含 /
                String command = input.startsWith("/") ? input.substring(1) : input;
                // 明確檢查 tempData 是否包含 "console"
                boolean asConsole = tempData != null && tempData.contains("console");
                plugin.getLogger().info("Saving command: " + command + ", asConsole: " + asConsole + ", tempData: " + tempData);
                actions.add(new GUIAction("command", command, asConsole));
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), true, actions));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                return currentGui.name();
            }
            case "set_message" -> {
                GUIItem item = page.items().get(slot);
                List<GUIAction> actions = new ArrayList<>(item.actions());
                actions.add(new GUIAction("message", input, false));
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), true, actions));
                gui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                return currentGui.name();
            }
            case "set_page" -> {
                try {
                    int targetPageId = Integer.parseInt(input);
                    if (targetPageId < 1 || !currentGui.pages().containsKey(targetPageId)) return null;
                    GUIItem item = page.items().get(slot);
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    actions.add(new GUIAction("page", String.valueOf(targetPageId), false));
                    page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), true, actions));
                    gui.pages().put(pageId, page);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    return currentGui.name();
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "set_sound" -> {
                try {
                    Sound.valueOf(input);
                    GUIItem item = page.items().get(slot);
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    actions.add(new GUIAction("sound", input, false));
                    page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), true, actions));
                    gui.pages().put(pageId, page);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    return currentGui.name();
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    }
}