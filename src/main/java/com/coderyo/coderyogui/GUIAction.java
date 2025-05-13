package com.coderyo.coderyogui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record GUIAction(String type, String value, boolean asConsole, float volume, float pitch) {
    public GUIAction(String type, String value, boolean asConsole) {
        this(type, value, asConsole, 1.0f, 1.0f);
    }

    public GUIAction(String type, String value) {
        this(type, value, false, 1.0f, 1.0f);
    }

    public void execute(Player player) {
        switch (type.toLowerCase()) {
            case "command":
                String command = value;
                if (asConsole) {
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
                } else {
                    player.performCommand(command);
                }
                break;
            case "message":
                player.sendMessage(value);
                break;
            case "sound":
                try {
                    Sound sound = Sound.valueOf(value.toUpperCase());
                    player.playSound(player.getLocation(), sound, volume, pitch);
                } catch (IllegalArgumentException e) {
                    player.getServer().getLogger().warning("無效音效名稱: " + value);
                }
                break;
            case "close":
                player.closeInventory();
                break;
            default:
                player.getServer().getLogger().warning("未知動作類型: " + type);
                break;
        }
    }
}