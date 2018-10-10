/**
* Tower creates the tower used in the game.
*
* @author  Andrew Warfield
*/
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class Tower {
	protected static final int HEIGHT = 600;
	protected static final int WIDTH = 200;
	protected static final Point2D.Double middleOfTower = new Point2D.Double(GameFrame.pWidth * 0.875, (HEIGHT - Ground.getTopOfGround())/2);
	private static final int maxHealth = 1000; // something wrong with the health. FIX
	private static int health = maxHealth;
	private Rectangle rect;
	private int xLoc;
	private int yLoc;
	private Random rand;
	Sprite towerSprite;
	//will probably do something with the tower's health
	
	/**
	   * This is a constructor for Tower
	   */
	public Tower() {
		xLoc = (int)(GameFrame.pWidth * 0.8);
		yLoc = (int)GameFrame.pHeight - (int)HEIGHT;
		rect = new Rectangle(xLoc, yLoc, (int)WIDTH, (int)HEIGHT);
		rand = new Random();
		towerSprite = new Sprite(xLoc, yLoc, WIDTH, HEIGHT, "tower.png");
	}
	
	/**
	   * This method is used to get the rectangle representing the tower
	   * @return Rectangle Returns the rectangle representing the tower
	   */
	public Rectangle getRect() {
		return rect;
	}
	
	/**
	 * kills the tower, ends the game
	 */
	public void kill() {
		health = 0;
	}
	
	/**
	 * gets the tower's max health
	 * @return int the max health
	 */
	public int getMaxHealth() {
		return maxHealth;
	}
	
	/**
	 * Gives the tower back its maximum amount of health
	 */
	public void resetHealth() {
		health = maxHealth;
	}
	
	/**
	 * get the height
	 * @return double the height
	 */
	public double getHeight() {
		return HEIGHT;
	}
	
	/**
	 * get tower width
	 * @return double the tower width
	 */
	public double getWidth() {
		return WIDTH;
	}
	/**
	   * This method is used to get a point within the tower
	   * @return Point2D.Double Returns a random point within the tower
	   */
	public Point2D.Double getPointOnTower() { //calculating where the flying guys are aiming
		double x = getSideOfTower();
		double y = rand.nextInt((int)HEIGHT) + yLoc;
		while(y > GameFrame.pHeight - Ground.HEIGHT - HEIGHT / 8)
			y = rand.nextInt((int)HEIGHT) + yLoc; //making sure y is an acceptable value

		return new Point2D.Double(x, y);
	}
	
	/**
	   * This method is used to get the y Location of the top of the tower
	   * @return double Returns the y location of the top of the tower
	   */
	public double getTopOfTower() {
		return GameFrame.pHeight - HEIGHT;
	}
	
	/**
	   * This method is used to get the tower's current health
	   * @return int Returns the tower's current health
	   */
	public int getHealth() {
		return health;
	}
	
	/**
	   * This method is used to get the tower's current health
	   * @return String Returns the tower's current health
	   */
	public String getHealthString() {
		String h = "" + health;
		return h;
	}
	
	/**
	   * This method is used to lower the tower's health when it is attacked by a guy
	   */
	public void isAttackedByGuy() {
		health -= 40;
	}
	
	/**
	   * This method is used to lower the tower's health when it is attacked by a FlyingGuy
	   */
	public void isAttackedByFlyingGuy() {
		health -= 20;
	}
	
	/**
	   * This method is used to determine if the tower is dead
	   * @return boolean True if the tower is dead, false if not
	   */
	public boolean isDead() {
		if(health <= 0)
			return true;
		else 
			return false;
	}
	
	/**
	   * This method is used to get the x location of the left side of the tower
	   * @return double The x location of the left side of the tower
	   */
	public double getSideOfTower() {
		return GameFrame.pWidth * 0.8;
	}
	
	/**
	   * This method is used to determine when a guy reaches the tower
	   * @return boolean True if the guy is at the tower, false if not
	   */
	public boolean guyIntersects(Guys guy) {
		if( guy.getXLoc() > GameFrame.pWidth * 0.8 - guy.getWidth() - 5) // I don't want the guy to overlap with the tower
			return true;
		else
			return false;
	}
	
	/**
	   * This method is used to tell if you tried to shoot the tower
	   * @return boolean Returns true if the player tried to shoot the tower, false if not
	   */
	public boolean triedToShootTheTower(Point2D.Double click) {
		if(click.getX() >= getSideOfTower())
			return true;
		else 
			return false;
	}
	
	/**
	   * This method is used to draw the tower
	   * @param the graphics used to draw it
	   */
	public void draw(Graphics g)
	{
	    
	     //g.setColor(Color.blue);
	     towerSprite.drawSprite(g);
	    // g.fillRect(xLoc, yLoc, (int)WIDTH, (int)HEIGHT );

	}  // end of draw()

}
