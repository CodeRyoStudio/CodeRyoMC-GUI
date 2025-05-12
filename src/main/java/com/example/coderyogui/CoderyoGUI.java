package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.CoderyoGUIAPI;
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
        // 保存預設配置文件
        saveDefaultConfig();
        // 初始化核心組件
        this.guiManager = new GUIManager();
        this.dataStorage = new DataStorage(this);
        dataStorage.loadGUIs();
        // 初始化 API
        CoderyoGUIAPI.init(this);
        // 檢查是否作為依賴，設置內建功能啟用狀態
        boolean enableFeatures = getConfig().getBoolean("enable-plugin-features", !isDependency());
        if (enableFeatures) {
            getCommand("coderyogui").setExecutor(new CommandHandler(this));
            getCommand("coderyogui").setTabCompleter(new TabCompleterImpl(this));
        }
        // 始終註冊事件監聽器
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info("CoderyoGUI 已啟用，內建功能: " + (enableFeatures ? "啟用" : "禁用，僅提供 API"));
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

    private boolean isDependency() {
        for (org.bukkit.plugin.Plugin plugin : getServer().getPluginManager().getPlugins()) {
            org.bukkit.plugin.PluginDescriptionFile desc = plugin.getDescription();
            if (desc.getDepend().contains("CoderyoGUI") || desc.getSoftDepend().contains("CoderyoGUI")) {
                return true;
            }
        }
        return false;
    }
}