package com.viduwa.minigames.controllers;

import com.viduwa.minigames.dao.GameDAO;
import com.viduwa.minigames.db.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.util.List;

public class LeaderboardUIController {
    private static LeaderboardUIController instance;

    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, Integer> colPosition;
    @FXML private TableColumn<LeaderboardEntry, String> colName;
    @FXML private TableColumn<LeaderboardEntry, Integer> colScore;

    @FXML
    public void initialize() {
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("totalScore"));

        loadLeaderboard();
    }

    public LeaderboardUIController() {
        instance = this;
    }

    public static LeaderboardUIController getInstance() {
        return instance;
    }

    private void loadLeaderboard() {
        try (GameDAO dao = new GameDAO()) {
            List<String> leaderboard = dao.getLeaderboard();
            System.out.println(leaderboard);

            int pos = 1;
            for (String row : leaderboard) {
                String[] parts = row.split(" - ");
                String username = parts[0];
                int score = Integer.parseInt(parts[1]);
                leaderboardTable.getItems().add(new LeaderboardEntry(pos++, username, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class (POJO for table row)
    public static class LeaderboardEntry {
        private final int position;
        private final String username;
        private final int totalScore;

        public LeaderboardEntry(int position, String username, int totalScore) {
            this.position = position;
            this.username = username;
            this.totalScore = totalScore;
        }

        public int getPosition() { return position; }
        public String getUsername() { return username; }
        public int getTotalScore() { return totalScore; }
    }

    public void refreshLeaderboard() {
        leaderboardTable.getItems().clear();
        try (GameDAO dao = new GameDAO();) {
            List<String> leaderboard = dao.getLeaderboard();
            int pos = 1;
            for (String row : leaderboard) {
                String[] parts = row.split(" - ");
                String username = parts[0];
                int score = Integer.parseInt(parts[1]);
                leaderboardTable.getItems().add(new LeaderboardEntry(pos++, username, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
