package hhb.project.cardgame;

import java.util.List;
import java.util.Arrays;
import javafx.scene.image.Image;

public class Card {

    private String suit;
    private String faceName;

    public Card(String faceName, String suit) {
        setSuit(suit);
        setFaceName(faceName);
    }

    public String getSuit() {
        return suit;
    }

    public static List<String> getValidFaceName() {
        return Arrays.asList("2","3","4","5","6","7","8","9","10","jack","queen","king","ace");
    }

    public static List<String> getValidSuits() {
        return List.of("hearts","diamonds","clubs","spades");
    }

    public void setSuit(String suit) {
        suit = suit.toLowerCase();
        if (getValidSuits().contains(suit))
            this.suit = suit;
        else
            throw new IllegalArgumentException(suit + " is Invalid. Must be one of: " + getValidSuits());
    }

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        faceName = faceName.toLowerCase();
        if (getValidFaceName().contains(faceName))
            this.faceName = faceName;
        else
            throw new IllegalArgumentException(faceName + " is invalid. Must be one of: " + getValidFaceName());
    }

    public Image getBackOfCardImage() {
        return new Image(Card.class.getResourceAsStream("images/back_of_card.png"));
    }
}
