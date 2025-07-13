package com.viduwa.minigames.menu;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

public class MainMenuScene {
    public static Parent get(String username) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label welcome = new Label("Welcome, " + username);
        Button snake = new Button("Play Snake");
        Button tetris = new Button("Play Tetris (Coming Soon)");

        layout.getChildren().addAll(welcome, snake, tetris);
        return layout;
    }
}
