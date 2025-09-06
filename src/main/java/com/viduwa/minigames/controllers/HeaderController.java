package com.viduwa.minigames.controllers;

import com.viduwa.minigames.dao.GameDAO;
import com.viduwa.minigames.games.SnakeGame;
import com.viduwa.minigames.games.TetrisGame;
import com.viduwa.minigames.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    private Label totalScore;
    @FXML
    private Label userLabel;
    @FXML
    private ImageView logoutImage;
    @FXML
    private VBox leaderboard;
    @FXML
    private ImageView checkLeaderboard;

    private MediaPlayer mediaPlayer;
    private Runnable scoreUpdateCallback; // Callback to refresh total score

    private static HeaderController instance;

    public HeaderController() {
        instance = this;
    }

    public static HeaderController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("HeaderController initialize() called");
        setupVideoBackground();
        refreshTotalScore(); // Initial score display

        // Check if user exists
        if (Session.getUser() != null) {
            userLabel.setText(Session.getUser().getUsername());
        } else {
            System.err.println("Warning: Session user is null!");
            userLabel.setText("Unknown User");
        }

        // Create callback to refresh total score
        scoreUpdateCallback = this::refreshTotalScore;

        try {
            // Container for multiple games
            HBox gameContainer = new HBox(30);
            gameContainer.setStyle("-fx-alignment: center; -fx-padding: 50;");

            System.out.println("Creating game container...");

            // Load Game 1 - Snake
            System.out.println("Loading Snake game...");
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GamePane.fxml"));
            if (loader1.getLocation() == null) {
                System.err.println("ERROR: GamePane.fxml not found!");
                return;
            }

            Parent game1 = loader1.load();
            GamePaneController controller1 = loader1.getController();

            if (controller1 == null) {
                System.err.println("ERROR: GamePaneController is null for game1!");
                return;
            }

            controller1.setGameData("Snake Game", "/images/snake.png",1, () -> {
                leaderboard.setVisible(false);
                SnakeGame snakeGame = new SnakeGame();
                snakeGame.setStyle("-fx-background-color: #111111;");

                // Pass the score update callback to the game
                snakeGame.setScoreUpdateCallback(scoreUpdateCallback);

                // Wrap in StackPane for automatic centering
                StackPane wrapper = new StackPane(snakeGame);
                wrapper.setAlignment(Pos.CENTER);
                wrapper.setPrefSize(contentPane.getWidth(), contentPane.getHeight());

                contentPane.getChildren().clear();
                contentPane.getChildren().add(wrapper);

                snakeGame.requestFocus();
            });

            System.out.println("Snake game loaded successfully");

            // Load Game 2 - Tetris
            System.out.println("Loading Tetris game...");
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GamePane.fxml"));
            Parent game2 = loader2.load();
            GamePaneController controller2 = loader2.getController();

            if (controller2 == null) {
                System.err.println("ERROR: GamePaneController is null for game2!");
                return;
            }

            controller2.setGameData("Tetris", "/images/tetris.png",2, () -> {
                leaderboard.setVisible(false);
                TetrisGame tetrisGame = new TetrisGame();

                tetrisGame.setStyle("-fx-background-color: #1a1a2e;");

                // Pass the score update callback
                tetrisGame.setScoreUpdateCallback(scoreUpdateCallback);

                // Wrap in StackPane for automatic centering
                StackPane wrapper = new StackPane(tetrisGame);
                wrapper.setAlignment(Pos.CENTER);
                wrapper.setPrefSize(contentPane.getWidth(), contentPane.getHeight());

                contentPane.getChildren().clear();
                contentPane.getChildren().add(wrapper);

                tetrisGame.requestFocus();
            });
            System.out.println("Tetris game loaded successfully");

            // Add both to container
            gameContainer.getChildren().addAll(game1, game2);

            // Clear and add to contentPane
            contentPane.getChildren().clear();
            contentPane.getChildren().add(gameContainer);

            // Force layout update
            Platform.runLater(() -> {
                contentPane.requestLayout();
            });

        } catch (Exception e) {
            System.err.println("Exception in initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openLeaderboard(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LeaderboardPage.fxml"));
            Parent leaderboardRoot = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(leaderboardRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to refresh the total score display
    public void refreshTotalScore() {
        Platform.runLater(() -> {
            try (GameDAO dao = new GameDAO()) {
                int total = dao.getUserTotalScore(Session.getUser().getId());
                totalScore.setText(String.valueOf(total));
                System.out.println("Total score updated: " + total);
            } catch (Exception e) {
                System.err.println("Error updating total score: " + e.getMessage());
                totalScore.setText("0");
            }
        });
    }

    private void setupVideoBackground() {
        try {
            URL videoURL = HeaderController.class.getResource("/videos/bg-loop1.mp4");
            if (videoURL == null) {
                System.err.println("Warning: Video file not found at /videos/bg-loop1.mp4");
                return;
            }

            Media media = new Media(videoURL.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaView.setMediaPlayer(mediaPlayer);

        } catch (Exception e) {
            System.err.println("Exception in setupVideoBackground: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Session.setUser(null);
        showLoginScene();
    }

    public void profile(MouseEvent mouseEvent) {
    }
}