package com.patrick.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {
	    List<Card> cards;

	    Deck() {
	        this.cards = createDeck();
	    }

	    private List<Card> createDeck() {
	        List<String> suits = Arrays.asList("Hearts", "Diamonds", "Clubs", "Spades");
	        List<String> ranks = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace");
	        List<Card> deck = new ArrayList<>();

	        for (String suit : suits) {
	            for (String rank : ranks) {
	                deck.add(new Card(suit, rank));
	            }
	        }
	        Collections.shuffle(deck);
	        return deck;
	    }

	    void faroShuffle(int times) {
	        int halfSize = cards.size() / 2;
	        for (int i = 0; i < times; i++) {
	            List<Card> topHalf = cards.subList(0, halfSize);
	            List<Card> bottomHalf = cards.subList(halfSize, cards.size());

	            List<Card> interwovenDeck = new ArrayList<>();
	            for (int j = 0; j < halfSize; j++) {
	                interwovenDeck.add(topHalf.get(j));
	                interwovenDeck.add(bottomHalf.get(j));
	            }
	            cards = interwovenDeck;
	        }
	    }

	    void displayShuffledDeck() {
	        for (Card card : cards) {
	            System.out.println(card.rank + " of " + card.suit);
	        }
	    }

	    List<List<Card>> dealCards(int numPlayers) {
	        List<List<Card>> hands = new ArrayList<>();
	        for (int i = 0; i < numPlayers; i++) {
	            hands.add(new ArrayList<>());
	        }

	        int currentPlayer = 0;
	        while (!cards.isEmpty()) {
	            Card card = cards.remove(0);
	            hands.get(currentPlayer).add(card);
	            currentPlayer = (currentPlayer + 1) % numPlayers;
	        }
	        return hands;
	    }
	}

