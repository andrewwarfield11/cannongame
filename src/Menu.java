import java.awt.Color;
import java.awt.image.*;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Menu {
	private Font menuFont;
	private Font infoFont;
	private Font hsFont;
	private Font endFont;
	private FontMetrics metrics;
	private int state;
	private int score;
	private boolean win;
	private final int MENU = 0;
	private final int INFO = 1;
	private final int HIGHSCORES = 2; // may or may not implement shop in this class
	private final int SHOP = 3;
	private final int ENDSCREEN = 4;

	//rectangles
	private Rectangle playRect;
	private Rectangle infoRect;
	private Rectangle hsRect;
	private Rectangle backRect;
	private Rectangle explosiveRect;
	private Rectangle reloadRect;
	private Rectangle clipRect;
	private Rectangle droneRect;
	private Rectangle airStrikeRect;
	private Rectangle exitRect;

	private boolean firstCall = true;
	//stuff for the shop
	private int explosiveAmmo = 0;
	private int moneys = 0; // all current money values are temporary
	protected static boolean hasExplosive = false;
	private int reloadsBought = 0;
	private int clipsBought = 0;
	private boolean hasDrone;
	private boolean hasAirStrike = false;
	//make a way to deal with people trying to buy something and not having enough money
	

	public Menu() {
		menuFont = new Font("SansSerif", Font.BOLD, 60);
		infoFont = new Font("SansSerif", Font.PLAIN, 20);
		hsFont = new Font("SansSerif", Font.PLAIN, 30);
		endFont = new Font("SansSerif", Font.BOLD, 72);
		state = MENU;
		/*try {
			image = ImageIO.read(getClass().getResource("test.jpg"));
		} catch(IOException e) {e.printStackTrace();}*/
 	}
	/**
	 * Resets values in the menu, mostly relating to the shop, when the game starts over
	 */
	public void reset() {
		score = 0;
		firstCall = true;
		moneys = 0;
		explosiveAmmo = 0;
		hasExplosive = false;
		reloadsBought = 0;
		clipsBought = 0;
	}
	
	/**
	 * Get the amount of money the player has
	 * @return int The amount of money
	 */
	public int getMoney() {
		return moneys;
	}
	
	/**
	 * Increases the amount of shots before the cannon needs to reload. 
	 * Can be bought in the store twice.
	 * @return boolean Whether the player could buy this
	 */
	public boolean boughtClip() {
		if((clipsBought == 0 || clipsBought == 1) && moneys >= 400) {
			moneys -= 400;
			clipsBought++;
			return true;
		}
		return false;
			
	}
	
	/**
	 * buys air strike capability
	 * @return boolean Whether the player was able to successfully buy airstrike
	 */
	public boolean boughtAirStrike() {
		if(!hasAirStrike && moneys >= 700) {
			moneys -= 700;
			hasAirStrike = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Decreases the amount of time spent reloading.
	 * Can be bought in the store twice.
	 * @return boolean Whether the player was able to successfully buy relaod
	 */
	public boolean boughtReload() {
		if((reloadsBought == 0 || reloadsBought == 1) && moneys >= 400) {
			moneys -= 400;
			reloadsBought++;
			return true;
		}
		return false;
	}
	
	/**
	 * Buys the ability to use explosive bullets, and if that has already been bought, it buys the explosive ammo
	 */
	public void boughtExplosive() {
		if(hasExplosive && moneys >= 30) {
			explosiveAmmo++;
			moneys -= 30;
		}
		else if(!hasExplosive && moneys >= 300) {
			hasExplosive = true;
			moneys -= 300;
		}
	}
	
	/**
	 * Buys defense drone if possible
	 * @return boolean If the player could buy the drone
	 */
	public boolean boughtDrone() {
		if( moneys >= 1000) {
			moneys -= 1000;
			hasDrone = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the player has bought explosive shot
	 * @return boolean The flag keeping track of if explosive shot has been bought
	 */
	public boolean hasBoughtExplosive() {
		return hasExplosive;
	}
	
	/**
	 * Gets the amount of explosive ammo
	 * @return int The amount of explosive ammo currently owned.
	 */
	public int getExplosiveAmmo() {
		return explosiveAmmo;
	}
	
	/**
	 * Called when an explosive shot is fired, decrements the amount of explosive ammo by one
	 */
	public void shotExplosive() {
		explosiveAmmo -= 1;
	}

	/**
	 * Increases the score.
	 * @param s The amount that the score is increased by.
	 */
	public void addToScore(int s) {
		score += s;
	}
	
	/**
	 * Called when the player loses the game, sets a boolean flag to false
	 */
	public void lost() {
		win = false;
	}

	/**
	 * Called when the player wins the game, sets a boolean flag to true
	 */
	public void won() {
		win = true;
	}

	/**
	 * Adds money, used in the shop between levels
	 * @param m The amount of money to be used.
	 */
	public void addMoneys(int m) {
		moneys += m;
	}

	/**
	 * Gets the rectangle for the play button in the menu
	 * @return Rectangle The play rectangle
	 */
	public Rectangle getPlayRect() {
		return playRect;
	}
	
	/**
	 * Gets the rectangle for the info button in the menu
	 * @return Rectangle The info rectangle
	 */
	public Rectangle getInfoRect() {
		return infoRect;
	}

	/**
	 * Gets the rectangle for the back button in the info and high score screens.
	 * Also used for the next level button in the shop and the play again button in the end screen
	 * @return Rectangle The back rectangle
	 */
	public Rectangle getBackRect() {
		return backRect;
	}
	
	public Rectangle getAirStrikeRect() {
		return airStrikeRect;
	}

	/**
	 * Gets the rectangle for the high scores button in the menu
	 * @return Rectangle The high scores rectangle
	 */
	public Rectangle getHSRect() {
		return hsRect;
	}

	/**
	 * Gets the rectangle for the lower reload time choice in the shop
	 * @return Rectangle The reload rectangle
	 */
	public Rectangle getReloadRect() {
		return reloadRect;
	}

	/**
	 * Get the rectangle for the explosive ammo choice in the shop
	 * @return Rectangle The explosive rectangle
	 */
	public Rectangle getExplosiveRect() {
		return explosiveRect;
	}
	
	/**
	 * Get the rectangle for the defense drone choice in the shop
	 * @return Rectangle The drone rectangle
	 */
	public Rectangle getDroneRect() {
		return droneRect;
	}
	
	/**
	 * Get rectangle for exit button
	 * @return rectangle the exit rectangle
	 */
	public Rectangle getExitRect() {
		return exitRect;
	}

	/**
	 * Gets the rectangle for the increasing clip size option in the shop.
	 * @return Rectangle The Clip Rectangle
	 */
	public Rectangle getClipRect() {
		return clipRect;
	}

	/**
	 * Used to determine if the menu is currently in menu state
	 * @return boolean True if the state is menu and false otherwise
	 */
	public boolean isMenuState() {
		if(state == MENU)
			return true;
		else
			return false;
	}

	/**
	 * Used to determine if the menu is currently in high scores state
	 * @return boolean True if the state is high scores and false otherwise.
	 */
	public boolean isHighScores() {
		if(state == HIGHSCORES) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Used to determine if the menu is currently in shop state
	 * @return boolean True if the state is shop and false otherwise
	 */
	public boolean isShop() {
		if(state == SHOP)
			return true;
		else
			return false;
	}

	/**
	 * Used to determine if the menu is currently in info state
	 * @return boolean True if the state is info and false otherwise
	 */
	public boolean isInfoState() {
		if(state == INFO ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Used to determine if the menu is currently in end screen state
	 * @return boolean True if the state is end screen and false otherwise
	 */
	public boolean isEndScreen() {
		if(state == ENDSCREEN ) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Changes the menu to info state
	 */
	public void changeToInfo() {
		state = INFO;
	}

	/**
	 * Changes the menu to high score state
	 */
	public void changeToHS() {
		state = HIGHSCORES;
	}

	/**
	 * Changes the menu to end screen state
	 */
	public void changeToEndScreen() {
		state = ENDSCREEN;
	}

	/**
	 * Changes the menu to shop state
	 */
	public void changeToShop() {
		state = SHOP;
	}

	/**
	 * Changes the menu to generic menu state
	 */
	public void changeToMenu() {
		state = MENU;
	}

	/**
	 * Gets the current state of the menu
	 * @return int The current state
	 */
	public int getState() {
		return state;
	}

	private void setRects() {
		playRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("PLAY")/2, GameFrame.pHeight/4 - metrics.getHeight(), 
				metrics.stringWidth("PLAY"), metrics.getHeight());
		infoRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("INFO")/2, GameFrame.pHeight/2 - metrics.getHeight(), //drawing info rect 
				metrics.stringWidth("INFO"), metrics.getHeight());
		hsRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("HIGH SCORE")/2, 3*GameFrame.pHeight/4 - metrics.getHeight(), //drawing info rect 
				metrics.stringWidth("HIGH SCORE"), metrics.getHeight());
		exitRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("EXIT")/2, hsRect.y + 2*metrics.getHeight(),
				metrics.stringWidth("EXIT"), metrics.getHeight());
		firstCall = false;
	}


	private void setBackRect() {
		backRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("BACK")/2, 3*GameFrame.pHeight/4 - metrics.getHeight(),
				metrics.stringWidth("BACK"), metrics.getHeight());
	}

	/**
	 * Draws the menu
	 * @param g The Graphics object used to draw
	 */
	public void draw(Graphics g) {
		//g.drawImage(image, 0, 0, this);
		if(state == MENU) {
			g.setFont(menuFont);
			metrics = g.getFontMetrics(g.getFont());

			g.setColor(Color.black);
			if(firstCall)
				setRects();
			g.drawString("MENU", GameFrame.pWidth/2 - metrics.stringWidth("MENU")/2, GameFrame.pHeight/10);
			g.drawString("PLAY", GameFrame.pWidth/2 - metrics.stringWidth("PLAY")/2, GameFrame.pHeight/4);
			g.drawString("INFO", GameFrame.pWidth/2 - metrics.stringWidth("INFO")/2, GameFrame.pHeight/2);
			g.drawString("HIGH SCORES", GameFrame.pWidth/2 - metrics.stringWidth("HIGH SCORES")/2, 3*GameFrame.pHeight/4);
			g.drawString("EXIT", GameFrame.pWidth/2 - metrics.stringWidth("EXIT")/2, exitRect.y + metrics.getHeight());
		}
		else if(state == INFO) {
			drawInfo(g);
		}
		else if(state == HIGHSCORES)
			drawHS(g);
		else if(state == SHOP)
			drawShop(g);
		else if(state == ENDSCREEN)
			drawEndScreen(g);

	}

	private void drawInfo(Graphics g) {
		//System.out.println("Entering drawinfo");
		String s = "You control a cannon on top of a tower, trying to protect the tower from enemies attacking it. "
				+ "There are both flying enemies trying to shoot the tower and enemies on the ground trying to walk and attack it. "
				+ "You can shoot and kill both of them along with the bullets shot by the flying " 
				+ "enemies. You have to reload for a short time after a certain amount of shots."
				+ "Left click to shoot the cannon, and once you buy the capabilites, you can middle " 
				+ "click to swap to explosive ammo or right click to call an airstrike in an area once per level.";
		g.setFont(infoFont);
		metrics = g.getFontMetrics(g.getFont());
		g.setColor(Color.black);
		int x = GameFrame.pWidth/5;
		int y = GameFrame.pHeight/5;
		boolean bool = false; // marker for starting a new line
		//g.drawString(s, x, y);

		for(int i = 0; i < s.length(); i++) {
			g.drawString(s.substring(i, i+1), x, y);
			x += metrics.charWidth(s.charAt(i));
			if(x >= (3*GameFrame.pWidth/4))
				bool = true;
			if(bool && s.charAt(i) == ' ') {
				y+= metrics.getHeight();
				bool = false;
				x = GameFrame.pWidth/5;
			}
		}
		g.setFont(infoFont);
		metrics = g.getFontMetrics(g.getFont());
		g.setColor(Color.black);
		//make an actual string variable
		//g.drawString("Controls and whatnot", GameFrame.pWidth/2 - metrics.stringWidth("Controls and whatnot"), GameFrame.pHeight/2);

		setBackRect();
		g.drawString("BACK", GameFrame.pWidth/2 - metrics.stringWidth("BACK")/2, 3*GameFrame.pHeight/4);
	}

	private void drawHS(Graphics g) {
		//System.out.println("Entering drawhs");

		g.setFont(hsFont);
		metrics = g.getFontMetrics(g.getFont());
		g.setColor(Color.black);

		g.drawString("Implement later", GameFrame.pWidth/2 - metrics.stringWidth("Implement later"), GameFrame.pHeight/2);

		setBackRect();
		g.drawString("BACK", GameFrame.pWidth/2 - metrics.stringWidth("BACK")/2, 3*GameFrame.pHeight/4);
	}

	private void makeShopRects() {
		if(!hasExplosive) {
			explosiveRect = new Rectangle(GameFrame.pWidth/4, GameFrame.pHeight/5 - metrics.getHeight(), 
					metrics.stringWidth("Explosive Shot"), metrics.getHeight());
		}
		else {
			explosiveRect = new Rectangle(GameFrame.pWidth/4, GameFrame.pHeight/5 - metrics.getHeight(), 
					metrics.stringWidth("Explosive Ammo"), metrics.getHeight());
		}
		backRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("Next Level")/2, 4*GameFrame.pHeight/5 - metrics.getHeight(),
				metrics.stringWidth("Next Level"), metrics.getHeight());

		reloadRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("Lower Reload Time")/2, GameFrame.pHeight/5 - metrics.getHeight(), 
				metrics.stringWidth("Lower Reload Time"), metrics.getHeight());
		clipRect = new Rectangle(3*GameFrame.pWidth/4 - metrics.stringWidth("Larger Clip Size")/2, GameFrame.pHeight/5 - metrics.getHeight(), 
				metrics.stringWidth("Larger Clip Size"), metrics.getHeight());
		
		droneRect = new Rectangle(GameFrame.pWidth/4, 2*GameFrame.pHeight/5 - metrics.getHeight(),
				metrics.stringWidth("Defense Drone"), metrics.getHeight());
		
		airStrikeRect = new Rectangle(3*GameFrame.pWidth/4 - metrics.stringWidth("Air Strike")/2, 2*GameFrame.pHeight/5 - metrics.getHeight(),
				metrics.stringWidth("Air Strike"), metrics.getHeight());
	}

	private void drawShop(Graphics g) {
		g.setColor(Color.black);
		g.setFont(infoFont);
		metrics = g.getFontMetrics(g.getFont());
		makeShopRects();
		String q;
		//g.drawRect(explosiveRect.x, explosiveRect.y, explosiveRect.width, explosiveRect.height);
		g.drawString("SHOP", GameFrame.pWidth/2, GameFrame.pHeight/20);
		g.drawString("Money: " + moneys, GameFrame.pWidth/20, GameFrame.pHeight/20);

		//buying explosive shot/ammo
		if(hasExplosive) {
			g.drawString("Explosive Ammo", GameFrame.pWidth/4, GameFrame.pHeight/5);
			g.drawString("Owned: " + explosiveAmmo, GameFrame.pWidth/4, GameFrame.pHeight/5 + metrics.getHeight());
			g.drawString("Cost: 30", GameFrame.pWidth/4 , GameFrame.pHeight/5 - metrics.getHeight());

		}
		else {
			g.drawString("Explosive Shot", GameFrame.pWidth/4, GameFrame.pHeight/5);
			g.drawString("Cost: 300", GameFrame.pWidth/4 , GameFrame.pHeight/5 - metrics.getHeight());

		}
		
		//drawing defense drone option
		if(!hasDrone) {
			//g.drawRect(droneRect.x, droneRect.y, droneRect.width, droneRect.height);
			g.drawString("Defense Drone", droneRect.x, droneRect.y + metrics.getHeight());
			g.drawString("Cost: 1000", droneRect.x, droneRect.y);
		}
		else {
			//g.drawRect(droneRect.x, droneRect.y, droneRect.width, droneRect.height);
			g.drawString("Sold out", droneRect.x, droneRect.y + metrics.getHeight());
		}
		
		//drawing air strike option
		if(!hasAirStrike) {
			//g.drawRect(airStrikeRect.x, airStrikeRect.y, airStrikeRect.width, airStrikeRect.height);
			g.drawString("Air Strike", airStrikeRect.x, airStrikeRect.y + metrics.getHeight());
			g.drawString("Cost: 700", airStrikeRect.x, airStrikeRect.y);
		}
		else {
			//g.drawRect(airStrikeRect.x, airStrikeRect.y, airStrikeRect.width, airStrikeRect.height);
			g.drawString("Sold out", airStrikeRect.x, airStrikeRect.y + metrics.getHeight());
		}
		//drawing the next level
		//g.drawRect(backRect.x, backRect.y, backRect.width, backRect.height);
		g.drawString("Next Level", backRect.x, backRect.y + metrics.getHeight());
		
		//buying lower reload time
		if(reloadsBought < 2) {
			//g.drawRect(reloadRect.x, reloadRect.y, reloadRect.width, reloadRect.height);
			g.drawString("Cost: 400", reloadRect.x , reloadRect.y);
			g.drawString("Lower Reload Time", reloadRect.x , reloadRect.y + metrics.getHeight());
			q = "Quantity: " + (2-reloadsBought);
			g.drawString(q, reloadRect.x, reloadRect.y+2*metrics.getHeight());
		}
		else { 
			//g.drawRect(reloadRect.x, reloadRect.y, reloadRect.width, reloadRect.height);
			g.drawString("Sold Out", reloadRect.x , reloadRect.y + metrics.getHeight());
		}
		
		//buying increased clip size
		if(clipsBought < 2) {
			//g.drawRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
			g.drawString("Larger Clip Size", clipRect.x, clipRect.y + metrics.getHeight());
			g.drawString("Cost: 400", clipRect.x , clipRect.y);
			q = "Quantity: " + (2-clipsBought);
			g.drawString(q, clipRect.x, clipRect.y+2*metrics.getHeight());
		}
		else { 
			//g.drawRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
			g.drawString("Sold Out", clipRect.x, clipRect.y + metrics.getHeight());
		}

	}

	private void drawEndScreen(Graphics g) {
		g.setColor(Color.black);
		g.setFont(endFont);
		metrics = g.getFontMetrics(g.getFont());
		if(!win) {
			g.drawString("Game Over", GameFrame.pWidth/2 - metrics.stringWidth("Game Over")/2, GameFrame.pHeight/4);
			g.drawString("Your Score: " + score, GameFrame.pWidth/2 - metrics.stringWidth("Your Score: " + score)/2, GameFrame.pHeight/2);
		}
		else {
			g.drawString("You win!", GameFrame.pWidth/2 - metrics.stringWidth("You win!")/2, GameFrame.pHeight/4);
			g.drawString("Your Score: " + score, GameFrame.pWidth/2 - metrics.stringWidth("Your Score: " + score)/2, GameFrame.pHeight/2);
		}
		backRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("Play Again")/2, GameFrame.pHeight/2 + metrics.getHeight(),
				metrics.stringWidth("Play Again"), metrics.getHeight());
		exitRect = new Rectangle(GameFrame.pWidth/2 - metrics.stringWidth("EXIT")/2, GameFrame.pHeight/2 + 3*metrics.getHeight(),
				metrics.stringWidth("EXIT"), metrics.getHeight());
		g.drawString("Play Again", backRect.x, backRect.y + metrics.getHeight());
		g.drawString("EXIT", exitRect.x, exitRect.y + metrics.getHeight());
		//g.drawRect(exitRect.x, exitRect.y, exitRect.width, exitRect.height);
		//g.drawRect(backRect.x, backRect.y, backRect.width, backRect.height);
	}
}
