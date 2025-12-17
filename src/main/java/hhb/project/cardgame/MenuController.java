package hhb.project.cardgame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class MenuController {

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button leaderboardButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;
    private String selectedDifficulty;

    @FXML
    public void initialize() {
        User currentUser = DataManager.getCurrentUser();
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getUsername() + " !");
        }
    }

    @FXML
    void playEasy() {
        selectedDifficulty = "Facile";
        loadGame("memory-game-easy.fxml", "Memory Game - Facile");
    }

    @FXML
    void playMedium() {
        selectedDifficulty = "Moyen";
        loadGame("memory-game-medium.fxml", "Memory Game - Moyen");
    }

    @FXML
    void playHard() {
        selectedDifficulty = "Difficile";
        loadGame("memory-game-hard.fxml", "Memory Game - Difficile");
    }



    @FXML
    void handleLogout() {
        DataManager.logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Memory Game - Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MenuController.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) easyButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showLeaderboard() {

        if (selectedDifficulty == null) {
            showAlert("Veuillez choisir un niveau avant d'afficher le classement.");
            return;
        }

        List<GameScore> top10 = DataManager.getTop10(selectedDifficulty);

        StringBuilder message = new StringBuilder();
        message.append("🏆 TOP 10 - ").append(selectedDifficulty).append(" 🏆\n\n");

        if (top10.isEmpty()) {
            message.append("Aucune partie enregistrée pour ce niveau.");
        } else {
            int rank = 1;
            for (GameScore score : top10) {
                message.append(String.format(
                        "%d. %s - %d coups\n",
                        rank++,
                        score.getUsername(),
                        score.getMoves()
                ));
            }
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}