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

	public enum Suit {
		HEARTS, SPADES, DIAMONDS, CLUBS // arranged in ordinal for the colors black (spades & clubs) and red (hearts &
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

		Collections.shuffle(deck);
		// displayAllCards();

		// initialize tableau piles
		int tableauCardCount = 0;
		for (int i = 0; i < numTableau; i++) {
			tableau[i] = new ArrayList<>();
			for (int j = 0; j <= i; j++) {
				Card card = deck.remove(0);
				if (j == i) {
					card.faceUp = true;
				}
				tableau[i].add(card);
				tableauCardCount++;
			}
		}

		// remaining cards after tableau initialization are for the talon
		for (int i = tableauCardCount; i < numCards; i++) {
			Card card = deck.remove(0);
			talon.add(card);
		}

		// Draw 3 cards from talon and add to waste pile
		/*
		 * for (int i = 0; i < 3; i++) { if (!talon.isEmpty()) { Card drawnCard =
		 * talon.remove(talon.size() - 1); drawnCard.faceUp = true;
		 * waste.add(drawnCard); } }
		 */

	}

	/*
	 * public void displayAllCards() {
	 * 
	 * for (Card card : deck) { System.out.println(card); } }
	 */

	public void playGame() {
		int maxMoves = 1000;
		int movesCounter = 0;

		while (movesCounter < maxMoves && !isGameOver()) {
			boolean moved = makeMoves();
			movesCounter++;

			if (!moved) {
				drawCard(); // draw a card if no moves are available
			}

			displayGameState();
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

	public void askForShuffle() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Do you want to shuffle the cards before starting the game? (yes/no): ");
		String response = scan.nextLine().toLowerCase();
		if (response.equals("yes")) {
			System.out.println("\nCards shuffling....");
			Collections.shuffle(deck);
		} else if (!response.equals("no")) {
			System.out.println("\nInvalid input. Defaulting to not shuffling.");
		}
	}

	public void drawCard() {
		if (!talon.isEmpty()) {
			Card drawnCard = talon.remove(talon.size() - 1);
			drawnCard.faceUp = true;
			waste.add(drawnCard);
		} else {
			if (!waste.isEmpty()) {
				// move all cards from waste to talon
				while (!waste.isEmpty()) {
					Card card = waste.remove(waste.size() - 1);
					card.faceUp = false; // flip face-up status when moving to talon
					talon.add(card); // add to talon
				}
			}
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
						tableau[i].remove(currentCard);
						moved = true;
						canMoveToFoundation = true;
						break;
					} else if (moveWithinTableau(currentCard, i)) {
						moved = true;
						break;
					}
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
		
		ArrayList<Card> sequencialCards = new ArrayList<Card>();
		
		int emptyTableau = 0;
		
			if (tableau[1].size() == 0) {
				emptyTableau = 1;
			}
			
			else if (tableau[2].size() == 0) {
				emptyTableau = 2;
			}
			else if (tableau[3].size() == 0) {
				emptyTableau = 3;
			}
			else if (tableau[4].size() == 0) {
				emptyTableau = 4;
			}
			else if (tableau[5].size() == 0) {
				emptyTableau = 5;
			}
			else if (tableau[6].size() == 0) {
				emptyTableau = 6;
			}
//			else if (tableau[7].size() == 0) {
//				emptyTableau = 7;
//			}
			
			
			for (int i = 0; i<tableau[1].size(); i++) {
				Card var1 = tableau[1].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[2].size(); i++) {
				Card var1 = tableau[2].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[3].size(); i++) {
				Card var1 = tableau[3].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[4].size(); i++) {
				Card var1 = tableau[4].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[5].size(); i++) {
				Card var1 = tableau[5].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[6].size(); i++) {
				Card var1 = tableau[6].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			for (int i = 0; i<tableau[7].size(); i++) {
				Card var1 = tableau[7].get(i);
				if (var1.rank== 13) {
					sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
				}
			}
			
			
		//	Card var1 = tableau[1].get(emptyTableau);
			//Card var2 = tableau[1].get(emptyTableau);
			//if (var.rank == 13) {
			//	sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
			//}
			//else if (var.rank == 13) {
			//	sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
			//}
			
			//for (int i=0; 0<tableau[numTableau].size(); i++) {
			//	Card var = tableau[numTableau].get(i);
			//	if (var.rank == 13 ) {
				//	sequencialCards.addAll(tableau[emptyTableau].subList(i, tableau[numTableau].size()));
					
					//break;
				//}
					
			//}
		/*
		 * List<Card> sequentialCards = new ArrayList<>(); 
		 * for (int i = 0; i <numTableau; i++) { 
		 * Card tableauCard = tableau[i].get(tableau[i].size() - 1);
		 * sequentialCards.add(tableauCard); }
		 */
		return moved || canMoveToFoundation;
	}

	public boolean moveWithinTableau(Card currentCard, int currentIndex) {
		for (int j = 0; j < numTableau; j++) {
			if (currentIndex != j && !tableau[j].isEmpty()) {
				Card targetCard = tableau[j].get(tableau[j].size() - 1);
				if (targetCard.faceUp && isMoveValid(currentCard, targetCard)) {
					// move only the top card from the source tableau to the target tableau
					Card movedCard = tableau[currentIndex].remove(tableau[currentIndex].size() - 1);
					tableau[j].add(movedCard);

					// check if there's a new top card in the source tableau and flip it face-up
					if (!tableau[currentIndex].isEmpty()) {
						Card newTopCard = tableau[currentIndex].get(tableau[currentIndex].size() - 1);
						newTopCard.faceUp = true;
					}

					// set face-up for the target tableau's top card
					if (!tableau[j].isEmpty()) {
						Card updatedTopCard = tableau[j].get(tableau[j].size() - 1);
						updatedTopCard.faceUp = true;
					}

					// flip the next card in the target tableau if present
					if (!tableau[j].isEmpty() && tableau[j].size() > 1) {
						Card nextCard = tableau[j].get(tableau[j].size() - 2);
						nextCard.faceUp = true;
					}

					return true; // break after moving one card
				}
			}
			// move K on an empty tableau
			if (currentIndex != j && tableau[j].isEmpty() && currentCard.rank == 13) {
				tableau[j].add(currentCard);
				tableau[currentIndex].remove(currentCard);

				if (!tableau[currentIndex].isEmpty()) {
					Card newTopCard = tableau[currentIndex].get(tableau[currentIndex].size() - 1);
					newTopCard.faceUp = true;
				}
			}

		}
		return false;
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
				&& (currentCard.suit.ordinal() % 2 != targetCard.suit.ordinal() % 2); // compare the suit where it should not have same color
																						
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