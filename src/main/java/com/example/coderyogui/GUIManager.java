package com.example.coderyogui;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private final Map<String, CustomGUI> guis;

    public GUIManager() {
        this.guis = new HashMap<>();
    }

    public Map<String, CustomGUI> getGUIs() {
        return guis;
    }

    public CustomGUI getGUI(String name) {
        return guis.get(name);
    }
}