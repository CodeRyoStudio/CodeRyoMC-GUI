package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.ActionType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record GUIAction(ActionType type, String value, boolean asConsole, float volume, float pitch) {
    public GUIAction(ActionType type, String value, boolean asConsole) {
        this(type, value, asConsole, 1.0f, 1.0f);
    }

    public GUIAction(ActionType type, String value) {
        this(type, value, false, 1.0f, 1.0f);
    }

    public void execute(Player player) {
        switch (type) {
            case COMMAND:
                String command = value;
                if (asConsole) {
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
                } else {
                    player.performCommand(command);
                }
                break;
            case MESSAGE:
                player.sendMessage(value);
                break;
            case SOUND:
                try {
                    Sound sound = Sound.valueOf(value.toUpperCase());
                    player.playSound(player.getLocation(), sound, volume, pitch);
                } catch (IllegalArgumentException e) {
                    player.getServer().getLogger().warning("Invalid sound name: " + value);
                }
                break;
            case CLOSE:
                player.closeInventory();
                break;
        }
    }
}