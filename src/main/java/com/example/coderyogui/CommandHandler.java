package com.example.coderyogui;

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
            sender.sendMessage("§c僅玩家可使用此命令！");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("§c使用方法: /coderyogui <create|open|edit|cancel> [名稱]");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (!player.hasPermission("coderyogui.create")) {
                    player.sendMessage("§c無權限！");
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage("§c使用方法: /coderyogui create <名稱>");
                    return true;
                }
                plugin.getGuiManager().createGUI(player, args[1], 3);
                break;
            case "open":
                if (!player.hasPermission("coderyogui.use")) {
                    player.sendMessage("§c無權限！");
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage("§c使用方法: /coderyogui open <名稱>");
                    return true;
                }
                plugin.getGuiManager().openGUI(player, args[1]);
                break;
            case "edit":
                if (args.length != 2 || !player.hasPermission("coderyogui.edit." + args[1])) {
                    player.sendMessage("§c無權限或使用方法錯誤！");
                    return true;
                }
                CustomGUI gui = plugin.getGuiManager().getGUI(args[1]);
                if (gui == null) {
                    player.sendMessage("§cGUI 不存在！");
                    return true;
                }
                GUIEditor.openEditor(player, gui, 1);
                break;
            case "cancel":
                if (!player.hasPermission("coderyogui.cancel")) {
                    player.sendMessage("§c無權限！");
                    return true;
                }
                EditSession session = plugin.getEditSession(player.getUniqueId());
                if (session == null) {
                    player.sendMessage("§c無正在進行的編輯！");
                    return true;
                }
                GUIEditor.openEditor(player, session.gui(), session.pageId());
                plugin.setEditSession(player.getUniqueId(), null);
                break;
            default:
                player.sendMessage("§c未知子命令！");
        }
        return true;
    }
}