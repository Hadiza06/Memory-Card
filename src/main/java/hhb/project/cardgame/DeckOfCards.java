package hhb.project.cardgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DeckOfCards {

    private ArrayList<Card> deck;

    public DeckOfCards() {
        deck = new ArrayList<>();
        List<String> suits = Card.getValidSuits();
        List<String> faces = Card.getValidFaceName();

        for (String suit : suits) {
            for (String face : faces) {
                deck.add(new Card(face, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card dealTopCard() {
        return deck.size() > 0 ? deck.remove(0) : null;
    }

    public int getNumOfCards() {
        return deck.size();
    }
}
