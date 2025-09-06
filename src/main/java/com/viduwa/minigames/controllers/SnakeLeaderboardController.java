package com.viduwa.minigames.controllers;

import com.viduwa.minigames.dao.GameDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SnakeLeaderboardController {
    private static SnakeLeaderboardController instance;

    @FXML private TableView<SnakeLeaderboardEntry> snakeLeaderboardTable;
    @FXML private TableColumn<SnakeLeaderboardEntry, Integer> colPosition;
    @FXML private TableColumn<SnakeLeaderboardEntry, String> colName;
    @FXML private TableColumn<SnakeLeaderboardEntry, Integer> colScore;

    private final int SNAKE_GAME_ID = 1;

    public SnakeLeaderboardController() {
        instance = this;
    }

    public static SnakeLeaderboardController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        loadSnakeLeaderboard();
    }

    private void loadSnakeLeaderboard() {
        try (GameDAO dao = new GameDAO()) {
            List<String> leaderboard = dao.getHighScores(SNAKE_GAME_ID);

            int pos = 1;
            for (String row : leaderboard) {
                String[] parts = row.split(" - ");
                String username = parts[0];
                int score = Integer.parseInt(parts[1]);
                snakeLeaderboardTable.getItems().add(new SnakeLeaderboardEntry(pos++, username, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshSnakeLeaderboard() {
        snakeLeaderboardTable.getItems().clear();
        loadSnakeLeaderboard();
    }

    // Inner POJO class for table rows
    public static class SnakeLeaderboardEntry {
        private final int position;
        private final String username;
        private final int score;

        public SnakeLeaderboardEntry(int position, String username, int score) {
            this.position = position;
            this.username = username;
            this.score = score;
        }

        public int getPosition() { return position; }
        public String getUsername() { return username; }
        public int getScore() { return score; }
    }
}
