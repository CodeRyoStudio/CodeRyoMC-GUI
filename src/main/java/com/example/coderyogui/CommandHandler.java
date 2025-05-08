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
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!player.hasPermission("coderyogui.use")) {
            player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.no_permission"));
            return true;
        }

        if (args.length == 0) {
            new MainMenuGUI(plugin, 1).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open":
                if (args.length < 2) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_usage_open"));
                    return true;
                }
                CustomGUI gui = plugin.getGuiManager().getGUI(args[1]);
                if (gui == null) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.gui_not_found"));
                    return true;
                }
                player.openInventory(gui.getPage(1));
                player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.gui_opened", args[1]));
                break;

            case "edit":
                if (!player.hasPermission("coderyogui.edit.*")) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.no_permission_edit"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_usage_edit"));
                    return true;
                }
                gui = plugin.getGuiManager().getGUI(args[1]);
                if (gui == null) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.gui_not_found"));
                    return true;
                }
                if (!player.hasPermission("coderyogui.edit." + args[1])) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.no_permission_edit_specific", args[1]));
                    return true;
                }
                GUIEditor.openEditor(player, gui, 1);
                player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.editing_gui", args[1]));
                break;

            case "del":
                if (!player.hasPermission("coderyogui.del")) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.no_permission_delete"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_usage_delete"));
                    return true;
                }
                if (plugin.getGuiManager().getGUIs().remove(args[1]) != null) {
                    plugin.getDataStorage().saveGUIsAsync();
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "command.deleted_gui", args[1]));
                } else {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.gui_not_found"));
                }
                break;

            case "lang":
                if (!player.hasPermission("coderyogui.lang")) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.no_permission_lang"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_usage_lang"));
                    return true;
                }
                String lang = args[1].toLowerCase();
                if (plugin.getLanguageManager().setPlayerLanguage(player, lang)) {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.language_set", lang));
                } else {
                    player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_language"));
                }
                break;

            default:
                player.sendMessage(plugin.getLanguageManager().getTranslation(player, "message.invalid_subcommand"));
                break;
        }
        return true;
    }
}