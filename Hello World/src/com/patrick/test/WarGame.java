package com.patrick.test;

import java.util.*;

public class WarGame {
    private static Map<String, Integer> cardValues;

    static {
        cardValues = new HashMap<>();
        cardValues.put("2", 2);
        cardValues.put("3", 3);
        cardValues.put("4", 4);
        cardValues.put("5", 5);
        cardValues.put("6", 6);
        cardValues.put("7", 7);
        cardValues.put("8", 8);
        cardValues.put("9", 9);
        cardValues.put("10", 10);
        cardValues.put("Jack", 11);
        cardValues.put("Queen", 12);
        cardValues.put("King", 13);
        cardValues.put("Ace", 14);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players: ");
        int numPlayers = scanner.nextInt();

        System.out.print("Enter the number of shuffles: ");
        int numFaroShuffles = scanner.nextInt();
        
        System.out.print("---------------------------------" + "\n");

        Deck deck = new Deck();
        deck.faroShuffle(numFaroShuffles); // Perform Faro shuffle

        System.out.println("Shuffled deck before dealing:");
        System.out.print("---------------------------------" + "\n");
        deck.displayShuffledDeck(); // Display shuffled deck
        System.out.println();

        List<List<Card>> hands = deck.dealCards(numPlayers);

        int roundNumber = 1;
        while (hands.stream().allMatch(hand -> !hand.isEmpty())) {
        	System.out.print("---------------------------------" + "\n");
        	System.out.println("Round " + roundNumber + ":");
            List<Card> roundCards = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                List<Card> hand = hands.get(i);
                if (!hand.isEmpty()) {
                    Card card = hand.remove(0);
                    roundCards.add(card);
                   // System.out.println(hand);
                    System.out.println("Player " + (i + 1) + " plays: " + card.rank + " of " + card.suit);
                }
            }
            Card roundWinner = compareCards(roundCards);
            System.out.println("Round winner: " + roundWinner.rank + " of " + roundWinner.suit);
            roundNumber++;
        }

        int winnerIndex = findWinnerIndex(hands);
        System.out.print("---------------------------------" + "\n");
        System.out.println("Player " + (winnerIndex + 1) + " wins!");
        System.out.print("---------------------------------" + "\n");
    }

    private static Card compareCards(List<Card> cards) {
        Card maxCard = cards.get(0);
        for (Card card : cards) {
            if (cardValues.get(card.rank) > cardValues.get(maxCard.rank)) {
                maxCard = card;
            }
        }
        return maxCard;
    }

    private static int findWinnerIndex(List<List<Card>> hands) {
        for (int i = 0; i < hands.size(); i++) {
            if (hands.get(i).isEmpty()) {
                return i;
            }
        }
        return -1; // No winner found
    }
}
