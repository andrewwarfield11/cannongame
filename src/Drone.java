import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class Drone extends FlyingGuy{

	//working on moving some shit from flying guy into here
	private Point2D.Double target;
	private boolean hasTarget;
	private FlyingGuy jerry;
	private Guys victim;
	private boolean isJerry;
	private Sprite droneSprite;

	/**
	 * Constructor for Drone, used for the defense drone
	 * @param x: The x location of the drone
	 * @param y: The y location of the drone
	 */
	public Drone(int x, int y) {
		height = 26;
		width = 77;
		shotTimer = 0;
		isShooting = false;
		firstShot = true;
		xLoc = x;
		yLoc = y;
		rect = new Rectangle(xLoc, yLoc, width, height);
		point = new Point2D.Double(xLoc, yLoc);
		bullet = new Point2D.Double(xLoc, yLoc);
		bullRect = new Rectangle(xLoc, yLoc, bulletRadius, bulletRadius);
		timeBetweenShots = 15;
		bulletSpeed = 8;
		droneSprite = new Sprite(x,y,width,height,"drone.png");
	}

	/**
	 * returns if the drone has a target
	 * @return boolean The flag keeping track of if the drone has a target
	 */
	public boolean hasTarget() {
		return hasTarget;
	}

	/**
	 * setter for the drone's target
	 * @param t: the point the drone is shooting at
	 */
	public void setTarget(FlyingGuy f) {
		if(f != null) {
			jerry = f;
			target = f.getCenterOfGuy();
			isJerry = true;
			hasTarget = true;
		}
	}

	/**
	 * Calculates the drone's target
	 * @param g The guy its shooting at
	 * @return The point of the target
	 */
	public Point2D.Double calcTarget(Guys g) {
		Point2D.Double p = new Point2D.Double(g.getXLoc(), g.getYLoc());
		double hyp = p.distance(this.getLocation());
		double xDis = hyp * (g.getSpeed()/this.bulletSpeed);
		p.x = p.getX() + xDis;
		return p;
	}

	/**
	 * Sets the drone's target if the target is a guy
	 * @param g Guy the target
	 * @param p the point of the guy
	 */
	public void setTarget(Guys g, Point2D.Double p) {
		victim = g;
		target = new Point2D.Double(p.getX(), GameFrame.pHeight - Ground.HEIGHT);
		isJerry = false;
		hasTarget = true;
	}

	@Override
	public void moveBullet() {
		if(firstShot) {
			crash.play( false);
			double run = target.getX() - bullet.getX();
			double rise = target.getY() - bullet.getY();
			double hypotenuse = Math.sqrt((run*run) + (rise*rise));
			double distance = target.distance(bullet);
			if(hypotenuse != distance)
				System.out.println("I suck at math.");
			xOffset = run / hypotenuse * bulletSpeed;
			yOffset = rise / hypotenuse * bulletSpeed;
			firstShot = false;
			//}
		}

		if((isJerry && bullRect.intersects(jerry.getRect())) 
				|| (!isJerry && bullRect.intersects(victim.getRect()))
				|| bullRect.x < 0 || bullRect.y > GameFrame.pHeight) {

			isShooting = false;
			hasTarget = false;
			firstShot = true;
			goBack();
		}
		else {
			bullet.setLocation(bullet.getX() + xOffset, bullet.getY() + yOffset); 
			bullRect.x = (int)bullet.getX() + (int)xOffset;
			bullRect.y = (int)bullet.getY() + (int)yOffset;
		}
	}


	@Override
	public void tryToShoot() {
		if(shotTimer < timeBetweenShots && isShooting == false) {
			shotTimer++;
		}
		else if(shotTimer >= timeBetweenShots) {
			isShooting = true; 
			moveBullet();
		}

	}

	@Override
	public void draw(Graphics g) {
		if(isShooting && !bulletDestroyed) {
			g.fillOval((int)bullet.x, (int)bullet.y, bulletRadius, bulletRadius);
		}
		droneSprite.drawSprite(g);
	}

}
