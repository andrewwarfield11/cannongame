import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class AirStrikeBullet extends Bullet {
	
	/**
	 * Constructor for the AirStrikeBullet class
	 * @param target The point representing the bullet's final location
	 * @param start The point representing the bullet's starting location
	 */
	public AirStrikeBullet(Point2D.Double target, Point2D.Double start) {
		super(target, false, true);
		destination = target;
		location = start;
		rect = new Rectangle((int)location.x, (int)location.y, width, height);
	}
	
	@Override
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
		else
			move();
	}
	
	@Override
	public void move() {
		if(isOnScreen()) {
			location.setLocation(location.getX() + xOffset, location.getY() + yOffset); 
			rect.x = (int)location.getX() + (int)xOffset;
			rect.y = (int)location.getY() + (int)yOffset;
		}
		else
			moving = false;
	}
	
	/**
	 * Tests if any members of an array list of guys intersect this bullet, and removes them if so
	 * Note: this does the same thing as what I have in Bullet, but it should be more efficient
	 * @param g The array list of guys
	 * @return The new array list of guys after checking for collisions and removing
	 */
	public ArrayList<Guys> hitGuy(ArrayList<Guys> g) {
		for(int i = 0; i < g.size(); i++) {
			if(rect.intersects(g.get(i).getRect()))
				g.remove(i);
		}
		return g;
	}
	
	/**
	 * Tests if any members of an array list of flying guys or their bullets intersect this bullet, and removes them if so
	 * Note: this does the same thing as what I have in Bullet, but it should be more efficient
	 * @param g The array list of flying guys
	 * @return The new array list of flying guys after checking for collisions and removing
	 */
	public ArrayList<FlyingGuy> hitFlyingGuy(ArrayList<FlyingGuy> g) {
		for(int i = 0; i < g.size(); i++) {
			if(rect.intersects(g.get(i).getRect()))
				g.remove(i);
			else if(rect.intersects(g.get(i).getBulletRectangle()))
				g.get(i).destroyBullet();
		}
		return g;
	}
	
	
}
