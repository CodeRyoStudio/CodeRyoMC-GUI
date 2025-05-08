package com.example.coderyogui;

import org.bukkit.entity.Player;

public record GUIAction(String type, String value, boolean asConsole) {
    public void execute(Player player) {
        if (type.equals("command")) {
            String command = value;
            if (asConsole) {
                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
            } else {
                player.performCommand(command);
            }
        }
    }
}