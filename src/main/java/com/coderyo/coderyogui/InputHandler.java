package com.coderyo.coderyogui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class InputHandler {
    private final CoderyoGUI plugin;
    private final Map<Player, Location> activeSigns = new HashMap<>();
    private final Map<Location, Material> originalBlocks = new HashMap<>();

    public InputHandler(CoderyoGUI plugin) {
        this.plugin = plugin;
        startSignCleanupTask();
    }

    public void openSignInput(Player player, String prompt, Consumer<String> callback) {
        try {
            Location loc = findSafeSignLocation(player);
            if (loc == null) {
                player.sendMessage("§c無法開啟輸入界面，請移動到開闊區域！");
                return;
            }
            Block block = loc.getBlock();
            Material originalType = block.getType();
            block.setType(Material.OAK_SIGN);

            Sign sign = (Sign) block.getState();
            sign.setLine(0, "");
            sign.setLine(1, "§a" + prompt);
            sign.setLine(2, "§7可使用多行輸入");
            sign.setLine(3, "§7點擊完成");
            sign.update();

            activeSigns.put(player, loc);
            originalBlocks.put(loc, originalType);
            player.openSign(sign);

            sign.setAllowedEditorUniqueId(player.getUniqueId());
            sign.update();
        } catch (Exception e) {
            player.sendMessage("§c無法開啟輸入界面，請重試！");
            plugin.getLogger().severe("開啟告示牌輸入失敗: " + e.getMessage());
        }
    }

    private void startSignCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<Player, Location>> iterator = activeSigns.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Player, Location> entry = iterator.next();
                    Player player = entry.getKey();
                    Location loc = entry.getValue();
                    Block block = loc.getBlock();
                    if (block.getType() != Material.OAK_SIGN) {
                        iterator.remove();
                        originalBlocks.remove(loc);
                        continue;
                    }
                    Sign sign = (Sign) block.getState();
                    if (sign.getAllowedEditorUniqueId() == null || !sign.getAllowedEditorUniqueId().equals(player.getUniqueId())) {
                        block.setType(originalBlocks.getOrDefault(loc, Material.AIR));
                        iterator.remove();
                        originalBlocks.remove(loc);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void removeSign(Player player) {
        Location loc = activeSigns.remove(player);
        if (loc != null) {
            loc.getBlock().setType(originalBlocks.getOrDefault(loc, Material.AIR));
            originalBlocks.remove(loc);
        }
    }

    // 新增 getter 方法
    public Map<Player, Location> getActiveSigns() {
        return activeSigns;
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