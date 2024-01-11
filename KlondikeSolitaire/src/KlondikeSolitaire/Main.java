package KlondikeSolitaire;
/**
 * This is the main class of the klondike solitaire
 * @author pmcarandang
 * 
 */
public class Main {
	public static void main(String[] args) {
		
		Game start = new Game();
		start.initialize(); // initialize the foundations, tableau, talon, and waste pile
		start.askForShuffle(); // method to ask player for shuffling the card before starting the game
		start.displayInitialArrangement(); // display initial arrangement before starting the game
		start.playGame(); // method for the game proper
    }
}
