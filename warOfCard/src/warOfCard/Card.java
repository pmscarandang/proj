package warOfCard;

import java.util.List;

public class Card {
/**
 * This class is for assigning rank and suit of a card and getting the winner of every round
 */
private Suit suit;
private Rank rank;
public Card(Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
}
public Suit getSuit() {
	return suit;
}
public void setSuit(Suit suit) {
	this.suit = suit;
}
public Rank getRank() {
	return rank;
}
public void setRank(Rank rank) {
	this.rank = rank;
}

@Override
public String toString() {
    return "{" + rank.getSymbol() + " of " + suit.getSymbol() + '}';
}

public static Card getRoundWinner(List<Card> cardsPlayed) {
     if (cardsPlayed.isEmpty()) {
         return null;
     }
  
     Card winningCard = cardsPlayed.get(0); // Assume the first card is the initial winner

     for (int i = 1; i < cardsPlayed.size(); i++) {
         Card currentCard = cardsPlayed.get(i); // Get the next card
         if (currentCard.rank.ordinal() > winningCard.rank.ordinal()) { // Compare the rank of current card to the winningCard
            winningCard = currentCard; // Update winning card if a higher-ranked card is found
         }
        else if (currentCard.rank.ordinal() == winningCard.rank.ordinal()){ //If rank is the same
             if (currentCard.suit.ordinal() > winningCard.suit.ordinal()) { // Compare the suit of current card to the winningCard
             winningCard = currentCard; // Update winning card if a higher-ranked card is found
         }
     }
 }
     return winningCard;
 }

}
