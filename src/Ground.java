/**
* Ground is used to create the ground in the game
*
* @author  Andrew Warfield
*/
import java.awt.*;
public class Ground {
	
	protected static final int HEIGHT = 40;
	protected final int WIDTH = GameFrame.pWidth;
	private Sprite sprite;
	
	/**
	   * This is a constructor for Ground
	   */
	public Ground() {
		sprite = new Sprite(0,getTopOfGround(),WIDTH,HEIGHT, "ground.png");
	}
	
	/**
	   * This method is used to return x position of the top of the ground
	   * @return the y position of the top of the ground
	   */
	public static int getTopOfGround() {
		return (GameFrame.pHeight - HEIGHT);
	}
	
	/**
	 * Gets the rectangle representing the ground
	 * @return Rectangle the rectangle representing the ground
	 */
	public static Rectangle getRect() {
		return new Rectangle(0, getTopOfGround(), GameFrame.pWidth, GameFrame.pHeight);
	}
	
	/**
	   * This method is used to draw the ground
	   */
	public void draw(Graphics g) {
	    
	     g.setColor(Color.green);
	     
	     //g.fillRect(0, getTopOfGround(), (int)width, (int)height );
	     int x = -sprite.getWidth();
	     sprite.drawSprite(g);
	     while (x < (GameFrame.pWidth - sprite.getWidth())) {
	    	 sprite.drawSprite(g);
	    	 x+= sprite.getWidth();
	    	 sprite.setPosition(x, sprite.getYPosn());
	     }
	      
	      
	}  // end of draw()
}
