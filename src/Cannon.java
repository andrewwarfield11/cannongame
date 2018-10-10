/**
* Cannon is used to create the cannon in the game
*
* @author  Andrew Warfield
*/
import java.awt.*;

public class Cannon {
	
	private static final int width = 112;
	private static final int height = 64;
	private double xPos;
	private double yPos;
	private Tower tower;
	Sprite sprite;
	//will eventually add more probably
	//hello
	
	/**
	   * This is a constructor for Cannon
	   */
	public Cannon()
	{
		tower = new Tower();
		xPos = tower.getSideOfTower();
		yPos = tower.getTopOfTower() - (height);
		sprite = new Sprite((int)xPos, (int)yPos, width, height, "cannon.png");
	}
	
	/**
	   * This method is used to return the cannon's x position
	   * @return double This is the cannon's x position
	   */
	public double getX()
	{
		return xPos;
	}
	/**
	 * Gets the cannon's height
	 * @return int the cannon's height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Gets the cannon's width
	 * @return int the cannon's width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	   * This method is used to return the cannon's y position
	   * @return double This is the cannon's y position
	   */
	public double getY()
	{
		return yPos;
	}
	
	/**
	   * This method is used to draw the cannon
	   */
	public void draw(Graphics g)
	{
	    //maybe add a little bit more to the visual of the cannon
	     g.setColor(Color.black);
	      
	     //g.fillOval((int)xPos, (int)yPos, width, height);
	     sprite.drawSprite(g);
	      
	      
	}  // end of draw()
}
