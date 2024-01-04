package warOfCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
/**
 * This class is for the game proper
 */
	private Deck deck;
	int numberOfRound = 1;
	private List<Player> players;

	public Game(int numberOfPlayers) {
		this.deck = new Deck();
		this.players = new ArrayList<>();
		for (int i = 0; i < numberOfPlayers; i++) {
			players.add(new Player());
		}
	}
	//displaying of cards of player
	public void showCard(List<Card> initialCards, int shuffleCount) {
		deck.initializeDeck(initialCards);
		deck.shuffleCard(shuffleCount);
		deck.dealCards(players);

		System.out.println("---------------------------------------------------------------------");
		for (Player player : players) {

			System.out.println("Player hand" + player.getHand());
		}
		playGame(initialCards);
	}
	// start of the game
	public void playGame(List<Card> initialCards) {
		System.out.println("\n" + "======================LET THE WAR OF CARDS BEGIN=====================");
		boolean gameFinished = false;

		while (!gameFinished) {
			List<Card> cardsPlayed = new ArrayList<>();

			System.out.println("============================== ROUND " + numberOfRound + " ==============================");
			for (Player player : players) {
				System.out.println("Player " + (players.indexOf(player) + 1) + " hand: " + player.getHand());
				Card playedCard = player.playCard();
				cardsPlayed.add(playedCard);
				System.out.println("Player " + (players.indexOf(player) + 1) + " played: " + playedCard);
			}
			roundWinner(cardsPlayed);
			numberOfRound++;

			// check if any player has all the cards
			for (Player player : players) {
				if (player.getHandSize() == initialCards.size()) {
					System.out.print("========================================================================" + "\n");
					System.out.println("We have a WINNER, a player has all the cards !!");
					System.out.println("HAND: " + player.getHand());
					gameFinished = true;
					break;
				}
			}
		}
		displayWinner();
	}
	
	// determine the winner of the round
	private void roundWinner(List<Card> cardsPlayed) {
		// Filter out null cards
		List<Card> validCardsPlayed = cardsPlayed.stream().filter(card -> card != null).collect(Collectors.toList());

		if (!validCardsPlayed.isEmpty()) {
			Card roundWinner = Card.getRoundWinner(validCardsPlayed);
			System.out.println("");
			System.out.println("CARDS PLAYED: " + validCardsPlayed);
			System.out.println("");
			System.out.println("WINNING CARD: " + roundWinner);
			System.out.println("");

			if (roundWinner != null) {
				int indexOfWinner = cardsPlayed.indexOf(roundWinner);
				Player winningPlayer = players.get(indexOfWinner);
				System.out.println("Round " + numberOfRound + " Winner: Player " + (indexOfWinner + 1));

				// Puts winning card on top
				validCardsPlayed.remove(roundWinner);
				// Collections.reverse(validCardsPlayed);
				validCardsPlayed.add(roundWinner);

				// Giving the cards to the winner
				winningPlayer.addCardsToBottom(validCardsPlayed);

				System.out.println("Player " + (indexOfWinner + 1) + " hand: " + winningPlayer.getHand());
			}
		} else {
			System.out.println("No valid cards played.");
		}
	}
	// display the winner of the game
	private void displayWinner() {
		Player winner = players.stream().filter(player -> !player.isHandEmpty()).findFirst().orElse(null);
		int index = players.indexOf(winner);
		System.out.println("========================================================================");
		System.out.println("Player " + (index + 1) + " wins the game!");
		System.out.println("CONGRATULATIONS!");
		System.out.println("========================================================================");
	}

}