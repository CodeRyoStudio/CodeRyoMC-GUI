package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.CoderyoGUIAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class CoderyoGUI extends JavaPlugin {
    private GUIManager guiManager;
    private DataStorage dataStorage;
    private final HashMap<UUID, EditSession> editSessions = new HashMap<>();
    private boolean dependencyMode;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.guiManager = new GUIManager();
        this.dataStorage = new DataStorage(this);
        dataStorage.loadGUIs();
        CoderyoGUIAPI.init(this);

        FileConfiguration config = getConfig();
        boolean enablePluginFeatures = config.getBoolean("enable-plugin-features", true);

        dependencyMode = !enablePluginFeatures;

        if (enablePluginFeatures) {
            PluginCommand command = getCommand("coderyogui");
            if (command != null) {
                CommandHandler commandHandler = new CommandHandler(this);
                command.setExecutor(commandHandler);
                command.setTabCompleter(new TabCompleterImpl(this));
            } else {
                getLogger().warning("命令 'coderyogui' 未在 plugin.yml 中註冊！");
            }
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info("CoderyoGUI 已啟用，內建功能: " + (enablePluginFeatures ? "啟用" : "禁用"));
    }

    @Override
    public void onDisable() {
        getLogger().info("CoderyoGUI 已禁用");
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public EditSession getEditSession(UUID uuid) {
        return editSessions.get(uuid);
    }

    public void setEditSession(UUID uuid, EditSession session) {
        if (session == null) {
            editSessions.remove(uuid);
        } else {
            editSessions.put(uuid, session);
        }
    }

    /**
     * Checks if the plugin is running as a dependency (i.e., plugin features are disabled).
     *
     * @return true if running as a dependency, false if running standalone.
     */
    public boolean isDependency() {
        return dependencyMode;
    }

    /**
     * Opens the main menu GUI for a player.
     *
     * @param player The player to open the main menu for.
     */
    public void openMainMenu(Player player) {
        new MainMenuGUI(this, 1).open(player);
        player.sendMessage("§a已返回主菜單");
    }
}