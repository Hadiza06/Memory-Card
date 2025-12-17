package hhb.project.cardgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.animation.PauseTransition;

public class MemoryGameController implements Initializable {

    @FXML private Label guessesLabel;
    @FXML private Label correctGuessesLabel;
    @FXML private Label timerLabel;
    @FXML private FlowPane imagesFlowPane;
    @FXML private Button playAgain;
    @FXML private Button backToMenu;

    private ArrayList<MemoryCard> cardsInGame;
    private int numOfGuess = 0;
    private int numOfMatched = 0;

    private MemoryCard firstCard = null;
    private MemoryCard secondCard = null;

    private MediaPlayer bgMusic;
    private AudioClip matchSound;
    private AudioClip failSound;

    private int previewTime;
    private String difficulty;

    // Chronomètre
    private int elapsedSeconds = 0;
    private Timeline timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeImageView();
        playAgain.setOnAction(event -> playAgain());

        if (backToMenu != null) {
            backToMenu.setOnAction(event -> returnToMenu());
        }

        // Détermine le niveau et le temps d'aperçu
        int numCards = imagesFlowPane.getChildren().size();
        if (numCards <= 12) {
            previewTime = 5;
            difficulty = "Facile";
        } else if (numCards <= 16) {
            previewTime = 4;
            difficulty = "Moyen";
        } else {
            previewTime = 3;
            difficulty = "Difficile";
        }

        playAgain();
    }

    @FXML
    void playAgain() {
        numOfGuess = 0;
        numOfMatched = 0;
        elapsedSeconds = 0;
        updateLabels();
        firstCard = null;
        secondCard = null;

        // Arrêter le chronomètre s'il existe
        if (timer != null) {
            timer.stop();
        }

        DeckOfCards deck = new DeckOfCards();
        deck.shuffle();

        cardsInGame = new ArrayList<>();
        for (int i = 0; i < imagesFlowPane.getChildren().size() / 2; i++) {
            Card dealt = deck.dealTopCard();
            cardsInGame.add(new MemoryCard(dealt.getFaceName(), dealt.getSuit()));
            cardsInGame.add(new MemoryCard(dealt.getFaceName(), dealt.getSuit()));
        }
        Collections.shuffle(cardsInGame);

        setCardInteractions(false);
        showAllFaces();

        PauseTransition delay = new PauseTransition(Duration.seconds(previewTime));
        delay.setOnFinished(event -> {
            flipAllCard();
            setCardInteractions(true);
            startTimer(); // Démarrer le chronomètre après l'aperçu
        });
        delay.play();
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            updateTimerLabel();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTimerLabel() {
        if (timerLabel != null) {
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void returnToMenu() {
        try {
            stopTimer();
            if (bgMusic != null) {
                bgMusic.stop();
            }
            FXMLLoader loader = new FXMLLoader(MenuController.class.getResource("menu-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backToMenu.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Memory Game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSounds() {
        String bgPath = getClass().getResource("/sounds/bg_music.mp3").toExternalForm();
        bgMusic = new MediaPlayer(new Media(bgPath));
        bgMusic.setCycleCount(MediaPlayer.INDEFINITE);
        bgMusic.setVolume(0.25);

        matchSound = new AudioClip(getClass().getResource("/sounds/match.mp3").toExternalForm());
        failSound = new AudioClip(getClass().getResource("/sounds/fail.mp3").toExternalForm());
    }

    private void initializeImageView() {
        loadSounds();
        bgMusic.play();

        for (int i = 0; i < imagesFlowPane.getChildren().size(); i++) {
            ImageView iv = (ImageView) imagesFlowPane.getChildren().get(i);
            iv.setImage(new Image(Card.class.getResourceAsStream("images/back_of_card.png")));
            iv.setUserData(i);
            iv.setOnMouseClicked(event -> {
                int index = (int) iv.getUserData();
                flipCard(index);
            });
        }
    }

    private void flipAllCard() {
        for (int i = 0; i < cardsInGame.size(); i++) {
            ImageView iv = (ImageView) imagesFlowPane.getChildren().get(i);
            MemoryCard card = cardsInGame.get(i);

            if (card.isMatched()) {
                iv.setImage(card.getFaceImage());
            } else {
                iv.setImage(card.getBackOfCardImage());
            }
        }
    }

    private void flipCard(int index) {
        if (firstCard == null && secondCard == null)
            flipAllCard();

        MemoryCard card = cardsInGame.get(index);
        ImageView iv = (ImageView) imagesFlowPane.getChildren().get(index);

        if (card.isMatched() || card == firstCard) return;

        if (firstCard == null) {
            firstCard = card;
            iv.setImage(card.getFaceImage());
        } else if (secondCard == null) {
            secondCard = card;
            iv.setImage(card.getFaceImage());

            numOfGuess++;
            checkForMatch();
            updateLabels();
        }
    }

    private void checkForMatch() {
        if (firstCard.isSameCard(secondCard)) {
            numOfMatched++;
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchSound.play();

            // Vérifier si le jeu est terminé
            if (numOfMatched == cardsInGame.size() / 2) {
                stopTimer();
                gameCompleted();
            }
        } else {
            failSound.play();
        }

        firstCard = null;
        secondCard = null;
        flipAllCard();
    }

    private void gameCompleted() {
        User currentUser = DataManager.getCurrentUser();

        if (currentUser != null) {
            // Sauvegarder le score
            GameScore score = new GameScore(
                    currentUser.getUsername(),
                    difficulty,
                    numOfGuess,
                    elapsedSeconds,
                    numOfMatched
            );
            DataManager.saveScore(score);

            // Afficher le message de félicitations
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Félicitations !");
            alert.setHeaderText("Partie terminée !");
            alert.setContentText(String.format(
                    "🎉 Bravo %s !\n\n" +
                            "Niveau : %s\n" +
                            "Tentatives : %d\n" +
                            "Temps : %d secondes\n" +
                            "Score : %d\n\n" +
                            "Votre score a été sauvegardé !",
                    currentUser.getUsername(),
                    difficulty,
                    numOfGuess,
                    elapsedSeconds,
                    score.getScore()
            ));
            alert.showAndWait();
        }
    }

    private void updateLabels() {
        guessesLabel.setText(Integer.toString(numOfGuess));
        correctGuessesLabel.setText(Integer.toString(numOfMatched));
    }

    private void showAllFaces() {
        for (int i = 0; i < cardsInGame.size(); i++) {
            ImageView iv = (ImageView) imagesFlowPane.getChildren().get(i);
            MemoryCard card = cardsInGame.get(i);
            iv.setImage(card.getFaceImage());
        }
    }

    private void setCardInteractions(boolean enable) {
        for (int i = 0; i < imagesFlowPane.getChildren().size(); i++) {
            ImageView iv = (ImageView) imagesFlowPane.getChildren().get(i);
            iv.setDisable(!enable);
        }
    }
}