package com.example.coderyogui;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class InputHandler {
    private final CoderyoGUI plugin;

    public InputHandler(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    public void openSignInput(Player player, String promptKey, Consumer<String> callback) {
        LanguageManager lang = plugin.getLanguageManager();
        try {
            Location loc = findSafeSignLocation(player);
            if (loc == null) {
                player.sendMessage(lang.getTranslation(player, "message.sign_input_failed"));
                return;
            }
            Block block = loc.getBlock();
            Material originalType = block.getType();
            block.setType(Material.OAK_SIGN);

            Sign sign = (Sign) block.getState();
            sign.setLine(0, "");
            sign.setLine(1, ChatColor.RESET + lang.getTranslation(player, "sign.prompt." + promptKey));
            sign.setLine(2, ChatColor.RESET + lang.getTranslation(player, "sign.prompt.multiline"));
            sign.setLine(3, ChatColor.RESET + lang.getTranslation(player, "sign.prompt.complete"));
            sign.update();

            player.openSign(sign);

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(originalType);
                }
            }.runTaskLater(plugin, 20L * 5);
        } catch (Exception e) {
            player.sendMessage(lang.getTranslation(player, "message.sign_input_failed"));
            plugin.getLogger().severe("Failed to open sign input: " + e.getMessage());
        }
    }

    private Location findSafeSignLocation(Player player) {
        Location loc = player.getLocation().subtract(0, 2, 0);
        if (isSafeLocation(loc)) {
            return loc;
        }
        loc = player.getLocation().add(0, 2, 0);
        if (isSafeLocation(loc)) {
            return loc;
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        Block block = loc.getBlock();
        return block.getType().isAir() || block.getType() == Material.SHORT_GRASS || block.getType() == Material.TALL_GRASS;
    }
}