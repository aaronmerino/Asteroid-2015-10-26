package aaron.asteroids;

public class HitBox implements Locateable
{
	public double x_pos;
	public double y_pos;
	public double WIDTH;
	public double HEIGHT;
	public boolean isAlive;
	
	
	public HitBox() 
	{
		
	}
	
	public HitBox(double x, double y, double width, double height)
	{
		this.x_pos = x;
		this.y_pos = y;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.isAlive = true;
	}
	
	public boolean isTouched(Object arg) 
	{	
		HitBox obj = (HitBox)arg;
		
		if (obj.getXPos() + obj.getWidth() >= getXPos() && obj.getXPos() <= getXPos() + getWidth()
		&&  obj.getYPos() + obj.getHeight() >= getYPos() && obj.getYPos() <= getYPos() + getHeight()){
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setXPos(double x) 
	{
		this.x_pos = x;
	}

	@Override
	public void setYPos(double y) 
	{
		this.y_pos = y;
	}
	
	public void setAlive(boolean alive) 
	{
		this.isAlive = alive;
	}
	
	@Override
	public double getXPos() 
	{
		return x_pos;
	}

	@Override
	public double getYPos() 
	{
		return y_pos;
	}
	
	public boolean getAlive() 
	{
		return isAlive;
	}
	
	public double getWidth() 
	{
		return WIDTH;
	}
	public double getHeight() {
		return HEIGHT;
	}
}
