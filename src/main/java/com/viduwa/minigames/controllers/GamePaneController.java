package com.viduwa.minigames.controllers;

import com.viduwa.minigames.dao.GameDAO;
import com.viduwa.minigames.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GamePaneController {

    @FXML
    private ImageView gameImage;

    @FXML
    private Label gameTitle;

    @FXML
    private Button playButton;

    @FXML
    private Label gameHighScore;

    @FXML private Button deleteScoreButton;

    private int GameID;

    @FXML
    private void initialize() {
        deleteScoreButton.setOnAction(e -> handleDeleteHighScore());
    }

    int currentHighScore = 0;

    // Call this after loading to set details
    public void setGameData(String title, String imagePath,int GameID, Runnable onPlay) {
        this.GameID = GameID;
        System.out.println("setGameData called with title: " + title + ", imagePath: " + imagePath);

        try (GameDAO dao = new GameDAO()) {
            currentHighScore = dao.getUserHighScoreForGame(Session.getUser().getId(), GameID);
            if(currentHighScore == 0 ){
                gameHighScore.setText("");
            }else{
                gameHighScore.setText("Your H.Score: "+currentHighScore);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gameTitle != null) {
            gameTitle.setText(title);
            System.out.println("Game title set successfully");
        } else {
            System.err.println("ERROR: gameTitle is null!");
        }

        if (gameImage != null) {
            try {
                // Check if image resource exists
                if (getClass().getResourceAsStream(imagePath) != null) {
                    gameImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                    System.out.println("Game image set successfully");
                } else {
                    System.err.println("ERROR: Image not found at path: " + imagePath);
                    // Set a fallback or default image if available
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to load image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("ERROR: gameImage is null!");
        }

        if (playButton != null) {
            playButton.setOnAction(e -> {
                System.out.println("Play button clicked for: " + title);
                if (onPlay != null) {
                    onPlay.run();
                } else {
                    System.err.println("WARNING: onPlay runnable is null!");
                }
            });
            System.out.println("Play button action set successfully");
        } else {
            System.err.println("ERROR: playButton is null!");
        }
    }

    private void handleDeleteHighScore() {
        try (GameDAO dao = new GameDAO();) {

            // Example: delete all scores for the current game & logged-in user
            dao.deleteScoresByUserAndGame(Session.getUser().getId(), GameID);
            System.out.println("deleted scores for game"+GameID);

            gameHighScore.setText("");
            System.out.println("High score deleted.");

            //Refresh total score in header
            if (HeaderController.getInstance() != null) {
                HeaderController.getInstance().refreshTotalScore();
            }

            // update leaderboard UI in real time
            if (LeaderboardUIController.getInstance() != null) {
                LeaderboardUIController.getInstance().refreshLeaderboard();
                System.out.println("Leaderboard refreshed after score delete.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}