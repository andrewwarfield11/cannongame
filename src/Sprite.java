
// Sprite.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A Sprite has a position, velocity (in terms of steps),
   an image, and can be deactivated.

   The sprite's image is managed with an ImagesLoader object,
   and an ImagesPlayer object for looping.

   The images stored until the image 'name' can be looped
   through by calling loopImage(), which uses an
   ImagesPlayer object.

 */

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.IOException;


public class Sprite 
{  

	// image-related
	private String imageName;
	private BufferedImage image;
	private int width, height;     // image dimensions

	private int pWidth, pHeight;   // panel dimensions

	private boolean isActive = true;      
	// a sprite is updated and drawn only when it is active

	// protected vars
	protected int locx, locy;        // location of sprite

	private GraphicsConfiguration gc;

	private int stripNum;
	private int loopCount = 0;
	private boolean isLooping = false;



	/**
	 * constructor for sprite
	 * @param x int the x location
	 * @param y int the y location
	 * @param w int the width
	 * @param h int the height
	 * @param name String the name of the file containing the image
	 */
	public Sprite(int x, int y, int w, int h, String name) 
	{ 
		locx = x; locy = y;
		pWidth = w; pHeight = h;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		setImage(name);    // the sprite's default image is 'name'
	} // end of Sprite()


	/**
	 * setter for the sprite's image
	 * @param name String the name of the file containing the sprite's new image
	 */
	public void setImage(String name)
	// assign the name image to the sprite
	{
		imageName = name;
		try {
			image = ImageIO.read(getClass().getResource(name));
		} catch(IOException e) {e.printStackTrace();}
		if (image == null) {    // no image of that name was found
			System.out.println("No sprite image for " + imageName);
		}
		else {
			width = image.getWidth();
			height = image.getHeight();
		}
		// no image loop playing 
	}  // end of setImage()

	/**
	 * Sets the sprite's image
	 * @param i BufferedImage the new image for the sprite
	 */
	public void setImage(BufferedImage i) {
		image = i;
	}

	/**
	 * gets the sprite's image
	 * @return BufferedImage the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * blurs the image
	 * @param time The amount of time since starting to blur the image, used to determine if it should blur
	 * @return BufferedImage the blurred image, if it was blurred
	 */
	public BufferedImage blur(int time) {
		if(time < 5 || time%5 == 0) {
			return image;
		}
		else {
			int num = 9;
			float[] matrix = new float[num];
			float xxx = 1.0f / (float)num;
			for(int i = 0; i < matrix.length; i++) {
				matrix[i] = xxx;
			}
			Kernel kernel = new Kernel(3, 3, matrix);
			ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
			BufferedImage i = op.filter(image,null );
			return i;
		}
	}

	/**
	 * Converts a strip image into distinct buffered images
	 * @param fnm String the filename of the strip image
	 * @param number int the number of images in the strip
	 * @return BufferedImage[] an array of the images in the strip
	 */
	public BufferedImage[] loadStripImageHorizontal(String fnm, int number) {
		if (number <= 0) {
			System.out.println("number <= 0; returning null");
			return null;
		}

		BufferedImage stripIm;
		if ((stripIm = loadImage(fnm)) == null) {
			System.out.println("Returning null");
			return null;
		}

		int imWidth = (int) stripIm.getWidth() / number;
		int height = stripIm.getHeight();
		int transparency = stripIm.getColorModel().getTransparency();

		BufferedImage[] strip = new BufferedImage[number];
		Graphics2D stripGC;

		// each BufferedImage from the strip file is stored in strip[]
		for (int i=0; i < number; i++) {
			strip[i] =  gc.createCompatibleImage(imWidth, height, transparency);

			// create a graphics context
			stripGC = strip[i].createGraphics();
			stripGC.setComposite(AlphaComposite.Src);

			// copy image
			stripGC.drawImage(stripIm, 
					0,0, imWidth,height,
					i*imWidth,0, (i*imWidth)+imWidth,height,
					null);
			stripGC.dispose();
		} 
		return strip;
	}

	/**
	 * loads an image
	 * @param fnm the filename of the image
	 * @return the image
	 */
	public BufferedImage loadImage(String fnm) {
		try {
			BufferedImage im = ImageIO.read(getClass().getResource(fnm) );
			int transparency = im.getColorModel().getTransparency();
			BufferedImage copy = gc.createCompatibleImage(im.getWidth(),im.getHeight(),transparency );
			// create a graphics context
			Graphics2D g2d = copy.createGraphics();

			// copy image
			g2d.drawImage(im,0,0,null);
			g2d.dispose();
			return copy;
		} catch(IOException e) {
			return null;
		}
	} // end of loadImage() using ImageIO


	/**
	 * loops a strip image
	 * this method doesnt do anyhthing useful
	 * @param seqDuration the total time of the loop
	 * @return boolean
	 */
	public boolean loopStripImage(int seqDuration)
	/* Switch on loop playing. The total time for the loop is
     seqDuration secs. The update interval (from the enclosing
     panel) is animPeriod ms. */
	{
		int swapTime = seqDuration;
		if (stripNum > 1) {
			isLooping = true;
		}
		else
			System.out.println(imageName + " is not a sequence of images");
		if(swapTime == loopCount ) {
			loopCount = 0;
			return true;
		}
		else {
			loopCount++;
			return false;
		}
	}  // end of loopImage()

	/**
	 * increments size
	 * not currently used, wasn't necessary
	 * @param radius radius of image
	 * @return new image
	 */
	public BufferedImage incrementSize(int radius) {
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage res;

		Graphics2D g2d;

		int transparency = image.getColorModel().getTransparency();
		res =  gc.createCompatibleImage(width, height, transparency);

		// create a graphics context
		g2d = res.createGraphics();
		g2d.setComposite(AlphaComposite.Src);

		// copy image
		g2d.drawImage(res, 
				0,0, width,height,
				0,0, width +2*radius,height+2*radius,
				null);
		g2d.dispose();
		/*img the specified image to be drawn. This method does nothing if img is null.
		dx1 the x coordinate of the first corner of the destination rectangle.
		dy1 the y coordinate of the first corner of the destination rectangle.
		dx2 the x coordinate of the second corner of the destination rectangle.
		dy2 the y coordinate of the second corner of the destination rectangle.
		sx1 the x coordinate of the first corner of the source rectangle.
		sy1 the y coordinate of the first corner of the source rectangle.
		sx2 the x coordinate of the second corner of the source rectangle.
		sy2 the y coordinate of the second corner of the source rectangle.
		observer object to be notified as more of the image is scaled and converted.*/
		return res;
	} 


	/*public void stopLooping()
  {
    if (isLooping) {
      player.stop();
      isLooping = false;
    }
  }  // end of stopLooping()
	 */

	/**
	 * gets width
	 * @return int width
	 */
	public int getWidth()    // of the sprite's image
	{  return width;  }

	/**
	 * gets height
	 * @return int height
	 */
	public int getHeight()   // of the sprite's image
	{  return height;  }

	/**
	 * get panel width
	 * @return panel width
	 */
	public int getPWidth()   // of the enclosing panel
	{  return pWidth;  }

	/**
	 * get panel height
	 * @return panel height
	 */
	public int getPHeight()  // of the enclosing panel
	{  return pHeight;  }

	/**
	 * gets if the sprite is active
	 * @return booelan if its active
	 */
	public boolean isActive() 
	{  return isActive;  }

	/**
	 * makes it active or inactive
	 * @param a the boolean of the state
	 */
	public void setActive(boolean a) 
	{  isActive = a;  }

	/**
	 * sets the sprite's position
	 * @param x int the x location
	 * @param y int the y location
	 */
	public void setPosition(int x, int y)
	{  locx = x; locy = y;  }

	/**
	 * translates the sprite
	 * @param xDist int the distance moved in the x direction
	 * @param yDist int the distance moved in the y direction
	 */
	public void translate(int xDist, int yDist)
	{  locx += xDist;  locy += yDist;  }

	/**
	 * gets the x position
	 * @return int the x position
	 */
	public int getXPosn()
	{  return locx;  }

	/**
	 * gets the y position
	 * @return int the y position
	 */
	public int getYPosn()
	{  return locy;  }


	/**
	 * gets the rectangle 
	 * @return Rectangle the rectangle 
	 */
	public Rectangle getMyRectangle()
	{  return  new Rectangle(locx, locy, width, height);  }


	/**
	 * draws the sprite
	 * @param g the graphics used to draw the sprite
	 */
	public void drawSprite(Graphics g) 
	{
		if (isActive()) {

			// if (isLooping)
			//   image = player.getCurrentImage();
			g.drawImage(image, locx, locy, null);

		}
	} // end of drawSprite()

}  // end of Sprite class
