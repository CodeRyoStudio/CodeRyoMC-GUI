package com.example.coderyogui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompleterImpl implements TabCompleter {
    private final GUIManager guiManager;

    public TabCompleterImpl(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("create", "open", "edit", "cancel"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("edit"))) {
            completions.addAll(guiManager.getGUIs().keySet());
        }
        return completions;
    }
}