package aaron.asteroids;

public class Bullets extends HitBox
{
	private double radius;
	private double velocity; // velocity with direction
	private double Xvelocity; // x velocity
	private double Yvelocity; // y velocity
	private double vXInitial; // init x vel
	private double vYInitial; // init y vel
	private double angle; // direction to move
	
	private double timeAlive;
	private double dmg;
	private String typeB;
	
	
	public Bullets()
	{
		this("test",10,10,0,0,0,0);
	}
	
	public Bullets(String typeB, double x_pos, double y_pos, double velocity, double vXInitial, double vYInitial, double angle)
	{
		super(x_pos,y_pos,4,4);
		this.radius = WIDTH/2;
		this.typeB = typeB;
		this.velocity = velocity;
		this.vXInitial = vXInitial;
		this.vYInitial = vYInitial;
		this.angle = angle;
		
		if(typeB.equals("SHIP"))
			this.dmg = 40;
		else // UFO SHIP
			this.dmg = 400;
		this.timeAlive = 50;
	}
	
	public void Move()
	{
		Xvelocity = (Math.cos(angle * (Math.PI / 180)) * velocity);
		Yvelocity = (Math.sin(angle * (Math.PI / 180)) * velocity);
	    
	    x_pos += vXInitial + Xvelocity;
	    y_pos += vYInitial + Yvelocity;
	    
	    if (isAlive())
	    {
	    	timeAlive -= 1;
	    }
	}
	
	public boolean isAlive()
	{
		if (timeAlive <= 0) 
		{
			return false;
		}
		return true;
	}
	
	public double getDamage()
	{
		return this.dmg;
	}
	
	public String getTypeB()
	{
		return this.typeB;
	}
	
	public double getRadius()
	{
		return this.radius;
	}
	
	public double getAngle()
	{
		return this.angle;
	}
}
