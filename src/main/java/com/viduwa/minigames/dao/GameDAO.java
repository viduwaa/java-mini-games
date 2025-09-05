package com.viduwa.minigames.dao;

import com.viduwa.minigames.db.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO implements AutoCloseable {
    private Connection conn;

    public GameDAO() throws SQLException {
        this.conn = DBManager.getConnection();
    }

    // ============================
    // Score Queries
    // ============================

    public void addScore(int userId, int gameId, int score) throws SQLException {
        Integer currentHighScore = getUserHighScoreForGame(userId, gameId);
        System.out.println(currentHighScore);

        if (currentHighScore == 0) {
            // No score yet → insert new record
            String insertSql = "INSERT INTO scores (user_id, game_id, score) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, gameId);
                stmt.setInt(3, score);
                stmt.executeUpdate();
            }
        } else {
            // New high score → update existing record
            String updateSql = "UPDATE scores SET score = ? WHERE user_id = ? AND game_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, score);
                stmt.setInt(2, userId);
                stmt.setInt(3, gameId);
                stmt.executeUpdate();
            }
        }
        // If score <= currentHighScore, do nothing
    }


    public List<String> getHighScores(int gameId) throws SQLException {
        String sql = """
            SELECT u.username, s.score, s.played_at
            FROM scores s
            JOIN users u ON s.user_id = u.user_id
            WHERE s.game_id = ?
            ORDER BY s.score DESC
            LIMIT 10
        """;

        List<String> results = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("username") + " - " + rs.getInt("score"));
            }
        }
        return results;
    }

    // Get a user's highest score for a specific game
    public int getUserHighScoreForGame(int userId, int gameId) throws SQLException {
        String sql = """
            SELECT MAX(score) AS high_score
            FROM scores
            WHERE user_id = ? AND game_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, gameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("high_score");
            }
        }
        return 0; // Return 0 if no scores found
    }


    public int getUserTotalScore(int userId) throws SQLException {
        String sql = """
            SELECT SUM(s.score) AS total_score
            FROM scores s
            WHERE s.user_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_score");
            }
        }
        return 0;
    }

    public List<String> getLeaderboard() throws SQLException {
        String sql = """
            SELECT u.username, SUM(s.score) AS total_score
            FROM scores s
            JOIN users u ON s.user_id = u.id
            GROUP BY u.id
            ORDER BY total_score DESC
            LIMIT 10
        """;

        List<String> results = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("username") + " - " + rs.getInt("total_score"));
            }
        }
        return results;
    }

    public void deleteScoresByUserAndGame(int userId, int gameId) throws SQLException {
        String sql = "DELETE FROM scores WHERE user_id = ? AND game_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, gameId);
            stmt.executeUpdate();
        }
    }


    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
