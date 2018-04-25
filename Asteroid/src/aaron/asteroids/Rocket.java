package aaron.asteroids;

import java.awt.Point;

public class Rocket implements Locateable
{
	private double t; // time
	private double F; // F = m*a
	private double m; // mass
	private double a; // acceleration
	private double aX; // x acceleration
	private double aY; // y acceleration

	private double x_pos; // x position
	private double y_pos; // y position

	private double dAddX; // x velocity to add
	private double dAddY; // y velocity to add

	private double vXInitial; // init x velocity
	private double vYInitial; // init y velocity 

	private double angle; // direction to move


	public Rocket()
	{
		this(270, 			    // angle
				0, 				// x_pos
				0, 				// y_pos
				0,   			// Force
				400, 			// mass
				0);  			// acceleration;
	}

	public Rocket(double angle, double x_pos, double y_pos, double f, double m, double a) 
	{
		this.angle = angle;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.F = f;
		this.m = m;
		this.a = a;
		this.t = 1;
	}
	
	// translate the ship
	public void Move(boolean keyPressed) 
	{
		double decAX = 0; // for decelerating the ship to a stop
		double decAY = 0;
		double decAngle = 0;

		a = F / m;
		aX = (Math.cos(angle * (Math.PI / 180)) * a);
		aY = (Math.sin(angle * (Math.PI / 180)) * a);
		
		// Decelerate ship to a stop
		if (!keyPressed)
		{
			if (getVelocity() > 0)
			{
				decAngle = getMovementAngle(); // get movement angle of ship and add 180 degrees or Math.PI radians;
				decAX = (Math.cos((decAngle * (Math.PI / 180)) + Math.PI) * 0.03);
				decAY = (Math.sin((decAngle * (Math.PI / 180)) + Math.PI) * 0.03);
			}
		}
		
		// calculate current velocity of ship\
		// t = 1 always for desired speed
		dAddX = ((vXInitial * t) + ((aX * (t*t)) / 2)) + ((decAX * (t*t)) / 2); // - ((vXInitial * (t - 1)) + ((aX * ((t - 1) * (t - 1))) / 2));
		dAddY = ((vYInitial * t) + ((aY * (t*t)) / 2)) + ((decAY * (t*t)) / 2); // - ((vYInitial * (t - 1)) + ((aY * ((t - 1) * (t - 1))) / 2));

		System.out.println(getVelocity());

		x_pos += dAddX;
		y_pos += dAddY;

		System.out.println(t + "::" + dAddX + "::" + dAddY);

		//t = 1;
	}
	
	//delete me
	public void setTime(double t)
	{
		this.t = t;
	}

	public void setAngle(double ang)
	{
		this.angle = ang;
	}
	
	public void setXPos(double x) 
	{
		this.x_pos = x;
	}

	public void setYPos(double y) 
	{
		this.y_pos = y;
	}

	public void setVXInitial(double vx)
	{
		this.vXInitial = vx;
	}

	public void setVYInitial(double vy) 
	{
		this.vYInitial = vy;
	}
	
	public void setdAddX(double dx)
	{
		this.dAddX = dx;
	}

	public void setdAddY(double dy) 
	{
		this.dAddY = dy;
	}

	public void setForce(double f) 
	{
		this.F = f;
	}

	public double getdAddX() 
	{
		return this.dAddX;
	}

	public double getdAddY() 
	{
		return this.dAddY;
	}

	public double getVelocity()
	{
		return Math.sqrt((getdAddX()*getdAddX()) + (getdAddY()*getdAddY()));
	}

	public double getXPos() 
	{
		return this.x_pos;
	}

	public double getYPos() 
	{
		return this.y_pos;
	}
	
	// getting the points for the right side of the ship
	public Point getPt1RightLine()
	{
		return new Point((int)((Math.cos((angle+120) * (Math.PI / 180))) * (10)) + (int)x_pos + 4, 
						 (int)((Math.sin((angle+120) * (Math.PI / 180))) * (10)) + (int)y_pos + 4);
	}
	
	// getting the points for the front of the ship
	public Point getPtFront()
	{
		return new Point((int)((Math.cos((angle) * (Math.PI / 180))) * (30)) + (int)x_pos + 4, 
				         (int)((Math.sin((angle) * (Math.PI / 180))) * (30)) + (int)y_pos + 4);
	}
	
	// getting the points for the left side of the ship
	public Point getPt1LeftLine()
	{
		return new Point((int)((Math.cos((angle-120) * (Math.PI / 180))) * (10)) + (int)x_pos + 4, 
					     (int)((Math.sin((angle-120) * (Math.PI / 180))) * (10)) + (int)y_pos + 4);
	}

	public double getAngle()
	{
		return this.angle;
	}
	
	// getting the angle of where the ship is moving
	// not same as pointer direction
	public double getMovementAngle()
	{
		double rr = getVelocity();
		double decAngle = 0;
		if (rr == 0) rr = 0.001;
		
		decAngle = Math.asin(dAddY / rr);

		if (dAddX < 0 && dAddY >= 0)
			decAngle = Math.PI - Math.abs(decAngle);
		else if (dAddX < 0 && dAddY < 0)
			decAngle = Math.PI + Math.abs(decAngle);
		
		return decAngle * (180 / Math.PI);
	}
	
	public double getForce()
	{
		return this.F;
	}
}
