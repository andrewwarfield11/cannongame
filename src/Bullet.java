/**
 * Bullet is used to create and manage all of the bullets shot out by the cannon
 *
 * @author  Andrew Warfield
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
public class Bullet {

	//MAKE EXPLOSIVE BULLETS EXPLODE WHEN THEY HIT THE GROUND
	protected final int speed = 6; // default value for normal ammo; changes for explosive ammo
	//private double gravity = 1;
	protected int height;
	protected int width;
	protected Point2D.Double location; // current location of the bullet 
	private static Cannon cannon = new Cannon(); 
	protected boolean moving = true;
	protected double xOffset; //used to determine the angle between the original location and the click
	protected double yOffset; //used to determine the angle between the original location and the click
	protected boolean firstMove = true; // keeps track of if it is the first call to move or not for this object
	protected Point2D.Double destination; // where the user clicked; determines direction bullet moves
	protected Rectangle rect;
	
	//these are used for the explosion
	private boolean explosive;
	private boolean exploding = false;
	private boolean finishedExploding = false;
	private int explosionRadius;
	private int explosionRadius2;
	private Ellipse2D.Double ellipse;
	private Sprite explodeSprite;
	private boolean success = false;
	private ClipInfo boom;
	

	/**
	 * This is a constructor for Bullet
	 * @param point This is the point that the bullet will go towards
	 * @param e boolean This keeps track of if the bullet is explosive or not
	 * @param a boolean This keeps track of if the bullet is part of an airstrike or not
	 */
	public Bullet(Point2D.Double point, boolean e, boolean a) { // bullets from cannon
		height = 10;
		width = 10;
		location = new Point2D.Double(cannon.getX(), cannon.getY() + cannon.getHeight()/4);
		destination = point; // don't forget to modify the draw as well eventually
		rect = new Rectangle((int)cannon.getX(), (int)cannon.getY(), height, width);
		explosive = e;
		if(!a)
			makeSound("boom", "cannon.wav");
	}
	
	private void makeSound(String soundName, String fileName) {
		boom = new ClipInfo(soundName, fileName);
		boom.play( false);
	}

	/**
	 * This method returns the bullet's speed
	 *
	 * @return int This returns bullet speed
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * This method gets whether the bullet is finished exploding or not
	 * @return boolean of whether the bullet has finished exploding
	 */
	public boolean finishedExploding() {
		return finishedExploding;
	}
	/**
	 * This method gets whether the bullet is explosive or not.
	 * @return boolean The flag keeping track of the bullet type.
	 */
	public boolean isExplosive() {
		return explosive;
	}
	
	/**
	 * Gets the ellipse associated with the explosion
	 * @return Ellipse2D.Double
	 * The ellipse keeping track of the explosion's hitbox
	 */
	public Ellipse2D.Double getExplosion() {
		return ellipse;
	}

	/**
	 * This method causes the bullet to start exploding
	 */
	public void explode() {
		//System.out.println("kaboom3");
		
		exploding = true;
		explosionRadius = 10;
		explosionRadius2 = 5;
		explodeSprite = new Sprite((int)location.x, (int)location.y, explosionRadius2*2, explosionRadius2*2, "kaboom.png");
		ellipse = new Ellipse2D.Double();
		makeSound("explode", "explosion.wav");
	}

	/**
	 * Gets whether the bullet is currently exploding
	 * @return boolean The flag keeping track of if the boolean is currently exploding
	 */
	public boolean isExploding() {
		return exploding;
	}
	
	/**
	 * This method is used determine if the bullet is currently moving or not
	 * 
	 * @return boolean This returns true if the bullet is moving and false if it is not
	 */
	public boolean isNotMoving() {
		if(!moving)
			return true;
		else
			return false;
	}

	/**
	 * This method is used to determine if the bullet is currently on the screen or not
	 * 
	 * @return boolean If the bullet is on the screen it returns true. If not, it returns false and 
	 * sets moving to false.
	 */
	public boolean isOnScreen() {
		if(location.getX() < 0 || location.getY() < 0 || location.getX() > GameFrame.pWidth 
				|| location.getY() > GameFrame.pHeight) {
			moving = false;
			return false;
		}
		else
			return true;
	}

	/**
	 * This method is used to get the bullet's current location.
	 * 
	 * @return Point2D.Double This returns the bullets current location.
	 */
	public Point2D.Double getLocation() {
		return location;
	}

	/**
	 * This method stops the bullet by setting moving to false
	 * 
	 */
	public void stopMoving(){
		moving = false;
	}

	/**
	 * This method is used before shooting a bullet. It determines the angle
	 * that the bullet will go toward and sets moving to true.
	 *
	 */
	public void prepareShot() {

		if(firstMove) {
			double run = destination.getX() - location.getX();
			double rise = destination.getY() - location.getY();
			double hypotenuse = Math.sqrt((run*run) + (rise*rise));
			double distance = destination.distance(location);
			if(hypotenuse != distance)
				System.out.println("I suck at math.");
			xOffset = run / hypotenuse * speed;
			yOffset = rise / hypotenuse * speed;
			firstMove = false;
			moving = true; // making sure
			//}
		}
		//gravity *= 1.001;

	}
	
	/**
	 * This method is used to determine if the bullet has hit the ground
	 * @return boolean keeping track of if the bullet has hit the ground
	 */
	public boolean hitGround() {
		if(Ground.getRect().intersects(this.rect)) {
			return true;
		}
		else
			return false;
	}
	

	/**
	 * This method is used to move the bullet in the direction found in prepareShot()
	 * 
	 */
	public void move() {
		if(isOnScreen() && !exploding) {//add gravity back later
			//location.setLocation(location.getX() + xOffset, location.getY() + yOffset + gravity); 
			//rect.x = (int)location.getX() + (int)xOffset;
			//rect.y = (int)location.getY() + (int)yOffset + (int)gravity;
			//not sure if i want to include gravity, makes the game feel more clunky
			location.setLocation(location.getX() + xOffset, location.getY() + yOffset); 
			rect.x = (int)location.getX() + (int)xOffset;
			rect.y = (int)location.getY() + (int)yOffset;
		}
		//gravity *= 1.001;
	}

	/**
	 * This method is used to determine if the bullet hit a Guy
	 * @param guy This is the guy that it tests if the bullet hit
	 * @return boolean This returns true if the bullet hit the Guy and false otherwise
	 */
	public boolean hitGuy(Guys guy) {
		if(rect.intersects(guy.getRect())) {
			success = true;
			return true;
		}
		else 
			return false;
	}

	/**
	 * This method is used to determine if the bullet hit a FlyingGuy
	 * @param guy This is the FlyingGuy that it tests if the bullet hit
	 * @return boolean This returns true if the bullet hit the FlyingGuy and false otherwise
	 */
	public boolean hitFlyingGuy(FlyingGuy guy) {
		if(rect.intersects(guy.getRect())) {
			success = true;
			return true;
		}
		else 
			return false;
	}

	/**
	 * This method is used to determine if the bullet hit a FlyingGuy's bullet
	 * @param guy This is the FlyingGuy in question
	 * @return boolean This returns true if the bullet hit the FlyingGuy's bullet and false otherwise
	 */
	public boolean hitEnemyBullet(FlyingGuy guy) {
		if(rect.intersects(guy.getBulletRectangle()))
			return true;
		else 
			return false;
	}
	
	/**
	 * This method returns whether the bullet hit an enemy or not
	 * @return boolean True if the bullet hit an enemy, false otherwise
	 */
	public boolean hit() {
		return success;
	}
	
	private void kaboom() {
		//System.out.println("kaboom2");
		// center = (location.getX(),location.gety())
		explosionRadius += 10;
		explosionRadius2 += 5;
		ellipse.setFrame(location.getX()-explosionRadius/2, location.getY()-explosionRadius/2, explosionRadius, explosionRadius);
	}

	/**
	 * This method draws the bullets
	 * 
	 */
	public void draw(Graphics g){
		if(!exploding) {
			g.setColor(Color.black);
			if(!isNotMoving() && isOnScreen()){
				g.fillOval((int)location.getX(), (int)location.getY(), width, height);
			}

		}
		else {
			//System.out.println("kaboom");
			g.setColor(Color.red);
			kaboom();
			g.drawImage(explodeSprite.getImage(), (int)location.getX() - explosionRadius2, (int)location.getY() - explosionRadius2,
					2*explosionRadius2, 2*explosionRadius2, null);
			//g.fillOval((int)ellipse.getX(), (int)ellipse.getY(), (int)ellipse.getWidth(), (int)ellipse.getHeight());

			if(explosionRadius >= 300) {
				exploding = false;
				finishedExploding = true;
			}
		}
			
		
	}  // end of draw()

}
