package com.example.coderyogui;

import java.util.HashMap;
import java.util.Map;

public record GUIPage(Map<Integer, GUIItem> items, boolean allowInteract) {
    public GUIPage() {
        this(new HashMap<>(), false);
    }
}