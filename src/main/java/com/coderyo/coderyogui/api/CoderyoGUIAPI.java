package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CoderyoGUI;
import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.EditSession;
import com.coderyo.coderyogui.GUIManager;
import com.coderyo.coderyogui.GUIPage;
import com.coderyo.coderyogui.GUIItem;
import com.coderyo.coderyogui.GUIAction; // Added import
import com.coderyo.coderyogui.api.StringSanitizer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main API for interacting with CoderyoGUI plugin, providing methods to create, modify, delete, and open custom GUIs.
 */
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

    /**
     * Creates a new GUI with the specified name and rows.
     *
     * @param name        Unique GUI name (1-32 characters, alphanumeric and underscores).
     * @param rows        Number of rows (1-6).
     * @param isTemporary If true, GUI is stored in memory and not saved to guis.yml.
     * @return GUIResult indicating success or failure with error message.
     * @throws GUIAPIException If input validation fails.
     */
    public GUIResult createGUI(String name, int rows, boolean isTemporary) throws GUIAPIException {
        if (name == null || name.isEmpty() || name.length() > 32 || !name.matches("[a-zA-Z0-9_]+")) {
            throw new GUIAPIException("GUI name must be 1-32 characters, alphanumeric and underscores");
        }
        if (rows < 1 || rows > 6) {
            throw new GUIAPIException("Rows must be between 1 and 6");
        }
        EditSession session = new EditSession(null, "create_gui", -1, 1);
        String result = session.handleInput(null, StringSanitizer.sanitize(name), plugin);
        if (result == null) {
            return new GUIResult(false, "Invalid GUI name: " + name);
        }
        CustomGUI gui = plugin.getGuiManager().getGUI(result);
        if (gui != null) {
            CustomGUI newGui = new CustomGUI(result, rows, gui.pages());
            if (isTemporary) {
                plugin.getGuiManager().getTemporaryGUIs().put(result, newGui);
            } else {
                plugin.getGuiManager().getGUIs().put(result, newGui);
                plugin.getDataStorage().saveGUIsAsync();
            }
            return new GUIResult(true, null);
        }
        return new GUIResult(false, "Failed to create GUI: " + name);
    }

    /**
     * Updates the number of rows for an existing GUI.
     */
    public GUIResult setGUIRows(String name, int rows) throws GUIAPIException {
        if (rows < 1 || rows > 6) {
            throw new GUIAPIException("Rows must be between 1 and 6");
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        guiManager.getGUIs().put(name, new CustomGUI(name, rows, gui.pages()));
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Adds a new page to the specified GUI.
     */
    public GUIResult addPage(String name, int pageId) throws GUIAPIException {
        if (pageId < 1) {
            throw new GUIAPIException("Page ID must be positive");
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (gui.pages().containsKey(pageId)) {
            return new GUIResult(false, "Page ID already exists: " + pageId);
        }
        EditSession session = new EditSession(gui, "add_page", -1, pageId);
        String result = session.handleInput(null, String.valueOf(pageId), plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to add page: " + pageId);
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Removes a page from the specified GUI.
     */
    public GUIResult removePage(String name, int pageId) throws GUIAPIException {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (!gui.pages().containsKey(pageId)) {
            return new GUIResult(false, "Page not found: " + pageId);
        }
        if (gui.pages().size() <= 1) {
            return new GUIResult(false, "Cannot remove the last page");
        }
        EditSession session = new EditSession(gui, "remove_page", -1, pageId);
        String result = session.handleInput(null, String.valueOf(pageId), plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to remove page: " + pageId);
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Sets an item in a specific slot on a GUI page.
     */
    public GUIResult setItem(String name, int pageId, int slot, GUIItem item) throws GUIAPIException {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (!gui.pages().containsKey(pageId)) {
            gui.pages().put(pageId, new GUIPage());
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return new GUIResult(false, "Invalid slot: " + slot);
        }
        if (item == null) {
            return new GUIResult(false, "Item cannot be null");
        }
        // Set material
        EditSession session = new EditSession(gui, "set_item_id", slot, pageId);
        String result = session.handleInput(null, StringSanitizer.sanitize(item.material()), plugin);
        if (result == null) {
            return new GUIResult(false, "Invalid material: " + item.material());
        }
        // Set name
        if (item.name() != null) {
            session = new EditSession(gui, "set_item_name", slot, pageId);
            result = session.handleInput(null, StringSanitizer.sanitize(item.name()), plugin);
            if (result == null) {
                return new GUIResult(false, "Invalid item name: " + item.name());
            }
        }
        // Set lore
        for (String line : item.lore()) {
            if (line.length() > 100) {
                return new GUIResult(false, "Lore line exceeds 100 characters: " + line);
            }
            session = new EditSession(gui, "set_lore", slot, pageId);
            result = session.handleInput(null, StringSanitizer.sanitize(line), plugin);
            if (result == null) {
                return new GUIResult(false, "Invalid lore line: " + line);
            }
        }
        // Set actions
        for (GUIAction action : item.actions()) {
            String state = switch (action.type().name().toLowerCase()) {
                case "command" -> "set_command";
                case "message" -> "set_message_action";
                case "sound" -> "set_sound_action";
                case "close" -> "set_close_action";
                default -> throw new GUIAPIException("Unsupported action type: " + action.type());
            };
            session = new EditSession(gui, state, slot, pageId, action.asConsole() ? List.of("console") : List.of());
            result = session.handleInput(null, StringSanitizer.sanitize(action.value()), plugin);
            if (result == null) {
                return new GUIResult(false, "Invalid action value: " + action.value());
            }
        }
        // Set takeable
        session = new EditSession(gui, "set_takeable", slot, pageId);
        result = session.handleInput(null, String.valueOf(item.takeable()), plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to set takeable: " + item.takeable());
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Removes an item from a specific slot on a GUI page.
     */
    public GUIResult removeItem(String name, int pageId, int slot) throws GUIAPIException {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (!gui.pages().containsKey(pageId)) {
            return new GUIResult(false, "Page not found: " + pageId);
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return new GUIResult(false, "Invalid slot: " + slot);
        }
        EditSession session = new EditSession(gui, "delete_item", slot, pageId);
        String result = session.handleInput(null, "", plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to remove item at slot: " + slot);
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Sets whether a GUI page allows player interactions.
     */
    public GUIResult setPageInteractable(String name, int pageId, boolean allowInteract) throws GUIAPIException {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (!gui.pages().containsKey(pageId)) {
            return new GUIResult(false, "Page not found: " + pageId);
        }
        EditSession session = new EditSession(gui, "set_page_interact", -1, pageId);
        String result = session.handleInput(null, String.valueOf(allowInteract), plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to set page interactable: " + allowInteract);
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Deletes a GUI from the system.
     */
    public GUIResult deleteGUI(String name) throws GUIAPIException {
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUI(name) == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        EditSession session = new EditSession(null, "delete_gui", -1, 1);
        String result = session.handleInput(null, name, plugin);
        if (result == null) {
            return new GUIResult(false, "Failed to delete GUI: " + name);
        }
        if (!guiManager.isTemporary(name)) {
            plugin.getDataStorage().saveGUIsAsync();
        }
        return new GUIResult(true, null);
    }

    /**
     * Opens a specific page of a GUI for a player.
     */
    public GUIResult openGUI(Player player, String name, int pageId) throws GUIAPIException {
        if (player == null) {
            throw new GUIAPIException("Player cannot be null");
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return new GUIResult(false, "GUI not found: " + name);
        }
        if (!gui.pages().containsKey(pageId)) {
            return new GUIResult(false, "Page not found: " + pageId);
        }
        player.openInventory(gui.getPage(pageId));
        return new GUIResult(true, null);
    }

    /**
     * Retrieves a read-only copy of a GUI's data.
     */
    public CustomGUI getGUI(String name) {
        CustomGUI gui = plugin.getGuiManager().getGUI(name);
        if (gui == null) {
            return null;
        }
        return new CustomGUI(gui.name(), gui.rows(), new HashMap<>(gui.pages()));
    }

    /**
     * Lists the names of all existing GUIs.
     */
    public Set<String> listGUIs() {
        Set<String> allGUIs = new HashSet<>(plugin.getGuiManager().getGUIs().keySet());
        allGUIs.addAll(plugin.getGuiManager().getTemporaryGUIs().keySet());
        return Collections.unmodifiableSet(allGUIs);
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