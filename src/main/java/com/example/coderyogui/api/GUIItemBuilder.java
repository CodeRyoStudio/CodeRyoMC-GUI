package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.GUIAction;
import com.coderyo.coderyogui.GUIItem;

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
        return new GUIItem(material, name, lore, takeable, actions);
    }
}