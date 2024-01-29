package KlondikeSolitaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Game {
	public static final int numFoundation = 4;
	public static final int numTableau = 7;
	public static final int numCards = 52;

	public static List<Card> deck;
	public static List<Card>[] foundations;
	public static List<Card>[] tableau;
	public static List<Card> talon;
	public static List<Card> waste;

	boolean gameOver = false;
	int counter = 0;

	public enum Suit {
		DIAMONDS, SPADES, HEARTS, CLUBS // arranged in ordinal for the colors black (spades & clubs) and red (hearts &
										// diamonds)
	}

	public class Card {
		public Suit suit;
		public int rank;
		public boolean faceUp;

		Card(Suit suit, int rank, boolean faceUp) {
			this.suit = suit;
			this.rank = rank;
			this.faceUp = faceUp;
		}

		public Suit getSuit() {
			return suit;
		}

		@Override
		public String toString() {
			String ANSI_RED = "\u001B[31m"; // ANSI escape code for red color
			String ANSI_BLACK = "\u001B[30m"; // ANSI escape code for BLACK color
			String ANSI_RESET = "\u001B[0m"; // ANSI escape code to reset color

			if (faceUp) {
				String[] ranks = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
				String cardString = ranks[rank - 1] + " of ";

				// red color for hearts and diamonds
				if (suit == Suit.HEARTS || suit == Suit.DIAMONDS) {
					return ANSI_RED + cardString + suit + ANSI_RESET;
				} else {
					// red color for spades and clubs
					return ANSI_BLACK + cardString + suit + ANSI_RESET;
				}
			} else {
				return "▬";
			}
		}
	}

	public void initialize() {
		deck = new ArrayList<>();
		foundations = new ArrayList[numFoundation];
		tableau = new ArrayList[numTableau];
		talon = new ArrayList<>();
		waste = new ArrayList<>();

		for (int i = 0; i < numFoundation; i++) {
			foundations[i] = new ArrayList<>();
		}

		for (int i = 0; i < numTableau; i++) {
			tableau[i] = new ArrayList<>();
		}

		for (int i = 0; i < numCards; i++) {
			Card card = new Card(Suit.values()[i / 13], (i % 13) + 1, false);
			deck.add(card);
		}
		Scanner scan = new Scanner(System.in);
		System.out.print("Do you want to shuffle the cards before starting the game? (yes/no): ");
		String response = scan.nextLine().toLowerCase();
		if (response.equals("yes")) {
			System.out.println("\nCards shuffling....");
			Collections.shuffle(deck);
			// displayInitialArrangement();
		} else if (!response.equals("no")) {
			System.out.println("\nInvalid input. Defaulting to not shuffling.");
			// displayInitialArrangement();
		}

		// Collections.shuffle(deck);
		// displayAllCards();

		// initialize tableau piles
		int tableauCardCount = 0;
		for (int j = 0; j < numTableau; j++) {
			tableau[j] = new ArrayList<>();
		}

		for (int i = 0; i < numTableau; i++) {
			for (int j = i; j < numTableau; j++) {
				Card card = deck.remove(0);
				if (j == i) {
					card.faceUp = true;
				}
				tableau[j].add(card);
				tableauCardCount++;
			}
		}

		// remaining cards after tableau initialization are for the talon
		for (int i = tableauCardCount; i < numCards; i++) {
			Card card = deck.remove(0);
			talon.add(card);
		}

	}

	/*
	 * public void displayAllCards() {
	 * 
	 * for (Card card : deck) { System.out.println(card); } }
	 */

	public void playGame() {

		while (!gameOver) {
			boolean moved = makeMoves();

			if (!moved) {
				drawCard(); // draw a card if no moves are available
			}
			displayGameState();
			if (counter == 3) {
				gameOver = true;
			}
		}

		if (isGameOver()) {
			System.out.println("\n=========================================================================");
			System.out.println("                      Congratulations! You won!");
			System.out.println("=========================================================================");
		} else {
			System.out.println("\n=========================================================================");
			System.out.println("                   No more possible moves. Game over!");
			System.out.println("=========================================================================");
		}
	}

	public void drawCard() {
		if (talon.isEmpty()) {
			// If talon is empty, move all cards from waste to talon
			talon.addAll(waste);
			waste.clear();
			// Reverse the order to maintain the correct order in talon
			Collections.reverse(talon);
			counter++;
		}

		else {
			Card drawnCard = talon.remove(talon.size() - 1);
			drawnCard.faceUp = true;
			waste.add(drawnCard);
		}
	}

	public void displayInitialArrangement() {
		System.out.println("\nWelcome to Klondike Solitaire!");
		System.out.println("\nInitial Arrangement:");

		for (int i = 0; i < numTableau; i++) {
			System.out.print("Pile " + (i + 1) + ": ");
			if (!tableau[i].isEmpty()) {
				for (Card card : tableau[i]) {
					System.out.print(card + ", ");
				}
			} else {
				System.out.print("Empty");
			}
			System.out.println();
		}

		// System.out.print("\nTalon: ");
		if (!talon.isEmpty()) {
			// System.out.println("Top card: ▬");
			System.out.println("\nTalon: " + talon.size() + " cards left");
		} else {
			System.out.println("Talon is empty.");
		}

		System.out.print("\nWaste Pile: ");
		if (!waste.isEmpty()) {
			for (Card card : waste) {
				System.out.print(card + ", ");
			}
			System.out.println();
		} else {
			System.out.println("Waste pile is empty.");
		}

		System.out.println();
	}

	public boolean makeMoves() {
		boolean moved = false;
		boolean canMoveToFoundation = false;

		// check moves within the tableau piles
		for (int i = 0; i < numTableau; i++) {
			if (!tableau[i].isEmpty()) {
				Card currentCard = tableau[i].get(tableau[i].size() - 1);
				if (currentCard.faceUp) {
					if (moveToFoundation(currentCard)) {
						counter = 0;
						tableau[i].remove(currentCard);
						moved = true;
						canMoveToFoundation = true;
						break;
					}
				}
			}
		}

		// Iterate through all cards in the tableau piles
		for (int k = tableau.length - 1; k >= 0; k--) {
			List<Card> tableauPile = tableau[k];
			int lastIndex = tableauPile.size() - 1;
			List<Card> sublist = new ArrayList<>();
			if (!tableauPile.isEmpty()) {

				// Attempt to move the card to another tableau pile
				// Check if there are cards to move
				if (lastIndex >= 0) {
					sublist.add(tableauPile.get(lastIndex)); // Add the top card to the sublist
					// Traverse the cards in reverse order starting from the second top card
					for (int i = lastIndex; i >= 0; i--) {
						Card currentCard = tableauPile.get(i);
						Card previousCard = tableauPile.size() > 1 && i > 0
								? tableauPile.get(i - 1)
								: null; // Get the last card in the sublist
						if (previousCard != null && previousCard.faceUp) {
							// Check if the current card can be added to the sublist
							if (areOppositeColors(currentCard, previousCard)
									&& currentCard.rank == (previousCard.rank - 1)) {
								System.out.println(previousCard + " Added to sublist " + sublist);
								sublist.add(0, previousCard);
								System.out.println("Sublist: " + sublist);
							} else {
								break; // Stop if the current card doesn't meet the conditions
							}
						}
					}
				}
				for (int j = tableau.length - 1; j >= 0; j--) {
					List<Card> destinationPile = tableau[j];
					Card destinationTopCard = destinationPile.isEmpty() ? null
							: destinationPile.get(destinationPile.size() - 1);
					if (destinationTopCard == null) {
						if (sublist.get(0).rank == 13) {
							if (canMoveToDestination(destinationPile, tableauPile, sublist)) {
								destinationPile.addAll(sublist);
								tableauPile.removeAll(sublist);
								counter = 0;
								moved = true;
								break; // Exit the loop after a move is made
							}
						}
					} else {
						if (destinationPile != tableauPile && (areOppositeColors(sublist.get(0), destinationTopCard)
								&& sublist.get(0).rank == (destinationTopCard.rank - 1))) {
							if (canMoveToDestination(destinationPile, tableauPile, sublist)) {
								destinationPile.addAll(sublist);
								tableauPile.removeAll(sublist);
								counter = 0;
								moved = true;
								break; // Exit the loop after a move is made
							}

						}
					}

				}

				if (moved) {
					break; // Exit the outer loop after a move is made
				}

			}
		}

		// check moves from waste pile to tableau or foundations
		if (!waste.isEmpty() && !canMoveToFoundation) {
			Card wasteCard = waste.get(waste.size() - 1);
			if (moveToFoundation(wasteCard)) {
				waste.remove(wasteCard);
				moved = true;
			} else {
				for (int i = 0; i < numTableau; i++) {
					if (!tableau[i].isEmpty()) {
						Card tableauCard = tableau[i].get(tableau[i].size() - 1);
						if (tableauCard.faceUp && isMoveValid(wasteCard, tableauCard)) {
							tableau[i].add(waste.remove(waste.size() - 1));
							moved = true;
							break;
						}
					}
				}
			}
		}

		return moved || canMoveToFoundation;
	}

	private boolean canMoveToDestination(List<Card> destinationPile, List<Card> tableauPile, List<Card> sublist) {
		Card sublistTopCard = sublist.get(0);
		Card destinationTopCard = destinationPile.isEmpty() ? null
							: destinationPile.get(destinationPile.size() - 1);
		if (tableauPile.size() == sublist.size() && sublistTopCard.rank == 13) {
			if (destinationPile.isEmpty()) {
				return false;
			}
		}
		else if (destinationPile.isEmpty() && sublistTopCard.rank == 13){
			return true;
		}
		return ((areOppositeColors(sublist.get(0), destinationTopCard)
				&& sublist.get(0).rank == (destinationTopCard.rank - 1)));
	}

	public boolean moveToFoundation(Card card) {
		for (int i = 0; i < numFoundation; i++) {
			if (foundations[i].isEmpty() && card.rank == 1) {
				// move only the top card to the foundation pile
				foundations[i].add(card);

				// flip the moved card to face-up
				card.faceUp = true;

				// flip the next card from the source tableau (if present)
				int tableauIndex = findCardInTableau(card);
				if (tableauIndex != -1 && !tableau[tableauIndex].isEmpty() && tableau[tableauIndex].size() > 1) {
					Card nextCard = tableau[tableauIndex].get(tableau[tableauIndex].size() - 2);
					nextCard.faceUp = true;
				}

				return true; // break after moving one card
			} else if (!foundations[i].isEmpty()) {
				Card topCard = foundations[i].get(foundations[i].size() - 1);
				if (card.suit == topCard.suit && card.rank == topCard.rank + 1) {
					// move only the top card to the foundation pile
					foundations[i].add(card);

					// flip the moved card to face-up
					card.faceUp = true;

					// flip the next card from the source tableau (if present)
					int tableauIndex = findCardInTableau(card);
					if (tableauIndex != -1 && !tableau[tableauIndex].isEmpty() && tableau[tableauIndex].size() > 1) {
						Card nextCard = tableau[tableauIndex].get(tableau[tableauIndex].size() - 2);
						nextCard.faceUp = true;
					}

					return true; // break after moving one card
				}
			}
		}
		return false;
	}

	public static boolean areOppositeColors(Card card1, Card card2) {
		Suit suit1 = card1.getSuit();
		Suit suit2 = card2.getSuit();

		// Check if the colors are opposite based on ordinal values
		return (suit1.ordinal() % 2 == 0 && suit2.ordinal() % 2 != 0) ||
				(suit1.ordinal() % 2 != 0 && suit2.ordinal() % 2 == 0);
	}

	// method to find the index of a card in tableau
	private int findCardInTableau(Card card) {
		for (int i = 0; i < numTableau; i++) {
			if (tableau[i].contains(card)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isMoveValid(Card currentCard, Card targetCard) {
		return (currentCard.rank == targetCard.rank - 1)// compare the rank
				&& (currentCard.suit.ordinal() % 2 != targetCard.suit.ordinal() % 2); // compare the suit where it
																						// should not have same color

	}

	public boolean isGameOver() {
		for (List<Card> foundation : foundations) {
			if (foundation.size() != 13) {
				return false;
			}
		}
		return true;
	}

	static void displayGameState() {

		System.out.println("=========================================================================");
		// System.out.print("Talon: ");
		if (!talon.isEmpty()) {
			System.out.println("Talon: " + talon.size() + " cards left");
		} else {
			System.out.println("Talon is empty.");
		}

		System.out.print("\nWaste Pile: ");
		if (!waste.isEmpty()) {
			for (Card card : waste) {
				System.out.print(card + ", ");
			}
			System.out.println();
		} else {
			System.out.println("Waste pile is empty.");
		}

		System.out.println("\nFoundations:");
		for (int i = 0; i < numFoundation; i++) {
			System.out.print("Foundation " + (i + 1) + ": ");
			if (!foundations[i].isEmpty()) {
				for (Card card : foundations[i]) {
					System.out.print(card + ", ");
				}
			} else {
				System.out.print("Empty");
			}
			System.out.println();
		}

		System.out.println("\nTableau:");
		for (int i = 0; i < numTableau; i++) {
			System.out.print("Pile " + (i + 1) + ": ");
			if (!tableau[i].isEmpty()) {
				tableau[i].get(tableau[i].size()-1).faceUp = true;
				for (Card card : tableau[i]) {
					System.out.print(card + ", ");
				}
			} else {
				System.out.print("Empty");
			}
			System.out.println();
		}

	}

}