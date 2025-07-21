package org.example.Config;

public class Session {
    private static int currentUserId;
    public static void setCurrentUserId(int id) { currentUserId = id; }
    public static int getCurrentUserId() { return currentUserId; }
}

