module hhb.project.cardgame {
    requires javafx.controls;
    requires java.sql;
    requires javafx.fxml;
    requires javafx.media;

    opens hhb.project.cardgame to javafx.fxml;
    exports hhb.project.cardgame;
}