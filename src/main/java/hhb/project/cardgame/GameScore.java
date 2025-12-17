package hhb.project.cardgame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameScore {
    private String username;
    private String difficulty;
    private int moves;
    private int timeSeconds;
    private int matchedPairs;
    private LocalDateTime date;

    public GameScore(String username, String difficulty, int moves, int timeSeconds, int matchedPairs) {
        this.username = username;
        this.difficulty = difficulty;
        this.moves = moves;
        this.timeSeconds = timeSeconds;
        this.matchedPairs = matchedPairs;
        this.date = LocalDateTime.now();
    }

    public GameScore(String username, String difficulty, int moves, int timeSeconds, int matchedPairs, LocalDateTime date) {
        this.username = username;
        this.difficulty = difficulty;
        this.moves = moves;
        this.timeSeconds = timeSeconds;
        this.matchedPairs = matchedPairs;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getMoves() {
        return moves;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public int getMatchedPairs() {
        return matchedPairs;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public int getScore() {
        return (timeSeconds * 2) + (moves * 10);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return username + ";" + difficulty + ";" + moves + ";" + timeSeconds + ";" + matchedPairs + ";" + date.format(formatter);
    }

    public static GameScore fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length == 6) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return new GameScore(
                    parts[0],
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    LocalDateTime.parse(parts[5], formatter)
            );
        }
        return null;
    }
}