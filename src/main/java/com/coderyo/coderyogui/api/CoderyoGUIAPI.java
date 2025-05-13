package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CoderyoGUI;
import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.GUIManager;
import com.coderyo.coderyogui.GUIItem;
import com.coderyo.coderyogui.GUIAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class CoderyoGUIAPI {
    private static CoderyoGUIAPI instance;
    private final CoderyoGUI plugin;

    private CoderyoGUIAPI(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    public static void init(CoderyoGUI plugin) {
        instance = new CoderyoGUIAPI(plugin);
        plugin.getServer().getPluginManager().registerEvents(new InternalListener(plugin), plugin);
    }

    public static CoderyoGUIAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoderyoGUIAPI is not initialized");
        }
        return instance;
    }

    public boolean createGUI(String name, int rows) {
        if (name == null || name.isEmpty() || name.length() > 32 || containsInvalidChars(name)) {
            return false;
        }
        if (rows < 1 || rows > 6) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUIs().containsKey(name)) {
            return false;
        }
        guiManager.getGUIs().put(name, new CustomGUI(name, rows));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean setGUIRows(String name, int rows) {
        if (rows < 1 || rows > 6) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return false;
        }
        guiManager.getGUIs().put(name, new CustomGUI(name, rows, gui.pages()));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean addPage(String name, int pageId) {
        if (pageId < 1) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || gui.pages().containsKey(pageId)) {
            return false;
        }
        gui.pages().put(pageId, new com.coderyo.coderyogui.GUIPage());
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean removePage(String name, int pageId) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId) || gui.pages().size() <= 1) {
            return false;
        }
        gui.pages().remove(pageId);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean setItem(String name, int pageId, int slot, GUIItem item) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return false;
        }
        if (item == null) {
            return false;
        }
        // 驗證 GUIItem
        if (item.material() == null || Material.matchMaterial(item.material()) == null) {
            return false;
        }
        if (item.name() != null && (item.name().isEmpty() || item.name().length() > 100 || containsInvalidChars(item.name()))) {
            return false;
        }
        if (item.lore() != null) {
            for (String line : item.lore()) {
                if (line != null && (line.length() > 100 || containsInvalidChars(line))) {
                    return false;
                }
            }
        }
        if (item.actions() != null) {
            for (GUIAction action : item.actions()) {
                if (action == null || action.type() == null || action.value() == null) {
                    return false;
                }
                if (action.type().isEmpty() || action.value().isEmpty() || action.type().length() > 50 || action.value().length() > 100) {
                    return false;
                }
                if (containsInvalidChars(action.value())) {
                    return false;
                }
            }
        }
        gui.pages().get(pageId).items().put(slot, item);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean removeItem(String name, int pageId, int slot) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return false;
        }
        gui.pages().get(pageId).items().remove(slot);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean setPageInteractable(String name, int pageId, boolean allowInteract) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        com.coderyo.coderyogui.GUIPage page = gui.pages().get(pageId);
        gui.pages().put(pageId, new com.coderyo.coderyogui.GUIPage(page.items(), allowInteract));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    public boolean deleteGUI(String name) {
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUIs().remove(name) != null) {
            plugin.getDataStorage().saveGUIsAsync();
            return true;
        }
        return false;
    }

    public boolean openGUI(Player player, String name, int pageId) {
        if (player == null) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        player.openInventory(gui.getPage(pageId));
        return true;
    }

    public CustomGUI getGUI(String name) {
        CustomGUI gui = plugin.getGuiManager().getGUI(name);
        if (gui == null) {
            return null;
        }
        return new CustomGUI(gui.name(), gui.rows(), new HashMap<>(gui.pages()));
    }

    public Set<String> listGUIs() {
        return Collections.unmodifiableSet(plugin.getGuiManager().getGUIs().keySet());
    }

    private boolean containsInvalidChars(String input) {
        if (input == null) return false;
        return input.contains("\n") || input.contains("\r") || input.contains(": ") || input.contains("&") || input.contains("?");
    }

    private static class InternalListener implements Listener {
        private final CoderyoGUI plugin;

        InternalListener(CoderyoGUI plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onGUIClick(GUIClickEvent event) {
            if (!plugin.isDependency() && event.isBackButton()) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                plugin.openMainMenu(player);
            }
        }
    }
}