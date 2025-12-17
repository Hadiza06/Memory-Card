package hhb.project.cardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Memory Game - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {

        launch();
    }
}


