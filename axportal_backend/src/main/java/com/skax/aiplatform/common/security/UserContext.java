package com.skax.aiplatform.common.security;

public class UserContext {
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static void setCurrentUsername(String username) {
        currentUsername.set(username);
    }

    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static void clear() {
        currentUsername.remove();
    }
}