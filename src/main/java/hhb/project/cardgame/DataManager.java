package hhb.project.cardgame;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String SCORES_FILE = DATA_DIR + "/scores.txt";

    private static User currentUser = null;

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            File scoresFile = new File(SCORES_FILE);
            if (!scoresFile.exists()) scoresFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static boolean register(String username, String password) {
        if (username == null || password == null) return false;
        username = username.trim();
        password = password.trim();
        if (username.isEmpty() || password.isEmpty()) return false;

        try (Connection conn = DatabaseManager.getConnection()) {


            String checkSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) return false;
                }
            }

            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hashPassword(password, salt);

            String insertSql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, username);
                ps.setString(2, hash);
                ps.setString(3, salt);
                ps.executeUpdate();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(String username, String password) {
        if (username == null || password == null) return false;
        username = username.trim();
        password = password.trim() ; // <-- remove? (typo)
        if (username.isEmpty() || password.isEmpty()) return false;

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT password_hash, salt FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;

                    String hashDb = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    String hashIn = PasswordUtils.hashPassword(password, salt);

                    if (hashDb.equals(hashIn)) {
                        // On garde currentUser pour que MenuController/MemoryGameController fonctionnent
                        currentUser = new User(username, ""); // on ne stocke pas le mdp en clair
                        return true;
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }


    public static boolean saveScore(GameScore score) {
        try (Connection conn = DatabaseManager.getConnection()) {

            String userSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement psUser = conn.prepareStatement(userSql);
            psUser.setString(1, score.getUsername());
            ResultSet rs = psUser.executeQuery();

            if (!rs.next()) return false;
            int userId = rs.getInt("id");

            String sql = """
            INSERT INTO scores (user_id, difficulty, moves, time_seconds, matched_pairs, played_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, score.getDifficulty());
            ps.setInt(3, score.getMoves());
            ps.setInt(4, score.getTimeSeconds());
            ps.setInt(5, score.getMatchedPairs());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(score.getDate()));

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<GameScore> getAllScores() {
        List<GameScore> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                GameScore score = GameScore.fromString(line);
                if (score != null) scores.add(score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public static List<GameScore> getTop10(String difficulty) {
        List<GameScore> scores = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = """
            SELECT u.username, s.difficulty, s.moves, s.time_seconds, s.matched_pairs, s.played_at
            FROM scores s
            JOIN users u ON s.user_id = u.id
            WHERE s.difficulty = ?
            ORDER BY s.moves ASC
            LIMIT 10
        """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, difficulty);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                scores.add(new GameScore(
                        rs.getString("username"),
                        rs.getString("difficulty"),
                        rs.getInt("moves"),
                        rs.getInt("time_seconds"),
                        rs.getInt("matched_pairs"),
                        rs.getTimestamp("played_at").toLocalDateTime()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scores;
    }


    public static List<GameScore> getScoresByUser(String username) {
        return getAllScores().stream()
                .filter(score -> score.getUsername().equals(username))
                .sorted(Comparator.comparingInt(GameScore::getScore))
                .collect(Collectors.toList());
    }

    public static List<GameScore> getScoresByDifficulty(String difficulty) {
        return getAllScores().stream()
                .filter(score -> score.getDifficulty().equalsIgnoreCase(difficulty))
                .sorted(Comparator.comparingInt(GameScore::getScore))
                .limit(10)
                .collect(Collectors.toList());
    }
}
