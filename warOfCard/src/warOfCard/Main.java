package warOfCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
/**
 * This is the main class of the war card game
 * @author pmcarandang
 * 
 */
public class Main {

	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) throws FileNotFoundException, IOException {

		String filePath = "C:\\Users\\pmcarandang\\git\\repository\\warOfCard\\src\\warOfCard\\input.txt";
		List<Card> initialCards = initialCardS(filePath);
		System.out.println("Initial Deck of Cards: " + initialCards);
		int numberOfPlayers = getNumberOfPlayers(initialCards);
		Game game = new Game(numberOfPlayers);
		int shuffleCount = getShuffleCount();
		game.showCard(initialCards, shuffleCount);

	}

	public static List<Card> initialCardS(String filePath) throws FileNotFoundException, IOException {
/**
 * This method uses string tokenizer in parsing the suit and rank of the cards
 */

		List<Card> listOfCards = new ArrayList<>();

		File file = new File(filePath);
		try (BufferedReader myReader = new BufferedReader(new FileReader(file))) {

			String line;
			while ((line = myReader.readLine()) != null) {

				StringTokenizer parts = new StringTokenizer(line, ",");

				while (parts.hasMoreTokens()) {
					String cardValues = parts.nextToken().trim();
					String[] cardVal = cardValues.split("-");

					if (cardVal.length == 2) {
						Suit suit = null;
						Rank rank = null;

						// Parsing the suit
						if ("D".equals(cardVal[0])) {
							suit = Suit.DIAMONDS;
						} else if ("H".equals(cardVal[0])) {
							suit = Suit.HEARTS;
						} else if ("S".equals(cardVal[0])) {
							suit = Suit.SPADES;
						} else if ("C".equals(cardVal[0])) {
							suit = Suit.CLUBS;
						}

						// Parsing the rank
						if ("A".equals(cardVal[1])) {
							rank = Rank.ACE;
						} else if ("K".equals(cardVal[1])) {
							rank = Rank.KING;
						} else if ("Q".equals(cardVal[1])) {
							rank = Rank.QUEEN;
						} else if ("J".equals(cardVal[1])) {
							rank = Rank.JACK;
						} else if ("10".equals(cardVal[1])) {
							rank = Rank.TEN;
						} else if ("9".equals(cardVal[1])) {
							rank = Rank.NINE;
						} else if ("8".equals(cardVal[1])) {
							rank = Rank.EIGHT;
						} else if ("7".equals(cardVal[1])) {
							rank = Rank.SEVEN;
						} else if ("6".equals(cardVal[1])) {
							rank = Rank.SIX;
						} else if ("5".equals(cardVal[1])) {
							rank = Rank.FIVE;
						} else if ("4".equals(cardVal[1])) {
							rank = Rank.FOUR;
						} else if ("3".equals(cardVal[1])) {
							rank = Rank.THREE;
						} else if ("2".equals(cardVal[1])) {
							rank = Rank.TWO;
						}

						if (suit != null && rank != null) {

							listOfCards.add(new Card(suit, rank));
						}
					}
				}
			}
		}
		return listOfCards;
	}

	
	public static int getShuffleCount() {
/**
 * This method gets the number of faro shuffle count from the player
 */
		int shuffleCount = 0;
		boolean isValidShuffle;

		do {
			isValidShuffle = true;
			try {
				System.out.print("\nEnter number of shuffles: ");
				shuffleCount = Integer.parseInt(scan.nextLine().trim());

				if (shuffleCount >= 0) {
					System.out.println("The deck will be shuffled " + shuffleCount + " times.");
					System.out.println("---------------------------------------------------------------------");
				} else {
					System.out.println("Invalid Input! Please enter a non-negative integer.");
					isValidShuffle = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input! Please enter a non-negative integer.");
				isValidShuffle = false;
			}
		} while (!isValidShuffle);

		return shuffleCount;
	}

	
	public static int getNumberOfPlayers(List<Card> initalCards) {
/**
 * This method gets the number of players
 */
		int numberOfPlayers = 0;
		boolean isValidPlayers;

		do {
			isValidPlayers = true;
			try {
				System.out.print("\nEnter number of players (1-" + initalCards.size() + "): ");
				numberOfPlayers = Integer.parseInt(scan.nextLine().trim());

				if (numberOfPlayers > 0 && numberOfPlayers <= initalCards.size()) {
					System.out.println(numberOfPlayers + " Player(s) entered the game.");
				} else {
					System.out.println("Invalid Input! Please enter an integer between 1-" + initalCards.size() + ".");
					isValidPlayers = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input! Please enter an integer between 1-" + initalCards.size() + ".");
				isValidPlayers = false;
			}
		} while (!isValidPlayers);

		return numberOfPlayers;
	}
}
