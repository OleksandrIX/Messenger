package com.obm.ukrainiansmessenger.models;

public class UserTransporter {
    private static boolean userAvailable = false;
    private static User user;

    public static boolean isUserAvailable() {
        return userAvailable;
    }

    public static void setUser(User user) {
        UserTransporter.user = user;
        userAvailable = true;
    }

    public static User getUser() {
        userAvailable = false;
        return user;
    }
}
