package com.example.coderyogui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleterImpl implements TabCompleter {
    private final CoderyoGUI plugin;

    public TabCompleterImpl(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>(Arrays.asList("open", "edit", "del"));
            if (player.hasPermission("coderyogui.lang")) {
                subcommands.add("lang");
            }
            return subcommands;
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "open":
                case "edit":
                case "del":
                    return new ArrayList<>(plugin.getGuiManager().getGUIs().keySet());
                case "lang":
                    return plugin.getLanguageManager().getAvailableLanguages();
            }
        }
        return new ArrayList<>();
    }
}