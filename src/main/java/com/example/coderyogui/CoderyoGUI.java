package com.example.coderyogui;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoderyoGUI extends JavaPlugin {
    private GUIManager guiManager;
    private DataStorage dataStorage;
    private Map<UUID, EditSession> editSessions;

    @Override
    public void onEnable() {
        guiManager = new GUIManager(this);
        dataStorage = new DataStorage(this);
        editSessions = new HashMap<>();
        dataStorage.loadGUIs();
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("coderyogui").setExecutor(commandHandler);
        getCommand("coderyogui").setTabCompleter(new TabCompleterImpl(guiManager));
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public EditSession getEditSession(UUID playerId) {
        return editSessions.get(playerId);
    }

    public void setEditSession(UUID playerId, EditSession session) {
        if (session == null) {
            editSessions.remove(playerId);
        } else {
            editSessions.put(playerId, session);
        }
    }
}