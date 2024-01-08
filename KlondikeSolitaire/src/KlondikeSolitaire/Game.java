package KlondikeSolitaire;

import java.util.*;

public class Game {
	public static final int NUM_FOUNDATIONS = 4;
	public static final int NUM_TABLEAU_PILES = 7;
	public static final int NUM_CARDS = 52;

	public static List<Card> deck;
	public static List<Card>[] foundations;
	public static List<Card>[] tableau;

	public static List<Card> talon;
	public static List<Card> waste;

	public enum Suit {
		SPADES, HEARTS, DIAMONDS, CLUBS
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
			if (faceUp) {
				String[] ranks = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
				return ranks[rank - 1] + " of " + suit;
			} else {
				return "▬";
			}
		}
	}

	public void initialize() {
		deck = new ArrayList<>();
		foundations = new ArrayList[NUM_FOUNDATIONS];
		tableau = new ArrayList[NUM_TABLEAU_PILES];
		talon = new ArrayList<>();
		waste = new ArrayList<>();

		for (int i = 0; i < NUM_FOUNDATIONS; i++) {
			foundations[i] = new ArrayList<>();
		}

		for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
			tableau[i] = new ArrayList<>();
		}

		for (int i = 0; i < NUM_CARDS; i++) {
			Card card = new Card(Suit.values()[i / 13], (i % 13) + 1, false);
			deck.add(card);
		}

		Collections.shuffle(deck);

		// Create tableau and talon
		int tableauCardCount = 0;
		for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
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

		// The remaining cards after tableau initialization are for the talon
		for (int i = tableauCardCount; i < NUM_CARDS; i++) {
			Card card = deck.remove(0);
			talon.add(card);
		}
	}

	public void playGame() {
		int maxMoves = 1000;
		int movesCounter = 0;

		while (movesCounter < maxMoves && !isGameOver()) {
			boolean moved = makeMoves();
			movesCounter++;

			if (!moved) {
				drawCard(); // Draw a card if no moves are available
			}

			displayGameState();
		}

		if (isGameOver()) {
			System.out.println("\n=========================");
			System.out.println("Congratulations! You won!");
			System.out.println("=========================");
		} else {
			System.out.println("\n==================================");
			System.out.println("No more possible moves. Game over!");
			System.out.println("==================================");
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
				Collections.shuffle(waste);
				talon.addAll(waste);
				waste.clear();
				drawCard();
			}
		}
	}

	public void displayInitialArrangement() {
		System.out.println("\nWelcome to Klondike Solitaire!");
		System.out.println("\nInitial Arrangement:");

		for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
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

		System.out.print("\nTalon: ");
		if (!talon.isEmpty()) {
			System.out.println("Top card: ▬");
			System.out.println("Number of cards left in talon: " + talon.size());
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

		// Check moves within the tableau piles
		for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
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

		// Check moves from waste pile to tableau or foundations
		if (!waste.isEmpty() && !canMoveToFoundation) {
			Card wasteCard = waste.get(waste.size() - 1);
			if (moveToFoundation(wasteCard)) {
				waste.remove(wasteCard);
				moved = true;
			} else {
				for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
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

	public boolean moveWithinTableau(Card currentCard, int currentIndex) {
		for (int j = 0; j < NUM_TABLEAU_PILES; j++) {
			if (currentIndex != j && !tableau[j].isEmpty()) {
				Card targetCard = tableau[j].get(tableau[j].size() - 1);
				if (targetCard.faceUp && isMoveValid(currentCard, targetCard)) {
					List<Card> cardsToMove = new ArrayList<>();
					for (int k = tableau[currentIndex].size() - 1; k >= 0; k--) {
						cardsToMove.add(tableau[currentIndex].remove(k));
						if (tableau[currentIndex].isEmpty()
								|| tableau[currentIndex].get(tableau[currentIndex].size() - 1).faceUp)
							break;
					}
					tableau[j].addAll(cardsToMove);
					return true;
				}
			}
		}
		return false;
	}

	public boolean moveToFoundation(Card card) {
		for (int i = 0; i < NUM_FOUNDATIONS; i++) {
			if (foundations[i].isEmpty() && card.rank == 1) {
				foundations[i].add(card);
				return true;
			} else if (!foundations[i].isEmpty()) {
				Card topCard = foundations[i].get(foundations[i].size() - 1);
				if (card.suit == topCard.suit && card.rank == topCard.rank + 1) {
					foundations[i].add(card);
					return true;
				}
			}
		}
		return false;
	}

	public boolean isMoveValid(Card currentCard, Card targetCard) {
		return (currentCard.rank == targetCard.rank - 1)
				&& (currentCard.suit.ordinal() % 2 != targetCard.suit.ordinal() % 2);
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
		System.out.println("Foundations:");
		for (int i = 0; i < NUM_FOUNDATIONS; i++) {
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
		for (int i = 0; i < NUM_TABLEAU_PILES; i++) {
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

		System.out.print("\nTalon: ");
		if (!talon.isEmpty()) {
			System.out.println("Top card: ▬");
			System.out.println("Number of cards left in talon: " + talon.size());
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
	}

}