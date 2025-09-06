package com.viduwa.minigames.controllers;

import com.viduwa.minigames.dao.GameDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TetrisLeaderboardController {
    private static TetrisLeaderboardController instance;

    @FXML private TableView<TetrisLeaderboardEntry> tetrisLeaderboardTable;
    @FXML private TableColumn<TetrisLeaderboardEntry, Integer> colPosition;
    @FXML private TableColumn<TetrisLeaderboardEntry, String> colName;
    @FXML private TableColumn<TetrisLeaderboardEntry, Integer> colScore;

    private final int TETRIS_GAME_ID = 2;

    public TetrisLeaderboardController() {
        instance = this;
    }

    public static TetrisLeaderboardController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        loadTetrisLeaderboard();
    }

    private void loadTetrisLeaderboard() {
        try (GameDAO dao = new GameDAO()) {
            List<String> leaderboard = dao.getHighScores(TETRIS_GAME_ID);

            int pos = 1;
            for (String row : leaderboard) {
                String[] parts = row.split(" - ");
                String username = parts[0];
                int score = Integer.parseInt(parts[1]);
                tetrisLeaderboardTable.getItems().add(new TetrisLeaderboardEntry(pos++, username, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTetrisLeaderboard() {
        tetrisLeaderboardTable.getItems().clear();
        loadTetrisLeaderboard();
    }

    // Inner POJO class for table rows
    public static class TetrisLeaderboardEntry {
        private final int position;
        private final String username;
        private final int score;

        public TetrisLeaderboardEntry(int position, String username, int score) {
            this.position = position;
            this.username = username;
            this.score = score;
        }

        public int getPosition() { return position; }
        public String getUsername() { return username; }
        public int getScore() { return score; }
    }
}
