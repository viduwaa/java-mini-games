// LoginController.java
package com.viduwa.minigames.controllers;

import com.viduwa.minigames.models.User;
import com.viduwa.minigames.session.Session;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.viduwa.minigames.auth.AuthService;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.viduwa.minigames.Main.showMainMenu;
import static com.viduwa.minigames.Main.showRegisterScene;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label responseMsg;

    @FXML
    public void openRegister() {
        showRegisterScene();
    }

    @FXML
    private void openGitHubProfile() {
        try {
            String url = "https://github.com/viduwaa";
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else if (os.contains("mac")) {
                // macOS
                new ProcessBuilder("open", url).start();
            } else {
                // Linux/Unix
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not open GitHub profile");
        }
    }

    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            responseMsg.setTextFill(Color.RED);
            responseMsg.setText("Please enter username and password");
        }else{
            User user = AuthService.login(username, password);
            if (user != null) {
                Session.setUser(user);
                showMainMenu(user.getUsername());
            }else{
                responseMsg.setTextFill(Color.RED);
                responseMsg.setText("Invalid username or password");
            }
        }
    }
}
