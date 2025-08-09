// LoginController.java
package com.viduwa.minigames.controllers;

import com.viduwa.minigames.models.User;
import com.viduwa.minigames.session.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.viduwa.minigames.auth.AuthService;
import javafx.scene.paint.Color;

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
