package com.example.coderyogui;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoderyoGUI extends JavaPlugin {
    private GUIManager guiManager;
    private DataStorage dataStorage;
    private final Map<UUID, EditSession> editSessions = new HashMap<>();

    @Override
    public void onEnable() {
        this.guiManager = new GUIManager();
        this.dataStorage = new DataStorage(this);
        dataStorage.loadGUIs();
        getCommand("coderyogui").setExecutor(new CommandHandler(this));
        getCommand("coderyogui").setTabCompleter(new TabCompleterImpl(this));
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info("CoderyoGUI 已啟用，命令 /coderyogui 已註冊");
    }

    @Override
    public void onDisable() {
        dataStorage.saveGUIsAsync();
        getLogger().info("CoderyoGUI 已禁用");
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