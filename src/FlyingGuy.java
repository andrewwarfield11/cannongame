/**
 * FlyingGuy is used to create the objects for all of the flying enemies
 *
 * @author  Andrew Warfield
 */

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

public class FlyingGuy {

	protected int height;
	private int spawnHeight;
	protected int width;
	private int spawnWidth;
	protected int xLoc;
	protected int yLoc;
	protected int timeBetweenShots;
	protected Rectangle rect;
	private Rectangle spawnRect;
	private Random rand;
	protected Point2D.Double point;
	protected int shotTimer;
	protected Point2D.Double bullet; // each flying guy can have one bullet out at a time
	protected Rectangle bullRect;
	protected boolean alive = true;
	protected boolean firstShot;
	protected boolean isShooting;
	protected double xOffset = 0;
	protected double yOffset = 0;
	protected int bulletSpeed = 6;
	protected Tower tower= new Tower();
	protected int bulletRadius = 7;
	protected boolean bulletDestroyed = false;
	private Sprite sprite;
	private String fileName;
	private int animTime = 1;
	private BufferedImage[] bI;
	private BufferedImage image;
	private int anim;
	protected ClipInfo crash;

	/**
	 * This is a constructor for FlyingGuy
	 */
	public FlyingGuy() {
		height = 20;
		width = 60;
		shotTimer = -50;
		isShooting = false;
		firstShot = true;
		timeBetweenShots = 50;
		fileName = "flyingguystrip.png";
		newLocation();
		sprite = new Sprite(xLoc, yLoc, width, height, fileName);
		bI = new BufferedImage[4];
		bI = sprite.loadStripImageHorizontal(fileName, 4);
		anim = 0;
		image = bI[0];
		crash = new ClipInfo("shoot",  "enemyshot.wav");
	}


	/**
	 * This method is used get the point representing the FlyingGuy's bullet
	 * @return Point2D.Double Returns the point representing the bullet
	 */
	public Point2D.Double getBullet() {
		return bullet;
	}
	
	/**
	 * Gets the point at the center of the flying guy
	 * @return Point2D.Double The point representing the center of the enemy
	 */
	public Point2D.Double getCenterOfGuy() {
		return new Point2D.Double(xLoc+30, yLoc+10);
	}

	/**
	 * Spawns a FlyingGuy in a different location if it attempts to spawn on top of a currently living FlyingGuy
	 * @param list The current linked list of living FlyingGuys
	 */
	public void preventSpawnIntersection(ArrayList<FlyingGuy> list) { 
		// probably not the best way to do it, but it should work

		int counter = 0;
		while(counter <= list.size()-1) {
			for(FlyingGuy f: list) {
				// sets new location if it intersects a member of the current linked list
				if(f.getSpawnRect().intersects(this.spawnRect)) {
					newLocation();
					sprite.setPosition(xLoc, yLoc);
					counter = 0;
				}
				else
					counter++; 
				// breaks out of the while if it has gone through all elements without intersection
			}
		}
	}

	private void newLocation() {

		rand = new Random();
		int temp = rand.nextInt((int)(GameFrame.pHeight / 2));
		xLoc = 5;
		yLoc = temp + GameFrame.pHeight / 4; 
		rect = new Rectangle(xLoc, yLoc, width, height);
		point = new Point2D.Double(xLoc, yLoc);
		bullet = new Point2D.Double(xLoc, yLoc);
		bullRect = new Rectangle(xLoc, yLoc, bulletRadius, bulletRadius);

		//these three are used to make sure flying guys don't spawn too close to each other
		spawnHeight = height + 20;
		spawnWidth = width + 20;
		spawnRect = new Rectangle(xLoc, yLoc, spawnWidth, spawnHeight);
	}

	private Rectangle getSpawnRect() {
		return spawnRect;
	}

	/**
	 * This method is used get the rectangle representing the FlyingGuy's bullet
	 * @return Rectangle Returns the rectangle representing the bullet
	 */
	public Rectangle getBulletRectangle() {
		return bullRect;
	}

	/**
	 * This method is used to destroy the FlyingGuy's bullet
	 */
	public void destroyBullet() {
		bulletDestroyed = true;
	}

	/**
	 * This method is used get to reset the FlyingGuy's bullet after it hits the tower or is destroyed
	 */
	public void goBack() {
		bullRect.setLocation(xLoc, yLoc); 
		bullet.setLocation(xLoc, yLoc);
		shotTimer = 0;
		bulletDestroyed = false;
	}

	/**
	 * This method is used determine if the flying guy should shoot a bullet, and if yes, it shoots
	 */
	public void tryToShoot() {

		if(shotTimer < timeBetweenShots && isShooting == false) {
			shotTimer++;
		}
		else if(shotTimer >= timeBetweenShots) {
			isShooting = true; 
			moveBullet();
		}

	}

	/**
	 * This method is used to move the bullet
	 */
	public void moveBullet() {
		if(firstShot) {
			crash.play( false);
			Point2D.Double towerPoint = tower.getPointOnTower();
			double run = towerPoint.getX() - bullet.getX();
			double rise = towerPoint.getY() - bullet.getY();
			double hypotenuse = Math.sqrt((run*run) + (rise*rise));
			double distance = towerPoint.distance(bullet);
			if(hypotenuse != distance)
				System.out.println("I suck at math.");
			xOffset = run / hypotenuse * bulletSpeed;
			yOffset = rise / hypotenuse * bulletSpeed;
			firstShot = false;
			//}
		}

		if(!bullRect.intersects(tower.getRect())) {//add gravity back later
			bullet.setLocation(bullet.getX() + xOffset, bullet.getY() + yOffset); 
			bullRect.x = (int)bullet.getX() + (int)xOffset;
			bullRect.y = (int)bullet.getY() + (int)yOffset;
		}
		else {
			isShooting = false;
			tower.isAttackedByFlyingGuy();
			goBack();
		}

	}

	/*public boolean inExplosion(Ellipse2D.Double e) {
		int minX = rect.x;
		int maxY = rect.y;
		int maxX = rect.x + rect.width;
		int minY = rect.y + rect.height;

		int ellipseX = e.
	}*/

	/**
	 * This method is used to kill the flying guy
	 */
	public void ded() {
		crash = new ClipInfo("dead", "gag.wav");
		crash.play(false);
		alive = false;
	}

	/**
	 * This method is used to determine if the flying guy is alive
	 * @return boolean Returns true if the flying guy is alive and false if not
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * This method is used get the point representing the FlyingGuy's location
	 * @return Point2D.Double Returns the point representing the flying guy's location
	 */
	public Point2D.Double getLocation() {
		return point;
	}

	/**
	 * This method is used to set the flying guy's y location
	 * @param int The value of the new y location
	 */
	public void setYLoc(int y) {
		yLoc = y;
		rect.y = y;
	}

	/**
	 * This method is used to get the rectangle representing the flying guy
	 * @return Rectangle The rectangle representing the flying guy's location
	 */
	public Rectangle getRect() {
		return rect;
	}

	/**
	 * This method is used to get the flying guy's y location
	 * @return Point2D.Double Returns the point representing the bullet
	 */
	public int getYLoc() {
		return yLoc;
	}

	/**
	 * This method is used to draw the flying guy and its bullets
	 */
	public void draw(Graphics g) {
		/*g.setColor(Color.gray);

		if(alive)
			g.fillRect(xLoc, (yLoc), width, height );
			*/
		g.setColor(Color.black);
		if(isShooting && !bulletDestroyed) {
			g.fillOval((int)bullet.x, (int)bullet.y, bulletRadius, bulletRadius);
		}
		if(alive) {
			//if(sprite.loopStripImage(animTime)) {
				increaseAnim();
			//}
			image = bI[anim];
			g.drawImage(image, sprite.getXPosn(), sprite.getYPosn(), null);
		}/*
		
		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		DONT CONTINUE WITH THIS TOMORROW
		AND GET THE EXPLOSION ANIMATION DOWN
		do this after those maybe
		but also sounds
		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
		
		
		*/

	}
	
	private void increaseAnim() {
		if(anim == bI.length-1) 
			anim = 0;
		else
			anim++;
	}
	

}
