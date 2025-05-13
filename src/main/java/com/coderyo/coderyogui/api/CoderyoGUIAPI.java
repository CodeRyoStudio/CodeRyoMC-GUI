package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CoderyoGUI;
import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.GUIManager;
import com.coderyo.coderyogui.GUIItem;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * The main API for interacting with the CoderyoGUI plugin, providing methods to
 * create, modify, delete, and open custom graphical user interfaces (GUIs) in a
 * Minecraft server.
 */
public class CoderyoGUIAPI {
    private static CoderyoGUIAPI instance;
    private final CoderyoGUI plugin;

    /**
     * Constructs a new API instance with the specified plugin.
     * This constructor is private to enforce singleton pattern.
     *
     * @param plugin The CoderyoGUI plugin instance.
     */
    private CoderyoGUIAPI(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes the API with the given plugin instance.
     * This method must be called by the CoderyoGUI plugin during its enable phase.
     *
     * @param plugin The CoderyoGUI plugin instance.
     */
    public static void init(CoderyoGUI plugin) {
        instance = new CoderyoGUIAPI(plugin);
    }

    /**
     * Retrieves the singleton instance of the API.
     *
     * @return The API instance.
     * @throws IllegalStateException if the API has not been initialized.
     */
    public static CoderyoGUIAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoderyoGUIAPI is not initialized");
        }
        return instance;
    }

    /**
     * Creates a new GUI with the specified name and number of rows.
     *
     * @param name The unique name of the GUI (1-32 characters, non-null).
     * @param rows The number of rows in the GUI (1-6).
     * @return true if the GUI was created successfully, false if the name is invalid,
     *         rows are out of range, or the GUI already exists.
     */
    public boolean createGUI(String name, int rows) {
        if (name == null || name.isEmpty() || name.length() > 32) {
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

    /**
     * Updates the number of rows for an existing GUI.
     *
     * @param name The name of the GUI to modify.
     * @param rows The new number of rows (1-6).
     * @return true if the rows were updated successfully, false if the GUI does not
     *         exist or rows are out of range.
     */
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

    /**
     * Adds a new page to the specified GUI.
     *
     * @param name   The name of the GUI.
     * @param pageId The ID of the new page (positive integer, must not already exist).
     * @return true if the page was added successfully, false if the GUI does not exist
     *         or the pageId is invalid or already exists.
     */
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

    /**
     * Removes a page from the specified GUI.
     * The GUI must retain at least one page.
     *
     * @param name   The name of the GUI.
     * @param pageId The ID of the page to remove.
     * @return true if the page was removed successfully, false if the GUI does not
     *         exist, the pageId does not exist, or it is the last page.
     */
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

    /**
     * Sets an item in a specific slot on a GUI page.
     *
     * @param name   The name of the GUI.
     * @param pageId The ID of the page.
     * @param slot   The slot index (9 to rows * 9 - 1).
     * @param item   The GUIItem to place (non-null).
     * @return true if the item was set successfully, false if the GUI, page, or slot
     *         is invalid, or the item is null.
     */
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
        gui.pages().get(pageId).items().put(slot, item);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * Removes an item from a specific slot on a GUI page.
     *
     * @param name   The name of the GUI.
     * @param pageId The ID of the page.
     * @param slot   The slot index (9 to rows * 9 - 1).
     * @return true if the item was removed successfully, false if the GUI, page, or
     *         slot is invalid.
     */
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

    /**
     * Sets whether a GUI page allows player interactions (e.g., taking items).
     *
     * @param name         The name of the GUI.
     * @param pageId       The ID of the page.
     * @param allowInteract true to allow interactions, false to prevent them.
     * @return true if the interactability was set successfully, false if the GUI or
     *         page does not exist.
     */
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

    /**
     * Deletes a GUI from the system.
     *
     * @param name The name of the GUI to delete.
     * @return true if the GUI was deleted successfully, false if the GUI does not exist.
     */
    public boolean deleteGUI(String name) {
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUIs().remove(name) != null) {
            plugin.getDataStorage().saveGUIsAsync();
            return true;
        }
        return false;
    }

    /**
     * Opens a specific page of a GUI for a player.
     *
     * @param player The player to open the GUI for (non-null).
     * @param name   The name of the GUI.
     * @param pageId The ID of the page to open.
     * @return true if the GUI was opened successfully, false if the player, GUI, or
     *         page is invalid.
     */
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

    /**
     * Retrieves a read-only copy of a GUI's data.
     *
     * @param name The name of the GUI.
     * @return A read-only CustomGUI object, or null if the GUI does not exist.
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
     *
     * @return An unmodifiable set of GUI names.
     */
    public Set<String> listGUIs() {
        return Collections.unmodifiableSet(plugin.getGuiManager().getGUIs().keySet());
    }
}