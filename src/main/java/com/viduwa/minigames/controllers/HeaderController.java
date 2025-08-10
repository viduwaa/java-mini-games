package com.viduwa.minigames.controllers;

import com.viduwa.minigames.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

import static com.viduwa.minigames.Main.showLoginScene;

public class HeaderController implements Initializable {
    @FXML
    private MediaView mediaView;

    @FXML
    private Pane contentPane;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label userLabel;

    @FXML
    private ImageView logoutImage;


    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("HeaderController initialize() called");
        setupVideoBackground();
        userLabel.setText(Session.getUser().getUsername());
    }

    private void setupVideoBackground() {
        try {
            URL videoURL = HeaderController.class.getResource("/videos/bg-loop1.mp4");

            Media media = new Media(videoURL.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.play();

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("End of media reached - seeking to beginning");
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // Set the media player to the view
            mediaView.setMediaPlayer(mediaPlayer);

        } catch (Exception e) {
            System.err.println("Exception in setupVideoBackground: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {
        // Stop media player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Session.setUser(null);

        // Show login screen
        showLoginScene();
    }

    public void profile(MouseEvent mouseEvent) {
    }
}