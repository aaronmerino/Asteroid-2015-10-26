package aaron.asteroids;

public class PlayerStat 
{
	
	private int score;
	private int highScore;
	private int lives;
	private int level;
	
	
	public PlayerStat()
	{
		this(0,4,0);
	}
	
	public PlayerStat(int score, int lives, int level)
	{
		this.score = score;
		this.highScore = score;
		this.lives = lives;
		this.level = level;
	}
	
	
	public void reset()
	{
		if (score > highScore)
			highScore = score;
		
		this.score = 0;
		this.lives = 4;
		this.level = 0;
	}
	
	public void addScore(int amount)
	{
		this.score += amount;
	}
	
	public void addLife()
	{
		this.lives += 1;
	}
	
	public void subLife()
	{
		this.lives -= 1;
	}
	
	public void addLevel()
	{
		this.level += 1;
	}
	
	
	
	public int getScore()
	{
		return this.score;
	}
	
	public int getHighScore()
	{
		return this.highScore;
	}
	
	public int getLives()
	{
		return this.lives;
	}
	
	public int getLevel()
	{
		return this.level;
	}

}
