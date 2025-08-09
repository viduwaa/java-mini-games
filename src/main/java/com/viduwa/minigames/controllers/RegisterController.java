package com.viduwa.minigames.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import com.viduwa.minigames.auth.AuthService;
import javafx.scene.paint.Color;

import javax.sql.rowset.spi.SyncFactory;

import static com.viduwa.minigames.Main.showLoginScene;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Hyperlink loginHyperlink;

    @FXML
    private Label responseMsg;

    /**
     * Handles the register button click event.
     * This method validates the input fields and performs the registration logic.
     *
     * @param event The ActionEvent that triggered this method.
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            responseMsg.setText("Please fill all the fields");
            System.out.println("Please fill in all fields.");
            // You would typically show an error message to the user here
        } else if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            responseMsg.setText("Passwords do not match.");
            // You would typically show an error message to the user here
        } else {
            if(AuthService.register(username, password)){
                responseMsg.setTextFill(Color.rgb(255,251,0));
                responseMsg.setText("Registration successful.");
            }else{
                responseMsg.setTextFill(Color.RED);
                responseMsg.setText("Registration failed.");
            }
            System.out.println("Registering user: " + username);
            System.out.println("Registration successful!");
        }
    }

    /**
     * Handles the "Login" hyperlink click event.
     * This method is responsible for switching the scene back to the login page.
     *
     * @param event The ActionEvent that triggered this method.
     */
    @FXML
    private void openLogin(ActionEvent event) {
        System.out.println("Navigating to the login scene.");
        showLoginScene();
    }
}
