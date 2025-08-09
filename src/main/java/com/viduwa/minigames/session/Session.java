package com.viduwa.minigames.session;

import com.viduwa.minigames.models.User;

public class Session {
    private static User loggedInUser;

    public static void setUser(User user) {
        loggedInUser = user;
    }

    public static User getUser() {
        return loggedInUser;
    }
}
