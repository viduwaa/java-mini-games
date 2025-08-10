package com.viduwa.minigames.menu;

import com.viduwa.minigames.auth.LoginScene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.util.Objects;

public class MainMenuScene {
    public static Parent get() {
        try {
            return FXMLLoader.load(Objects.requireNonNull(LoginScene.class.getResource("/fxml/header.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
