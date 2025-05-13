package com.coderyo.coderyogui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    private final CoderyoGUI plugin;

    public CommandHandler(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令僅限玩家使用！");
            return true;
        }

        if (!player.hasPermission("coderyogui.use")) {
            player.sendMessage("§c無權限使用 /coderyogui！");
            return true;
        }

        if (args.length == 0) {
            new MainMenuGUI(plugin, 1).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /coderyogui open <GUI名>");
                    return true;
                }
                CustomGUI openGui = plugin.getGuiManager().getGUI(args[1]);
                if (openGui == null) {
                    player.sendMessage("§cGUI 不存在！");
                    return true;
                }
                player.openInventory(openGui.getPage(1));
                player.sendMessage("§a已打開 GUI: " + args[1]);
                break;

            case "edit":
                if (!player.hasPermission("coderyogui.edit.*")) {
                    player.sendMessage("§c無權限編輯 GUI！");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c用法: /coderyogui edit <GUI名>");
                    return true;
                }
                CustomGUI editGui = plugin.getGuiManager().getGUI(args[1]);
                if (editGui == null) {
                    player.sendMessage("§cGUI 不存在！");
                    return true;
                }
                GUIEditor.openEditor(player, editGui, 1);
                player.sendMessage("§a正在編輯 GUI: " + args[1]);
                break;

            case "del":
                if (!player.hasPermission("coderyogui.del")) {
                    player.sendMessage("§c無權限刪除 GUI！");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c用法: /coderyogui del <GUI名>");
                    return true;
                }
                if (plugin.getGuiManager().getGUIs().remove(args[1]) != null) {
                    plugin.getDataStorage().saveGUIsAsync();
                    player.sendMessage("§a已刪除 GUI: " + args[1]);
                } else {
                    player.sendMessage("§cGUI 不存在！");
                }
                break;

            default:
                player.sendMessage("§c未知子命令！可用: open, edit, del");
                break;
        }
        return true;
    }
}