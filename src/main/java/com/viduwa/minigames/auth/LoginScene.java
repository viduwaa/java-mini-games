// LoginScene.java
package com.viduwa.minigames.auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.Objects;

public class LoginScene {
    public static Parent get() {
        try {
            return FXMLLoader.load(Objects.requireNonNull(LoginScene.class.getResource("/fxml/login.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
