package com.coderyo.coderyogui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterImpl implements TabCompleter {
    private final CoderyoGUI plugin;

    public TabCompleterImpl(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("open", "edit", "del"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("del"))) {
            completions.addAll(plugin.getGuiManager().getGUIs().keySet());
        }
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}