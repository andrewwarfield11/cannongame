/**
 * Guys is used to create all of the land based enemies in the game
 *
 * @author  Andrew Warfield
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Guys {

	private int height = 66; // i wasn't paying attention to the sprite size when i made them, so weird number
	private int width = 20;
	private static Tower tower;
	private int xLoc = 5;
	private int yLoc = Ground.getTopOfGround() - (2*height/3);
	private int speed;
	private boolean living;
	private boolean attacking = false;
	private boolean attackAnim = false;
	private int deathTime = 0; // counts how long the guy has been dead
	private int attackingTime = 100; // counts how long the guy has been attacking the tower
	private Rectangle rect;
	private Sprite liveSprite;
	private Sprite deadSprite;
	private Sprite attackingSprite;
	private String fileName;
	private int timer = 0;
	private BufferedImage[] ims;
	private BufferedImage[] attackingIms;
	
	private ClipInfo clip;
	/**
	 * This is a constructor for Guys
	 * @param s int the guy's speed
	 */
	public Guys(int s) {
		speed = s;
		tower = new Tower();
		living = true;
		rect = new Rectangle(xLoc, yLoc, width, height);
		fileName = "guywalking.png";
		liveSprite = new Sprite(xLoc, yLoc, width, height, fileName);
		attackingSprite = new Sprite(xLoc,yLoc,width,height,"guypunch.png");
	}
	
	/**
	 * constuctor for Guys
	 * @param s int the guy's speed 
	 * @param f String the file name of the guy's sprite
	 */
	public Guys(int s, String f) {
		speed = s;
		tower = new Tower();
		living = true;
		yLoc += 30;
		rect = new Rectangle(xLoc, yLoc, width, height);
		liveSprite = new Sprite(xLoc, yLoc, width, height, f);
		fileName = f;
	}

	/**
	 * This method moves the Guy or, if they are at the tower, it has them attack the tower
	 */
	public void move() {
		if(living) {
			if(!tower.guyIntersects(this)) {
				xLoc += speed;
				rect.x = xLoc;
				liveSprite.translate(speed,0);
				if(attackingSprite != null)
					attackingSprite.translate(speed, 0);
			}
			else if(attackingTime >= 100) {
				if(!attacking)
					attacking = true;
				attackAnim = true;
				attackingTime = 0;
				if(fileName == "clownStrip.png")
					tower.kill();
				attackTower();
			}
			else {
				incrementAttackingTime();
				if(attackingTime == 5) {
					attackAnim = false;
					timer = 0;
				}
			}
		}
	}
	
	private boolean inAttackAnim() {
		return attackAnim;
	}
	

	/**
	 * Gets the speed of this instance of a Guy
	 * @return int The speed of the guy
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * This method is used to determine how long a guy has been dead
	 */
	public void incrementDeathTime() {
		deathTime++;
		deadSprite.setImage(deadSprite.blur(deathTime));
	}

	/**
	 * This method is used to determine how long a guy has been attacking the tower
	 */
	public void incrementAttackingTime() {
		attackingTime++;
	}

	/**
	 * This method attacks the tower
	 */
	public void attackTower() {
		tower.isAttackedByGuy();
	}


	/**
	 * This method is used to get the x Location of the guy
	 * @return double the x location of the guy
	 */
	public double getXLoc() {
		return xLoc;
	}

	/**
	 * This method is used to get the y Location of the guy
	 * @return double the y location of the guy
	 */
	public double getYLoc() {
		return yLoc;
	}

	/**
	 * This method is used to get the width of the guy
	 * @return double Returns the width of the guy
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * This method kills the guy
	 */
	public void ded() {
		clip = new ClipInfo("dead", "gag.wav");
		clip.play( false);
		living = false;
		deadSprite = new Sprite(xLoc, Ground.getTopOfGround(), height, width, "deadguy.png");
	}

	/**
	 * This method is used to get the rectangle of the guy
	 * @return Rectangle Returns the Rectangle representing the guy
	 */
	public Rectangle getRect() {
		return rect;
	}

	/**
	 * This method is used to determine if the guy is alive
	 * @return boolean Returns whether or not the guy is alive
	 */
	public boolean isAlive() {
		return living;
	}
	
	public boolean isAttackingTower() {
		return attacking;
	}

	/**
	 * This method is used remove the guy's corpse after he has been dead for a certain amount of time
	 * @return boolean True if the guy has been dead for long enough, false otherwise
	 */
	public boolean rotted() {
		if(deathTime > 75)
			return true;
		else 
			return false;
	}

	/**
	 * This method is used to draw the guy
	 */
	public void draw(Graphics g) {
		/*
		g.setColor(Color.darkGray);
		if(living)
			g.fillRect(xLoc, yLoc, width, height );
		if(!living && deathTime <= 75)
			g.fillRect(xLoc, Ground.getTopOfGround() - width, height, width); // guy falls on his side, dead
		 */
		if(fileName == "clownStrip.png" && living ) { //CLOWN
			if(ims == null)
				ims = liveSprite.loadStripImageHorizontal(fileName, 3);
			timer++;
			if(timer==3) 
				timer = 0;
			g.drawImage(ims[timer], xLoc, yLoc, null);
		}
		else if(living && !isAttackingTower()) { // walking
			if(ims == null)
				ims = liveSprite.loadStripImageHorizontal(fileName, 4);
			timer++;
			if(timer == 4)
				timer = 0;
			g.drawImage(ims[timer], xLoc, yLoc, null);
		}
		else if(isAttackingTower() && fileName != "clownStrip.png" && living) { // attacking tower
			if(attackingIms == null) {
				attackingIms = liveSprite.loadStripImageHorizontal("guypunch.png", 5);
				timer = 0;
			}
			if(this.inAttackAnim() )
				timer++;
			g.drawImage(attackingIms[timer], xLoc, yLoc, null);
		}
		else if(!living && deathTime <= 75 && fileName != "clownStrip.png") { //dead
			deadSprite.drawSprite(g); // guy falls on his side, dead
		}
		
	}  // end of draw()
}
