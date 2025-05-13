package com.coderyo.coderyogui.api;

public class GUIResult {
    private final boolean success;
    private final String errorMessage;

    public GUIResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}