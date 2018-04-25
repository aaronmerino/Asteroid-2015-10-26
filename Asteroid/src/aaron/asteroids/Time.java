package aaron.asteroids;

public class Time extends Thread
{
	private int timePassed;
	private boolean killThread;
	
	public Time(String name)
	{
		timePassed = 0;
		killThread = false;
		this.setName(name);
	}
	
	@Override
	public void run() 
	{
		while (!killThread)
		{
			timePassed+=1;
			
			try 
			{
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void killThread()
	{
		killThread = true;
	}
	
	public void resetTime()
	{
		timePassed = 0;
	}
	
	public void setTime(int time)
	{
		this.timePassed = time;
	}
	
	public int getTimePassed()
	{
		return timePassed;
	}

}
