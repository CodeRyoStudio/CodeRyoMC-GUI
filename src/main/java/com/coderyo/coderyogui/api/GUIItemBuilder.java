package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.GUIAction;
import com.coderyo.coderyogui.GUIItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for creating {@link GUIItem} instances with customizable properties
 * such as material, name, lore, takeability, and actions.
 */
public class GUIItemBuilder {
    private String material = "AIR";
    private String name;
    private List<String> lore = new ArrayList<>();
    private boolean takeable = false;
    private List<GUIAction> actions = new ArrayList<>();

    /**
     * Sets the material of the GUI item.
     *
     * @param material The material name (e.g., "DIAMOND"). Defaults to "AIR" if null.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder material(String material) {
        this.material = material != null ? material : "AIR";
        return this;
    }

    /**
     * Sets the display name of the GUI item.
     *
     * @param name The display name, or null for no custom name.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the lore (description) of the GUI item.
     *
     * @param lore The list of lore lines, or null for an empty list.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder lore(List<String> lore) {
        this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        return this;
    }

    /**
     * Adds a single line to the GUI item's lore.
     *
     * @param line The lore line to add.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    /**
     * Sets whether the GUI item can be taken by players.
     *
     * @param takeable true if the item can be taken, false otherwise. Defaults to false.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder takeable(boolean takeable) {
        this.takeable = takeable;
        return this;
    }

    /**
     * Adds a custom action to the GUI item.
     *
     * @param action The action to add, or null (ignored).
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addAction(GUIAction action) {
        if (action != null) {
            this.actions.add(action);
        }
        return this;
    }

    /**
     * Adds a command action to the GUI item.
     *
     * @param command   The command to execute (e.g., "give %player% diamond 1").
     * @param asConsole true to execute as console, false to execute as player.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addCommandAction(String command, boolean asConsole) {
        if (command == null || command.isEmpty() || command.length() > 100) {
            throw new IllegalArgumentException("Command must be 1-100 characters");
        }
        this.actions.add(new GUIAction(ActionType.COMMAND, command, asConsole));
        return this;
    }

    /**
     * Adds a message action to send a chat message to the player.
     *
     * @param message The message to send.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addMessageAction(String message) {
        if (message == null || message.isEmpty() || message.length() > 100) {
            throw new IllegalArgumentException("Message must be 1-100 characters");
        }
        this.actions.add(new GUIAction(ActionType.MESSAGE, message));
        return this;
    }

    /**
     * Adds a sound action to play a sound for the player.
     *
     * @param sound  The sound name (e.g., "ENTITY_EXPERIENCE_ORB_PICKUP").
     * @param volume The sound volume (default 1.0).
     * @param pitch  The sound pitch (default 1.0).
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addSoundAction(String sound, float volume, float pitch) {
        if (sound == null || sound.isEmpty() || sound.length() > 100) {
            throw new IllegalArgumentException("Sound name must be 1-100 characters");
        }
        this.actions.add(new GUIAction(ActionType.SOUND, sound, false, volume, pitch));
        return this;
    }

    /**
     * Adds an action to close the player's GUI.
     *
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder addCloseAction() {
        this.actions.add(new GUIAction(ActionType.CLOSE, "close"));
        return this;
    }

    /**
     * Sets the list of actions for the GUI item.
     *
     * @param actions The list of actions, or null for an empty list.
     * @return This builder instance for method chaining.
     */
    public GUIItemBuilder actions(List<GUIAction> actions) {
        this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
        return this;
    }

    /**
     * Builds and returns the configured GUIItem.
     *
     * @return The constructed GUIItem instance.
     */
    public GUIItem build() {
        return new GUIItem(material, name, lore, takeable, actions);
    }
}