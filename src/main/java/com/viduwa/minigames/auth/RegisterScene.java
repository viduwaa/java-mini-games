package com.viduwa.minigames.auth;

import com.viduwa.minigames.Main;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.util.Objects;

public class RegisterScene {
    public static Parent get() {
        try {
            return FXMLLoader.load(Objects.requireNonNull(LoginScene.class.getResource("/fxml/register.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void newRegister(String username,String password){


    }
}
