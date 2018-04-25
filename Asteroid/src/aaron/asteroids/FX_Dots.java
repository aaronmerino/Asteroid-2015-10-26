package aaron.asteroids;

public class FX_Dots implements Locateable
{
	private double x_pos;
	private double y_pos;
	private double velocity;
	private double XVelocity;
	private double YVelocity;
	private double angle;
	private double WIDTH;
	private double HEIGHT;
	
	
	public FX_Dots(double x, double y, double width, double height, double angle, double velocity)
	{
		this.x_pos = x;
		this.y_pos = y;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.angle = angle;
		this.velocity = velocity;
	}
	
	public void Move()
	{
		XVelocity = (Math.cos((angle) * (Math.PI / 180)) * velocity);
	    YVelocity = (Math.sin((angle) * (Math.PI / 180)) * velocity);
	    decWidth(0.1);
	    decHeight(0.1);
	    
	    x_pos += XVelocity;
	    y_pos += YVelocity;
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
	
	public double getWidth()
	{
		return this.WIDTH;
	}
	
	public double getHeight()
	{
		return this.HEIGHT;
	}
	
	public void decWidth(double amt)
	{
		this.WIDTH -= amt;
	}
	
	public void decHeight(double amt)
	{
		this.HEIGHT -= amt;
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
