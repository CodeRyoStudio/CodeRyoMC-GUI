package com.example.coderyogui;

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

    public void openSignInput(Player player, String prompt, Consumer<String> callback) {
        try {
            // 檢查安全位置
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

            // 打開告示牌界面（Paper API）
            player.openSign(sign);

            // 恢復原始方塊（延遲執行，避免干擾輸入）
            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(originalType);
                }
            }.runTaskLater(plugin, 20L * 180); // 180 秒後恢復
        } catch (Exception e) {
            player.sendMessage("§c無法開啟輸入界面，請重試！");
            plugin.getLogger().severe("開啟告示牌輸入失敗: " + e.getMessage());
        }
    }

    private Location findSafeSignLocation(Player player) {
        // 首先嘗試玩家下方 2 格
        Location loc = player.getLocation().subtract(0, 2, 0);
        if (isSafeLocation(loc)) {
            return loc;
        }
        // 嘗試玩家頭部上方 2 格
        loc = player.getLocation().add(0, 2, 0);
        if (isSafeLocation(loc)) {
            return loc;
        }
        // 無安全位置
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        Block block = loc.getBlock();
        // 檢查是否為空氣或可替換方塊（如短草、 tall grass）
        return block.getType().isAir() || block.getType() == Material.SHORT_GRASS || block.getType() == Material.TALL_GRASS;
    }
}