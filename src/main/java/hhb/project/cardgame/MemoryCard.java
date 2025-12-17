package hhb.project.cardgame;

import javafx.scene.image.Image;

public class MemoryCard extends Card {

    private boolean matched;

    public MemoryCard(String faceName, String suit) {
        super(faceName, suit);
        this.matched = false;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }


    public boolean isSameCard(MemoryCard otherCard) {
        return this.getSuit().equals(otherCard.getSuit()) &&
                this.getFaceName().equals(otherCard.getFaceName());
    }


    public Image getFaceImage() {
        String face = getFaceName().toLowerCase();
        String suit = getSuit().toLowerCase();
        String imagePath = "images/" + face + "_of_" + suit + ".png";

        return new Image(Card.class.getResourceAsStream(imagePath));
    }
}
