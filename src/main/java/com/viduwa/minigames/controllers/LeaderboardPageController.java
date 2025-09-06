package com.viduwa.minigames.controllers;

import com.viduwa.minigames.session.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import static com.viduwa.minigames.Main.showMainMenu;

public class LeaderboardPageController {
    @FXML
    private void goBack() {
        try {
            showMainMenu(Session.getUser().getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
