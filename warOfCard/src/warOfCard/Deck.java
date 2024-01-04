package warOfCard;

import java.util.ArrayList;
import java.util.List;

public class Deck {
/**
 * This class is for initializing, shuffling, and dealing of cards to player(s).
 */
	private List<Card> deck;

	public Deck() {
		this.deck = new ArrayList<>();
	}

	public void initializeDeck(List<Card> initialCards) {
		deck.addAll(initialCards);
	}

	//faro in
	public void shuffleCard(int shuffleCount) {
		int shufflenum = 0;
		int halfSize = deck.size() / 2;
		int remainder = deck.size() % 2;
		for (int i = 0; i < shuffleCount; i++) {
			List<Card> topHalf = deck.subList(0, halfSize);
			List<Card> bottomHalf = deck.subList(halfSize, deck.size());

			List<Card> interwovenDeck = new ArrayList<>();
			for (int j = 0; j < halfSize + remainder; j++) {
				if (j < bottomHalf.size()) {
					interwovenDeck.add(bottomHalf.get(j));
				}
				if (j < topHalf.size()) {
					interwovenDeck.add(topHalf.get(j));
				}	
			}
			deck.clear();
			deck = interwovenDeck;
			System.out.println("Shuffled Deck:" + (shufflenum + 1) + deck);
		}
	}

	//deal cards to players
	public void dealCards(List<Player> players) {
		int playerIndex = 0;

		while (!deck.isEmpty()) {
			Card card = deck.remove(0);
			players.get(playerIndex).addCardToHand(card);
			playerIndex = (playerIndex + 1) % players.size();
		}

	}
}
