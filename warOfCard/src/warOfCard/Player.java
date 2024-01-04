package warOfCard;

import java.util.ArrayList;
import java.util.List;

public class Player {
/**
 * This class is for player hand
 */

	private List<Card> hand;

	//constructor
	public Player() {
		this.hand = new ArrayList<>();
	}

	//getter
	public List<Card> getHand() {
		return hand;
	}
	//adding card to player for dealing
	public void addCardToHand(Card card) {
		hand.add(card);
	}
	//getting the top card of player
	public Card playCard() {
		if (!hand.isEmpty()) {
			return hand.remove(hand.size() - 1);
		}
		return null;
	}
	//getting the size of hand
	public int getHandSize() {
		return hand.size();
	}
	//adding cards to bottom of deck
	public void addCardsToBottom(List<Card> cards) {
		hand.addAll(0, cards);
	}
	//checking if hand in empty
	public boolean isHandEmpty() {
		return hand.isEmpty();
	}
}
