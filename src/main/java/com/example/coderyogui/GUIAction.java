package com.example.coderyogui;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public record GUIAction(String type, String value, boolean asConsole) {
    public void execute(Player player) {
        switch (type) {
            case "command" -> {
                if (asConsole) {
                    if (!player.hasPermission("coderyogui.use.console")) return;
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.replace("%player%", player.getName()));
                } else {
                    if (!player.hasPermission("coderyogui.use")) return;
                    Bukkit.dispatchCommand(player, value.replace("%player%", player.getName()));
                }
            }
            case "message" -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', value));
            case "page" -> {
                CustomGUI gui = ((GUIHolder) player.getOpenInventory().getTopInventory().getHolder()).getGUI();
                player.openInventory(gui.getPage(Integer.parseInt(value)));
            }
            case "sound" -> {
                try {
                    player.playSound(player.getLocation(), Sound.valueOf(value), 1.0f, 1.0f);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }
}