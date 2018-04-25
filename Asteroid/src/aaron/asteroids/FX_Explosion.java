package aaron.asteroids;

import java.awt.Color;
import java.util.Random;

public class FX_Explosion implements Locateable
{
	private Random rnd;
	private FX_Dots[] emp;
	private Time lifeTime;
	private Color color;
	private double x_pos;
	private double y_pos;
	private int amt;
	private int thickness;
	private int velocity;
	
	
	public FX_Explosion(int amt, int thickness,int velocity, double x, double y, Color color)
	{
		this.amt = amt;
		this.thickness = thickness;
		this.velocity = velocity;
		this.x_pos = x;
		this.y_pos = y;
		this.color = color;
		this.rnd = new Random();
		this.lifeTime = new Time("Life Time");
		this.lifeTime.start();
	}

	public FX_Dots[] explode()
	{	
		emp = new FX_Dots[amt];
		for (int i = 0, a = 360/amt, aa = 0; i < amt; i++, aa += a) 
		{
			emp[i] = new FX_Dots(
					x_pos, 
					y_pos, 
					thickness, // W
					thickness, // H
					aa + rnd.nextInt((120/amt) - (-120/amt) + 1) + (-120/amt), // ANGLE
					velocity); // VELOCITY
		}
		
		// DONT MAKE TOO MANY THREADS! “java.lang.OutOfMemoryError : unable to create new native Thread”
		/*Thread t = new Thread() 
		{
			public void run ()
			{
				while(FX_Explosion.this.isAlive())
				{
					for (int i = 0; i < emp.length; i++)
					{
						emp[i].Move();
					}
					
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
					}
					//System.out.println(Thread.currentThread());
				}
			}
		};
		t.start();*/
		return emp;
	}
	
	public void Move()
	{
		for (int i = 0; i < emp.length; i++)
		{
			emp[i].Move();
		}
	}
	
	public boolean isAlive()
	{
		if (lifeTime.getTimePassed() >= 2)
		{
			lifeTime.killThread();
			return false;
		}
		
		return true;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	@Override
	public double getXPos() {
		// TODO Auto-generated method stub
		return this.x_pos;
	}

	@Override
	public double getYPos() {
		// TODO Auto-generated method stub
		return this.y_pos;
	}
	
	@Override
	public void setXPos(double x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setYPos(double y) {
		// TODO Auto-generated method stub
		
	}
}
