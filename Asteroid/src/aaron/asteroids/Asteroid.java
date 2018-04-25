package aaron.asteroids;

import java.util.Random;

public class Asteroid extends HitBox
{
	private Random rnd;
	private int[] rndV;  // for polygon points
	private double velocity; // velocity with direction
	private double XVelocity; // x velocity
	private double YVelocity; // y velocity
	private double angle; // direction
	private int rockSize; // 3-large | 2-mid | 1-small	
	private int health;  // health of asteroid
	private boolean fillOrNoFill; // if the shape is filled
	
	
	public Asteroid()
	{
		this(3,100,10,100,0);
	}
	
	public Asteroid(int rockSize, double x_pos, double y_pos,double velocity, double angle)
	{
		this.rnd = AsteroidApplet.rnd;
		this.rockSize = rockSize;
		this.WIDTH = rockSize * 20;
		this.HEIGHT = rockSize * 20;
		
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.velocity = velocity;
		this.angle = angle;
		
		this.health = 100;
		this.fillOrNoFill = false;
		this.rndV = new int[4];
		generateRandomVal();
	}
	
	// translation
	public void Move()
	{
		XVelocity = (Math.cos(angle * (Math.PI / 180)) * velocity);
	    YVelocity = (Math.sin(angle * (Math.PI / 180)) * velocity);
	    
	    x_pos += XVelocity;
	    y_pos += YVelocity;
	}

	public boolean isAlive()
	{
		if (health <= 0)
		{
			return false;
		}
		return true;
	}
	
	// RANDOMLY GENERATE POINTS ON ASTEROID
	public void generateRandomVal()
	{
		boolean positiveVal;
		for (int i = 0; i < rndV.length; i++)
		{
			positiveVal = rnd.nextBoolean();
			if(positiveVal)
				rndV[i] = rnd.nextInt(((rockSize * 2) - (1)) + 1) + (1);
			else
				rndV[i] = -1 * (rnd.nextInt(((rockSize * 2) - (1)) + 1) + (1));
		}
	}
	
	// GET X POINTS FOR POLYGON
	public int[] getPolygonX()
	{
		int x = (int)x_pos;
		int w = (int)WIDTH;
		
		int[] xP = new int[] {
				  x, 
				 (x + (w/2) + rndV[0]*2), //2
				  x + w, 
				  x + w - 3 + rndV[0] *2, 
				  x + w,
				 (x + (w/2) + rndV[1]*2), //3
				  x , 
				  x + rndV[1] * 2};
		
		return xP;
	}
	
	// GET Y POINTS FOR POLYGON
	public int[] getPolygonY()
	{
		int y = (int)y_pos;
		int w = (int)WIDTH;
		
		int[] yP = new int[] {
				  y, 
				  y - 3 + rndV[2], 
				  y, 
				  y + (w/2) + rndV[2]*2, // 0
				  y + w, 
				  y + w + 3 + rndV[3], 
				  y + w, 
				  y + (w/2) + rndV[3]*2}; // 1
		
		return yP;
	}
	
	public void setFillorNoFill(boolean isFill)
	{
		this.fillOrNoFill = isFill;
	}
	
	public void doDmg(double dmg)
	{
		this.health -= dmg;
	}
	
	public double getAngle()
	{
		return this.angle;
	}
	
	public int getHealth()
	{
		return this.health;
	}
	
	public int getRockSize()
	{
		return this.rockSize;
	}
	
	public boolean getFillOrNoFill()
	{
		return this.fillOrNoFill;
	}
	
	public String toString()
	{
		return "Size: " + getRockSize() + ", X: " + getXPos() + ", Y: " + getYPos();
	}
	
	
}
