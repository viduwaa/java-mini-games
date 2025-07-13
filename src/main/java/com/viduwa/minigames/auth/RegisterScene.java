package com.viduwa.minigames.auth;

import com.viduwa.minigames.Main;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

public class RegisterScene {
    public static Parent get() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Register");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Login");

        Label message = new Label();

        registerButton.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setText("Fill all fields");
                return;
            }

            if (AuthService.register(user, pass)) {
                message.setText("Registration successful!");
            } else {
                message.setText("Username already taken");
            }
        });

        backButton.setOnAction(e -> Main.showLoginScene());

        layout.getChildren().addAll(title, usernameField, passwordField, registerButton, backButton, message);
        return layout;
    }
}
