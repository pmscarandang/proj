package warOfCard;

public enum Suit {
/**
 * This enum if for listing of the suit of a card
 */
	CLUBS("C"), SPADES("S"), HEARTS("H"), DIAMONDS("D");

	private final String symbol;

	Suit(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
}
