// MyGame2.java
// Andrew Warfield

/* 
 * You control a cannon on top of a tower, trying to protect the tower from enemies attacking it. 
 * There are both flying enemies trying to shoot the tower and enemies on the ground trying to walk
 * and attack it. You can shoot and kill both of them along with the bullets shot by the flying 
 * enemies. You have to reload for a short time after every 30 shots.  Your score is the number of
 * enemies killed.
 * 
 * At the moment, the game ends as soon as the tower reaches 0 health, but I intend to implement 
 * levels soon, and will probably make the game get a bit harder every level.
 */

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.IOException;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class Game extends GameFrame {

	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 80;

	private int maxAmmo = 30;
	private int flyingGuySpawnRate = 120; // note: increasing this lowers the spawn rate
	private int guySpawnRate = 130; // same deal
	private int reloadingTime = 160;
	private int maxLevel = 10;
	private int maxEnemiesSpawned = 30;
	private int enemiesSpawned = 0;
	private int guySpeed = 1;
	private long airStrikeStart;
	private boolean isExplosive = false;
	private boolean ownsDrone = false;
	private boolean ownsAirStrike = false; // fix airstrike sound
	private boolean airStriking = false;
	private boolean airStrikeUsed = false;
	private ArrayList<Bullet> fred; // the bullets
	private ArrayList<AirStrikeBullet> airStrike; 
	private Point2D.Double fredClick;
	private Cannon cannon;
	private ArrayList<Guys> guys;
	private ArrayList<FlyingGuy> flies;
	private Drone drone;
	private Tower tower;
	private Menu menu;
	private Ground ground;
	private int timeSinceSpawn;
	private int timeSinceShot;
	private Random rand;
	// used at game termination
	private int ammo = maxAmmo;
	private int outOfAmmoCounter = 0;
	private boolean outOfAmmo = false;
	//what state the game is in
	private final int MENU = 0;
	private final int INGAME = 1;
	private  int gameState = MENU;

	protected int level = 1;
	private double moneyCounter = 0;

	private int score = 0;

	private Font font;
	private FontMetrics metrics;
	private BufferedImage menuBackground;
	private BufferedImage gameBackground;
	private BufferedImage shopBackGround;

	private Rectangle ammoRect;
	private Rectangle healthBarRect;
	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp

	//private ClipsLoader clip;
	private ClipInfo airClip;
	private ClipInfo backClip;

	/**
	 * contructor for game
	 * @param period long the period
	 */
	public Game(long period) {
		super(period);		
	}

	//resetting variables for the start of a new level
	private void newLevel() {
		if(level != maxLevel) {
			outOfAmmo = false;
			fred.clear();
			outOfAmmoCounter = 0;
			timeSinceSpawn = 0;
			ammo = maxAmmo;
			timeSinceShot = 100;
			flyingGuySpawnRate -= 5;
			guySpawnRate -=5;
			enemiesSpawned = 0;
			level++;
			if(level == maxLevel)
				maxEnemiesSpawned = 1;
			else
				maxEnemiesSpawned += 5;
			score += tower.getHealth()/3;
			tower.resetHealth();
			menu.addMoneys((int)moneyCounter);
			menu.addToScore(score);
			menu.changeToShop();
			gameState = MENU;
			moneyCounter = 0;
			score = 0;
			if(ownsDrone) {
				drone.goBack();
				drone.setTarget(null);
				drone.isShooting=false;
			}
			if(ownsAirStrike)
				airStrike.clear();
			airStriking = false;
			this.airStrikeUsed = false;
		}
		else { // end of the game
			menu.won();
			menu.changeToEndScreen();
			gameState = MENU;
		}
	}

	// reset variables to reset game
	private void resetGame() {
		newLevel();
		level = 1;
		reloadingTime = 160;	
		score = 0;
		tower.resetHealth();
		guySpeed = 1;
		ownsDrone = false;
		ownsAirStrike = false;
		isExplosive = false;
		maxEnemiesSpawned = 30;
		flyingGuySpawnRate = 120;
		guySpawnRate = 130;
		maxAmmo = 30;
		ammo = maxAmmo;
	}

	@Override
	protected void simpleInitialize() {
		// create game components

		cannon = new Cannon();
		tower = new Tower();
		System.out.println(tower.getWidth() + "\n" + tower.getHeight());
		flies = new ArrayList<FlyingGuy>();
		guys = new ArrayList<Guys>();
		ground = new Ground();
		System.out.println("Ground height: " + Ground.HEIGHT);
		rand = new Random();
		fred = new ArrayList<Bullet>();
		menu = new Menu();
		airStrike = new ArrayList<AirStrikeBullet>();
		fredClick = new Point2D.Double();
		timeSinceSpawn = 0;
		timeSinceShot = 100;
		try {
			menuBackground = ImageIO.read(getClass().getResource("background.png"));
			gameBackground = ImageIO.read(getClass().getResource("sky.png"));
			shopBackGround = ImageIO.read(getClass().getResource("parchment.jpg"));

		} catch(IOException e) {e.printStackTrace();}
		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);

		metrics = this.getFontMetrics(font);
		ammoRect = new Rectangle(GameFrame.pWidth/3, 5, GameFrame.pWidth/4, metrics.getHeight());
		healthBarRect = new Rectangle((int)cannon.getX(), (int)cannon.getY() - 2*metrics.getHeight(),(int)tower.getWidth(), metrics.getHeight());

		airClip = new ClipInfo("airstrike", "airstrike.wav");
		backClip = new ClipInfo("background", "Tower-Defense.wav");
		//clip.load("background", "Tower-Defense.wav");
		backClip.play( true);
		//MidisLoader midi = new MidisLoader();
		//midi.load("background", "Tower-Defense.mp3.mid");
		//midi.play("background", true);

		//menu.changeToShop();
		//menu.addMoneys(500);
		//menu.changeToEndScreen();



	}

	@Override
	public void keyPress(int k) {
		//if space is pressed
		if(KeyEvent.VK_SPACE == k) {
			System.out.println("Space pressed.");
			if(gameState == INGAME && menu.hasBoughtExplosive() && menu.getExplosiveAmmo() > 0) {
				isExplosive = true;
			}
			else
				isExplosive = false;

		}

	}

	@Override
	protected void mouseRightPress(int x, int y) {

		if(ownsAirStrike && gameState == INGAME && !airStrikeUsed) {
			//clip.play("airstrike", false);
			airClip.play(false);
			airStrikeStart = System.currentTimeMillis();
			// adding bullets into the array list
			for(int i = 0; i < 30; i++) {
				Random rand = new Random();
				double offset = (double)rand.nextInt(GameFrame.pWidth) - GameFrame.pWidth/2; //random number between -width/4 and width/4
				airStrike.add(new AirStrikeBullet(new Point2D.Double(offset, (double)Ground.getTopOfGround()),
						new Point2D.Double((double)x, 0)));
			}
			airStrikeUsed = true;
			airStriking = true;
		}
	}

	@Override
	protected void mouseMiddlePress(int x, int y) { 
		if(gameState == INGAME && menu.hasBoughtExplosive() && menu.getExplosiveAmmo() > 0 && isExplosive == false) {
			isExplosive = true;
		}
		else if(gameState == INGAME && isExplosive)
			isExplosive = false;
	}

	@Override
	protected void mouseLeftPress(int x, int y) {
		if(gameState == INGAME) {
			if(timeSinceShot >= 0 && !outOfAmmo) { // taking away the restriction for now
				fredClick = new Point2D.Double(x, y);
				if(!tower.triedToShootTheTower(fredClick)) {
					if(!isExplosive || (isExplosive && menu.getExplosiveAmmo() > 0)) {
						//clip.add(new ClipsLoader());
						//clip.get(clip.size()-1).load("cannon", "cannon.wav");
						//clip.get(clip.size()-1).play("cannon", false);
						fred.add(new Bullet(fredClick, isExplosive, false));
						fred.get(fred.size()-1).prepareShot();
						timeSinceShot = 0;
						ammo -= 1;
						if(ammo == 0) {
							outOfAmmo = true;
							ammo = maxAmmo;
						}
						if(isExplosive)
							menu.shotExplosive();
					}
				}
			}
		}
		else if(gameState == MENU) { 
			//generic menu screen
			if((menu.isMenuState() || menu.isEndScreen()) && menu.getExitRect().contains(x, y)) {
				running = false;
			}

			if(menu.isMenuState()) {
				if(menu.getPlayRect().contains(x, y))
					gameState = INGAME;
				else if(menu.getInfoRect().contains(x, y))
					menu.changeToInfo();
				else if(menu.getHSRect().contains(x,y))
					menu.changeToHS();
			}
			//info screen or high scores screen
			else if(menu.isInfoState() || menu.isHighScores()) {
				if(menu.getBackRect().contains(x, y))
					menu.changeToMenu();
			}
			//shop screen
			else if(menu.isShop()) {
				//buying explosive shot/ammo
				if(menu.getExplosiveRect().contains(x,y)) {
					menu.boughtExplosive();
				}
				//next level button
				else if(menu.getBackRect().contains(x,y)) {
					gameState = INGAME;
				}
				//buying increased clip size
				else if(menu.getClipRect().contains(x, y)) { 

					if(menu.boughtClip()) {
						maxAmmo += 5;
						ammo = maxAmmo;
					}
				}
				//buying decreased reloading time
				else if(menu.getReloadRect().contains(x,y)) { 
					if(menu.boughtReload())
						reloadingTime -= 20;

				}
				//buying defense drone
				else if(menu.getDroneRect().contains(x, y)) {
					if(menu.boughtDrone()) {
						ownsDrone = true;
						drone = new Drone((int)tower.getSideOfTower() - 150, (int)tower.getTopOfTower() - GameFrame.pHeight/8);
					}
				}
				//buying airstrike
				else if(menu.getAirStrikeRect().contains(x,y)) {
					if(menu.boughtAirStrike()) {
						ownsAirStrike = true;
						airStrike = new ArrayList<AirStrikeBullet>();
					}
				}
			}
			//end screen
			else if(menu.isEndScreen()) {
				if(menu.getBackRect().contains(x,y)) {
					resetGame();
					menu.reset();
					gameState = INGAME;
					simpleInitialize();
				}
			}
		}

	} 


	@Override
	protected void mouseMove(int x, int y) {

	}

	@Override
	protected void simpleRender(Graphics dbg) {

		if(gameState == INGAME) {
			dbg.drawImage(gameBackground, 0, 0, GameFrame.pWidth, GameFrame.pHeight,0,0,
					gameBackground.getWidth(),gameBackground.getHeight(),null);

			dbg.setColor(Color.blue);
			dbg.setFont(font);
			dbg.drawString("Level: " + level, 18*GameFrame.pWidth/20, GameFrame.pHeight/20);

			// report frame count & average FPS and UPS at top left
			// dbg.drawString("Frame Count " + frameCount, 10, 25);
			dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", "
					+ df.format(averageUPS), 20, 25); // was (10,55)

			dbg.drawRect(healthBarRect.x, healthBarRect.y, healthBarRect.width, healthBarRect.height);
			double ratio = (double)tower.getHealth()/tower.getMaxHealth();
			double size = ratio*healthBarRect.width;
			dbg.setColor(Color.green);
			dbg.fillRect(healthBarRect.x, healthBarRect.y, healthBarRect.width, healthBarRect.height);
			dbg.setColor(Color.red);
			dbg.fillRect(healthBarRect.x, healthBarRect.y, (int)size, healthBarRect.height);

			dbg.setColor(Color.black);

			//making the ammo bar		
			int bWidth = ammoRect.width / maxAmmo / 2;
			int tempX = ammoRect.x;
			for(int i = 0; i < ammo; i++) {
				dbg.drawOval(tempX, 5, bWidth, metrics.getHeight());
				tempX += 2*bWidth;
			}

			if(isExplosive) {
				dbg.drawString("Explosive Ammo: " + menu.getExplosiveAmmo(), 2*GameFrame.pWidth/3, 25);
			}
			else
				dbg.drawString("Normal Ammo", 2*GameFrame.pWidth/3, 25);

			// draw game elements: the cannon and the bullet
			tower.draw(dbg);
			for(int i = 0; i < fred.size(); i++) //drawing all of the bullets
				if(fred.get(i).isOnScreen())
					fred.get(i).draw(dbg);
			ground.draw(dbg);
			cannon.draw(dbg);
			if(ownsAirStrike && airStriking) {
				for(int i = 0; i < airStrike.size(); i++) {
					airStrike.get(i).draw(dbg);
				}
			}
			for(int i = 0; i < guys.size(); i++) // drawing all of the guys
				guys.get(i).draw(dbg);

			for(int i = 0; i < flies.size(); i++) { // drawing the flying guys
				flies.get(i).draw(dbg);
			}
			if(ownsDrone)
				drone.draw(dbg);
			dbg.setColor(Color.black);
			if(outOfAmmo) { // drawing reloading
				dbg.drawString("RELOADING", (int)cannon.getX() - 50, (int)(cannon.getY() - 50));
			}
			if(tower.getHealth() > 0)
				dbg.drawString(tower.getHealthString() + "/1000", (int)cannon.getX(), (int)cannon.getY() + 200);
			else
				dbg.drawString("0/1000", (int)cannon.getX(), (int)cannon.getY() + 200);

		}
		else if(gameState == MENU && menu.isShop()) {
			dbg.drawImage(shopBackGround, 0, 0, GameFrame.pWidth, GameFrame.pHeight, 0, 0, 
					shopBackGround.getWidth(), shopBackGround.getHeight(), null);
			menu.draw(dbg);
		}
		else {
			dbg.drawImage(menuBackground, 0, 0, GameFrame.pWidth, GameFrame.pHeight,0,0,
					menuBackground.getWidth(),menuBackground.getHeight(),null);
			menu.draw(dbg);
		}

	} // end of simpleRender()

	@Override
	protected void simpleUpdate() {
		// gotta get the explosion to actually kill people
		// make sure to work on that later
		// also get it to work with explosive ammo


		if(gameState == INGAME) {

			if(outOfAmmo) { // reloading the cannon
				outOfAmmoCounter++;
				if(outOfAmmoCounter >= reloadingTime) {
					outOfAmmo = false;
					outOfAmmoCounter = 0;
				}
			}
			//spawning new flying guys
			int temp = rand.nextInt(flyingGuySpawnRate); //spawn them randomly
			if(temp == 37 && flies.size() < 4 && enemiesSpawned <= maxEnemiesSpawned && level != maxLevel) {
				FlyingGuy newGuy = new FlyingGuy();
				if(!flies.isEmpty())
					newGuy.preventSpawnIntersection(flies);
				flies.add(newGuy);
				enemiesSpawned++;
			}
			for(int i = 0; i < flies.size(); i++) {			
				flies.get(i).tryToShoot();
			}

			//moving all of the bullets
			if(!fred.isEmpty()) {
				for(int i = 0; i < fred.size(); i++) {
					if(fred.get(i).isExplosive() && fred.get(i).hitGround() &&!fred.get(i).isExploding())
						fred.get(i).explode();

					if(((fred.get(i).isOnScreen() && fred.get(i).moving) || fred.get(i).isExploding()) &&!fred.get(i).finishedExploding()) {
						fred.get(i).move();
						for(int j = 0; j < flies.size(); j++) { // deals with killing the flying guys
							if(fred.get(i).hitFlyingGuy(flies.get(j)) && !fred.get(i).isExploding()) {
								flies.get(j).ded();
								fred.get(i).stopMoving();
								score += 3;
								moneyCounter += 10;
								if(fred.get(i).isExplosive()) {
									fred.get(i).explode();
								}
							}
							if(fred.get(i).isExploding()) {
								if(fred.get(i).getExplosion().intersects(flies.get(j).getRect())) {
									flies.get(j).ded();
									score += 3;
									moneyCounter += 10;
								}
							}

							if(fred.get(i).hitEnemyBullet(flies.get(j))) { // if a bullet hits an enemy bullet
								flies.get(j).destroyBullet();
								fred.get(i).stopMoving();
								score++;
								moneyCounter += 2;
								if(fred.get(i).isExplosive())
									fred.get(i).explode();
								if(fred.get(i).isExploding()) {
									if(fred.get(i).getExplosion().contains(flies.get(j).getBullet())) {
										flies.get(j).destroyBullet();
										score++;
										moneyCounter += 2;
									}
								}
							}
							if(!flies.get(j).isAlive()) // removing dead flying guys
								flies.remove(j);
						}
					}
					else {
						if(!fred.get(i).hit()) {
							score -= 0.5;
						}
						fred.remove(i); // remove off screen bullets
					}
				}
			}

			if(level == maxLevel ) {
				if(timeSinceSpawn == 0) {
					guys.add(new Guys(guySpeed, "clownStrip.png"));
					enemiesSpawned++;
				}
				timeSinceSpawn =1;
			}
			else if(timeSinceSpawn == guySpawnRate && enemiesSpawned < maxEnemiesSpawned) { //spawning new guys
				guys.add(new Guys(guySpeed));
				enemiesSpawned++;
				timeSinceSpawn = 0;
			}
			else {
				timeSinceSpawn++;
			}

			for(int i = 0; i < guys.size();i++) { //moves the guys and kills them if they got hit by a bullet
				if(guys.get(i).isAlive()) {
					guys.get(i).move();
					for(int j = 0; j < fred.size(); j++) {
						if(guys.get(i) != null && fred.get(j).hitGuy(guys.get(i)) && !fred.get(j).isExploding()) { 
							//null pointer exception here???? ^^^
							guys.get(i).ded();
							fred.get(j).stopMoving();
							score += 2;
							moneyCounter += 5;
							if(fred.get(j).isExplosive())
								fred.get(j).explode();

						}
						if(fred.get(j).isExploding()) {
							if(fred.get(j).getExplosion().intersects(guys.get(i).getRect())) {
								guys.get(i).ded();
								score += 2;
								moneyCounter += 5;	
							}
						}

					}
				}
				else if(guys.get(i).rotted()) //removes from the linked list if they've been dead for too long
					guys.remove(i);
				else
					guys.get(i).incrementDeathTime();
			}
			moneyCounter += 0.05;
			timeSinceShot++; // cooldown between cannon shots
			if(tower.isDead()) {
				menu.lost();
				menu.addToScore(score);
				menu.changeToEndScreen();
				gameState = MENU;
				/*else {
					menu.addMoneys((int)moneyCounter);
					menu.changeToShop();
				}*/
			}

			if(ownsDrone) 
				updateDrone();
			if(ownsAirStrike && airStriking)
				updateAirStrike();

			if(enemiesSpawned >= maxEnemiesSpawned && noEnemies()) { // end of a level
				newLevel();
			}


			removeFlyingGuys();
			removeGuys();
		}
	}

	private void updateAirStrike() {
		if(System.currentTimeMillis() - airStrikeStart > 1) {
			for(int i = 0; i < airStrike.size(); i++) {
				airStrike.get(i).prepareShot();
				guys = airStrike.get(i).hitGuy(guys);
				flies = airStrike.get(i).hitFlyingGuy(flies);
				if(airStrike.get(i).isNotMoving())
					airStrike.remove(i);
			}
		}
		else {
			for(int i = 0; i < airStrike.size()/2; i++) {
				airStrike.get(i).prepareShot();
				if(airStrike.get(i).isNotMoving())
					airStrike.remove(i);
				guys = airStrike.get(i).hitGuy(guys);
				flies = airStrike.get(i).hitFlyingGuy(flies);
			}
		}
	}

	private void updateDrone() {
		if(!drone.hasTarget()) {
			//giving the drone a target
			if(flies.isEmpty() && !guys.isEmpty()) {
				// shoot towards the ground
				// need an algorithm to tell where the guy will be when the bullet hits the ground
				Random rand = new Random();
				int num = rand.nextInt(guys.size());
				drone.setTarget(guys.get(num), drone.calcTarget(guys.get(num)));
				drone.tryToShoot();
			}
			else if(flies.size() == 1) {
				//target is the one flying guy alive
				drone.setTarget(flies.get(0));
				drone.tryToShoot();
			}
			else if(flies.size() > 1) {
				Random rand = new Random();
				int num = rand.nextInt(flies.size());
				drone.setTarget(flies.get(num));
				drone.tryToShoot();
				//choose a random flying guy
			}
		}
		else {
			drone.tryToShoot();
			for(int i = 0; i < flies.size(); i++) {
				if(drone.getBulletRectangle().intersects(flies.get(i).getRect())) {
					flies.get(i).ded();
					score += 3;
					moneyCounter += 10;
				}
				else if(drone.getBulletRectangle().intersects(flies.get(i).getBulletRectangle())) {
					flies.get(i).destroyBullet();
					drone.destroyBullet();
					score++;
					moneyCounter += 2;
				}
			}

			for(int i = 0; i < guys.size(); i++) { 
				if(drone.getBulletRectangle().intersects(guys.get(i).getRect())) {
					guys.get(i).ded();
					score += 2;
					moneyCounter += 5;
				}
			}
		}
	}

	private void removeFlyingGuys() {
		for(int i = 0; i < flies.size(); i++) {
			if(!flies.get(i).isAlive())
				flies.remove(i);
		}

	}

	private void removeGuys() {
		for(int i = 0; i < guys.size(); i++) {
			if(!guys.get(i).isAlive() && guys.get(i).rotted())
				guys.remove(i);
		}
	}

	//resetting variables for the start of a new level
	/*private void newLevel() {
		if(level != maxLevel) {
			fred.clear();
			outOfAmmoCounter = 0;
			timeSinceSpawn = 0;
			ammo = maxAmmo;
			timeSinceShot = 100;
			flyingGuySpawnRate -= 5;
			guySpawnRate +=5;
			enemiesSpawned = 0;
			level++;
			if(level == maxLevel)
				maxEnemiesSpawned = 1;
			else
				maxEnemiesSpawned += 5;
			tower.resetHealth();
			if(level == 5) {
				guySpeed++;
			}
			menu.addMoneys((int)moneyCounter);
			menu.addToScore(score);
			menu.changeToShop();
			gameState = MENU;
			moneyCounter = 0;
			score = 0;
			if(ownsDrone) 
				drone.goBack();
			if(ownsAirStrike)
				airStrike.clear();
			airStriking = false;
			this.airStrikeUsed = false;
		}
		else { // end of the game
			menu.won();
			menu.changeToEndScreen();
			gameState = MENU;
		}
	}*/

	private boolean noEnemies() {
		if(flies.isEmpty() && guys.isEmpty())
			return true;
		else
			return false;
	}

	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");
		new Game(period * 1000000L); // ms --> nanosecs
	} // end of main()

} // end of WormChase class

