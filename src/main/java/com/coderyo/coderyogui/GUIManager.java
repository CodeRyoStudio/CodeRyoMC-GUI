package com.coderyo.coderyogui;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private final Map<String, CustomGUI> guis;
    private final Map<String, CustomGUI> temporaryGUIs;

    public GUIManager() {
        this.guis = new HashMap<>();
        this.temporaryGUIs = new HashMap<>();
    }

    public Map<String, CustomGUI> getGUIs() {
        return guis;
    }

    public Map<String, CustomGUI> getTemporaryGUIs() {
        return temporaryGUIs;
    }

    public CustomGUI getGUI(String name) {
        return temporaryGUIs.getOrDefault(name, guis.get(name));
    }

    public boolean isTemporary(String name) {
        return temporaryGUIs.containsKey(name);
    }

    public void clearTemporaryGUIs() {
        temporaryGUIs.clear();
    }
}