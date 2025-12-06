package hhb.project.cardgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays; // Only needed if you use Arrays.asList()
import java.util.Locale;

public class Card {
    private String suit;
    private String faceName;

    public String getSuit() {
        return suit;
    }


    public static List<String> getValidFaceName()
    {
        return Arrays.asList("2","3","4","5","6","7","8","9","10","jack","queen","king","ace");
    }
    /**
     * valid face are "2","3","4","5","6","7","8","9","10","jack","queen","king","ace"
     * @param faceName
     */

    public static List<String> getValidSuits()
    {
        // Use List.of() for an unmodifiable list of valid suits (Java 9+)
        return List.of("hearts","diamonds","clubs","spades");
    }

    /**
     * valid suits are "hearts","diamonds","clubs","spades"
     * @param suit
     */

    public void setSuit(String suit) {
        suit =suit.toLowerCase();
        if (getValidSuits().contains(suit))
            this.suit = suit;
        else
            // Use IllegalArgumentException for invalid input
            throw new IllegalArgumentException(suit + " is Invalid. Must be one of: " + getValidSuits());
    }

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        faceName=faceName.toLowerCase();
        if(getValidFaceName().contains(faceName))
            this.faceName = faceName;
        else
            throw new IllegalArgumentException(faceName + "is valid must be one of " +getFaceName());
    }

    public Card(String faceName, String suit) {
        setSuit(suit);
        setFaceName(faceName);
    }

    public String toString()
    {
        return faceName+" of "+suit;
    }

    public String getColor()
    {
        if (suit.equals("hearts")|| suit.equals("diamonds")) //utiliser equals au lieu de ==
            return "red";
        else
            return "black";
    }

/**
 * cette methode retournera la valeur de la carte
 * "2","3","4","5","6","7","8","9","10","jack","queen","king","ace"
 */

    public int getValue()
    {
        return getValidFaceName().indexOf(faceName)+2;
    }









}