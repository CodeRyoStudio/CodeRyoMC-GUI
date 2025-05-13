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
                getLogger().warning("Command 'coderyogui' not registered in plugin.yml!");
            }
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info("CoderyoGUI enabled, built-in features: " + (enablePluginFeatures ? "enabled" : "disabled"));
    }

    @Override
    public void onDisable() {
        guiManager.clearTemporaryGUIs();
        getLogger().info("CoderyoGUI disabled");
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

    public boolean isDependency() {
        return dependencyMode;
    }

    public void openMainMenu(Player player) {
        new MainMenuGUI(this, 1).open(player);
        player.sendMessage("§aReturned to main menu");
    }
}