package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.GUIAction;
import com.coderyo.coderyogui.GUIItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GUIItemBuilder {
    private String material = "AIR";
    private String name;
    private List<String> lore = new ArrayList<>();
    private boolean takeable = false;
    private List<GUIAction> actions = new ArrayList<>();

    public GUIItemBuilder material(String material) {
        this.material = material != null ? material : "AIR";
        return this;
    }

    public GUIItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GUIItemBuilder lore(List<String> lore) {
        this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        return this;
    }

    public GUIItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public GUIItemBuilder takeable(boolean takeable) {
        this.takeable = takeable;
        return this;
    }

    public GUIItemBuilder addAction(GUIAction action) {
        if (action != null) {
            this.actions.add(action);
        }
        return this;
    }

    public GUIItemBuilder addCommandAction(String command, boolean asConsole) {
        this.actions.add(new GUIAction("command", command, asConsole));
        return this;
    }

    public GUIItemBuilder addMessageAction(String message) {
        this.actions.add(new GUIAction("message", message));
        return this;
    }

    public GUIItemBuilder addSoundAction(String sound, float volume, float pitch) {
        this.actions.add(new GUIAction("sound", sound, false, volume, pitch));
        return this;
    }

    public GUIItemBuilder addCloseAction() {
        this.actions.add(new GUIAction("close", "close"));
        return this;
    }

    public GUIItemBuilder actions(List<GUIAction> actions) {
        this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
        return this;
    }

    public GUIItem build() {
        // 驗證 material
        if (material == null || Material.matchMaterial(material) == null) {
            material = "AIR";
        }

        // 驗證 name
        if (name != null && (name.isEmpty() || name.length() > 100)) {
            throw new IllegalArgumentException("Item name must be 1-100 characters");
        }

        // 驗證 lore
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            for (String line : lore) {
                if (line != null && line.length() > 100) {
                    throw new IllegalArgumentException("Lore line must be <= 100 characters");
                }
                if (line != null && containsInvalidChars(line)) {
                    throw new IllegalArgumentException("Lore contains invalid characters");
                }
            }
        }

        // 驗證 actions
        if (actions == null) {
            actions = new ArrayList<>();
        } else {
            for (GUIAction action : actions) {
                if (action == null || action.type() == null || action.value() == null) {
                    throw new IllegalArgumentException("Invalid GUIAction: type and value cannot be null");
                }
                if (action.type().isEmpty() || action.value().isEmpty()) {
                    throw new IllegalArgumentException("GUIAction type and value must not be empty");
                }
                if (action.type().length() > 50 || action.value().length() > 100) {
                    throw new IllegalArgumentException("GUIAction type or value too long");
                }
                if (containsInvalidChars(action.value())) {
                    throw new IllegalArgumentException("GUIAction value contains invalid characters");
                }
            }
        }

        return new GUIItem(material, name, lore, takeable, actions);
    }

    private boolean containsInvalidChars(String input) {
        if (input == null) return false;
        return input.contains("\n") || input.contains("\r") || input.contains(": ") || input.contains("&") || input.contains("?");
    }
}