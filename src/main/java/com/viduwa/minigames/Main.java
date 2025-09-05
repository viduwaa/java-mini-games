package com.viduwa.minigames;

import com.viduwa.minigames.auth.LoginScene;
import com.viduwa.minigames.auth.RegisterScene;
import com.viduwa.minigames.db.DBManager;
import com.viduwa.minigames.menu.MainMenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Rimouski.otf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/upheavtt.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Stormfaze.otf"), 14);
        primaryStage = stage;
        DBManager.initialize();
        showLoginScene();
    }

    public static void showLoginScene() {
        primaryStage.setScene(new Scene(LoginScene.get(),1440,900));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        primaryStage.setFullScreen(false);
        primaryStage.setTitle("PixelFun");
        primaryStage.show();
    }

    public static void showRegisterScene() {
        primaryStage.setScene(new Scene(RegisterScene.get()));
        primaryStage.setTitle("Register");
    }

    public static void showMainMenu(String username) {
        primaryStage.setScene(new Scene(MainMenuScene.get()));
        primaryStage.setTitle("Pixel Fun - Welcome " + username);
    }

    public static void main(String[] args) {
        launch();
    }
}
