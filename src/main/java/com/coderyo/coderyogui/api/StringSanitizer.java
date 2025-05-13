package com.coderyo.coderyogui.api;

public class StringSanitizer {
    public static String sanitize(String input) {
        if (input == null) return null;
        // Remove Minecraft color codes, keep non-ASCII characters like Chinese
        return input.replaceAll("§.", "");
    }
}