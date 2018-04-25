package aaron.asteroids;

import java.util.Random;

public class UFOEnemy extends HitBox
{
	private Random rnd;
	private double velocity; // velocity with direction
	private double XVelocity; // x velocity
	private double YVelocity; // y velocity
	private double angle; // direction
	private Time AngleChangeDelay; // delay until the ship changes direction
	private Time shootDelay; // delay shooting
	private int health;
	private int timeAlive;
	private int UFOType; // 1 = small(aim bot); 2 = large(random shoot)
	
	
	public UFOEnemy()
	{
		
	}
	
	public UFOEnemy(int UFOType, int health, double x_pos, double y_pos, double velocity, double angle)
	{
		this.UFOType = UFOType;
		this.WIDTH = (UFOType  * 20)+10;
		this.HEIGHT = (UFOType  * 10)+10;
		this.health = health;
		this.timeAlive = 1340;
		
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.velocity = velocity;
		this.angle = angle;
		this.AngleChangeDelay = new Time("1");
		this.AngleChangeDelay.start();
		this.shootDelay = new Time("2");
		this.shootDelay.start();
		this.rnd = new Random();
		
	}
	
	// translate the ufo
	public void Move()
	{

		if (AngleChangeDelay.getTimePassed() == 2)
		{
			AngleChangeDelay.resetTime();
			angle = angle + (rnd.nextInt(60 - (-60) + 1) + (-60));
		}
		
		
		XVelocity = (Math.cos(angle * (Math.PI / 180)) * velocity);
	    YVelocity = (Math.sin(angle * (Math.PI / 180)) * velocity);
	    x_pos += XVelocity;
	    y_pos += YVelocity;
	    
	    if (isAlive())
	    {
	    	timeAlive -= 1;
	    }
	}
	
	// returns a bullet when ufo shoots
	public Bullets shoot(Rocket ship)
	{
		if (shootDelay.getTimePassed() == 1)
		{
			shootDelay.resetTime();
			if (UFOType == 1)
			{
				// MAKE UFO AIM
				// SHOOT
				
				double shipangle;
				double yL = (Math.rint(ship.getYPos())) - getYPos();
				double xL = (Math.rint(ship.getXPos())) - getXPos() ;
				double rL = Math.rint(Math.sqrt(Math.pow(yL, 2) + Math.pow(xL,2)));
				if (rL == 0) rL = 0.01;
				shipangle = Math.asin(yL/rL);
				if (xL < 0 && yL >= 0)
					shipangle = Math.PI - Math.abs(shipangle);
				else if (xL < 0 && yL < 0)
					shipangle  = Math.PI + Math.abs(shipangle);
				
				shipangle = (Math.rint(shipangle * (180/Math.PI))); // convert radians to degrees by multiplying 180/pi
				
				return new Bullets("UFO", getXPos() + (WIDTH/2), getYPos() + (HEIGHT/2), 10, 0, 0, shipangle);
			}
			else 
			{
				// RANDOM ANGLE
				// SHOOT
				return new Bullets("UFO", getXPos() + (WIDTH/2), getYPos() + (HEIGHT/2), 10, 0, 0, rnd.nextInt(360 - (0) + 1) + (0));
			}
		}
		return null;
	}
	
	public boolean isAlive()
	{
		if (health <= 0 || timeAlive <= 0)
		{
			AngleChangeDelay.killThread();
			shootDelay.killThread();
			return false;
		}
		return true;
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
	
	public int getUFOType()
	{
		return this.UFOType;
	}
}
