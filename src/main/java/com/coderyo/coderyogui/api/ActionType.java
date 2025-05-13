package com.coderyo.coderyogui.api;

public enum ActionType {
    COMMAND, MESSAGE, SOUND, CLOSE;

    public static boolean isValid(String type) {
        try {
            valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}