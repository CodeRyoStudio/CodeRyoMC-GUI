package com.example.coderyogui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private final CoderyoGUI plugin;
    private final Map<String, CustomGUI> guis = new HashMap<>();

    public GUIManager(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    public void createGUI(Player player, String name, int rows) {
        if (guis.containsKey(name)) {
            player.sendMessage("§cGUI 已存在！");
            return;
        }
        if (rows < 1 || rows > 6) {
            player.sendMessage("§c行數必須在 1-6 之間！");
            return;
        }
        CustomGUI gui = new CustomGUI(name, rows);
        guis.put(name, gui);
        plugin.getDataStorage().saveGUIsAsync();
        GUIEditor.openEditor(player, gui, 1);
    }

    public void openGUI(Player player, String name) {
        CustomGUI gui = guis.get(name);
        if (gui == null) {
            player.sendMessage("§cGUI 不存在！");
            return;
        }
        player.openInventory(gui.getPage(1));
    }

    public CustomGUI getGUI(String name) {
        return guis.get(name);
    }

    public Map<String, CustomGUI> getGUIs() {
        return guis;
    }
}