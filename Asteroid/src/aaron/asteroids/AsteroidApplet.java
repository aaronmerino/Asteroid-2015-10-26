package aaron.asteroids;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * ASTEROIDS
 * 
 * @author Aaron Saliba Merino
 * @date 12.29.14
 * 
 * ******CONTROLS:
 *                W - FORWARD THRUST
 *                
 *                S - BACKWARD THRUST
 *                
 *                SPACE BAR - HYPER SPACE (will put the ship on a random location)
 *                
 *                MOUSE - TURN/AIM
 *                
 *                LEFT CLICK - SHOOT
 *                
 *                
 *                It takes 5 shots to destroy an asteroid
 *                
 *                It takes 5 shots to destroy a UFO
 *                
 *                Small UFO will aim and shoot
 *                
 *                Larger UFO will shoot at random directions
 *                
 *                UFO's spawn periodically every 25 seconds
 *                
 *                After 40 000 points, only the smaller UFO will spawn
 *                            
 */

public class AsteroidApplet extends Applet implements Runnable,KeyListener,MouseListener
{
	
	private static final long serialVersionUID = 1L;
	protected static Random rnd;
	private Image dbImage;
	private Graphics dbg; 
	private BufferedImage rocketLifeIMG;
	private Dimension screenSize; // dimensions of the screen monitor
	private PointerInfo mouse;
	private PlayerStat playerStat; // keeps track of scores,lives, and current level of player
	private int WIDTH;
	private int HEIGHT;
	
	private Rocket ship; // ship
	private HitBox[] shipHitBoxLeft; // LEFT HITBOX FOR SHIP
	private HitBox[] shipHitBoxRight; // RIGHT HITBOX FOR SHIP
	private boolean shipHittable; // IF HITBOX's ON OR OFF
	private boolean shipAllowControl; // IF PLAYER IS ALLOWED TO CONTROL SHIP
	private boolean shipHyperSpace; // IF TO SEND SHIP TO HYPERSPACE
	private Time shipHitDelay; // DELAY UNTIL SHIP IS ABLE TO COLLIDE WITH OBJECTS
	
	private boolean UFOAlive;
	private Time UFOSpawnDelay;
	
	private double lenFireF; // LENGTH OF THRUST GRAPHICS
	private double lenFireB; // LENGTH OF THRUST GRAPHICS
	private double angle; // DIRECTION OF SHIP
	private double mouseAngle;
	private boolean turn; // IF SHIP IS TURNING OR NOT
	private boolean keyPressed; // IF A KEY IS PRESSED eg. forward thrust
	private boolean keyForward; // IF forward thrust are pressed
	private boolean mouseShoot; 
	private int delayShoot; // how fast the bullets go
	private int spreadAmt; // bullet spread
	private int numberOfBullets; // number of bullets to shoot at once
	private int numberOfAsteroids;
	private int initnumberOfAsteroids; // starting amount of asteroids
	private double newAng;
	
	private ArrayList<Object> allObj; // store all objects that interact with each other
	private ArrayList<FX_Explosion> allFXExp; // store all effects (explosions)
	private ArrayList<FX_Dots[]> allFXDots; // store all effects (dots)
	
	public void init() 
	{
		rnd = new Random();
		playerStat = new PlayerStat();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		WIDTH = (int) screenSize.getWidth() - 100;
		HEIGHT = (int) screenSize.getHeight()  - 150;
		
		// LOAD IMAGES
		try {
			URL imgUrl = getClass().getClassLoader().getResource("images/rocketLife.png");
			rocketLifeIMG = ImageIO.read(imgUrl);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		// LOAD FONT
		try {
		     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace.ttf")));
		} catch (IOException|FontFormatException e) {
		     //Handle exception
		}
		
		ship = new Rocket();
		shipHitBoxLeft = new HitBox[8];		// HITBOX FOR LEFT SIDE OF SHIP
		shipHitBoxRight = new HitBox[8];	// HIT BOX FOR RIGHT SIDE OF SHIP
		shipHitDelay = new Time("delay time"); // TIME FOR SHIP HITBOX TO TURN ON
		shipHitDelay.start();				
		UFOSpawnDelay = new Time("UFOSpawn delay time"); // TIME FOR UFO HITBOX TO TURN ON
		UFOSpawnDelay.start();
		
		for (int i = 0; i < shipHitBoxLeft.length; i++)
		{
			shipHitBoxLeft[i] = new HitBox(0,0,4,4);
			shipHitBoxRight[i] = new HitBox(0,0,4,4);
		}
		
		allObj = new ArrayList<Object>();
		allFXExp = new ArrayList<FX_Explosion>();
		allFXDots = new ArrayList<FX_Dots[]>();
		
		newGame(); // new game
		
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setSize(WIDTH,HEIGHT);
	}
	
	public void newGame()
	{
		playerStat.reset();
		UFOSpawnDelay.resetTime();
		shipHitDelay.resetTime();
		
		allObj.clear();
		allObj.add(ship);
		initnumberOfAsteroids = 1;
		numberOfAsteroids = 1;
		allObj.add(new Asteroid(1,rnd.nextInt(WIDTH - (1) + 1) + (1),rnd.nextInt(HEIGHT - (1) + 1) + (1),2,rnd.nextInt(360 - (1) + 1) + (1)));
		
		ship.setXPos(WIDTH/2);
		ship.setYPos(HEIGHT/2);
		shipHittable = false;		// IF SHIP HITBOX IS ON	
		shipAllowControl = true;	// IF PLAYER IS ALOWED TO CONTROL SHIP
		shipHyperSpace = false;
		UFOAlive = false;
		keyPressed = false;
		keyForward = false;
		mouseShoot = false;
		delayShoot = 0;
		spreadAmt = 1; // BULLET SPREAD
		numberOfBullets = 1; // NUMBER OF BULLETS FIRED AT ONCE
		angle = 0;
		mouseAngle = 0;
		lenFireF = 0;
		lenFireB = 0;
		newAng = 0;
	}
	
	public void start() 
	{
		Thread th = new Thread (this);
		th.start(); 
	}
	
	public void stop() {}

	public void destroy() {}
	
	// GENERATE A NUMBER OF ASTEROIDS
	public void generateAsteroids(double x, double y) 
	{
		numberOfAsteroids = initnumberOfAsteroids;
		for (int i = 0, a = 360/initnumberOfAsteroids, aa = 0; i < initnumberOfAsteroids; i++, aa += a) 
		{
			allObj.add(new Asteroid(4, x, y, 2, aa + rnd.nextInt((120/initnumberOfAsteroids) - (-120/initnumberOfAsteroids) + 1) + (-120/initnumberOfAsteroids)));
		}
	}
	
	// SPAWN UFO ON A RANDOM LOCATION
	public void generateUFO() 
	{
		if (UFOSpawnDelay.getTimePassed() == 25 && UFOAlive != true) // 25
		{
			int x = ((rnd.nextInt(1 - (0) + 1) + (0)) * WIDTH) - 40; // random location
			int y = (rnd.nextInt(HEIGHT - (0) + 1) + (0));
			int angle = ((x/WIDTH) * 180) + (rnd.nextInt(60 - (-60) + 1) + (-60)); // random angle
			int UFOType; // 1 = small; 2 = large UFO
			
			// SPAWN ONLY SMALL UFO AFTER 40,000 points
			if (playerStat.getScore() >= 40000)
				UFOType = 1;
			else
				UFOType = (rnd.nextInt(2 - (1) + 1) + (1));
			
			int UFOVel = 2;
			if (UFOType == 1) 
				UFOVel = 4;
			
			UFOAlive = true;
			allObj.add(new UFOEnemy(UFOType, 100, x, y, UFOVel, angle));
		}
	}
	
	// FIND ANGLE POINTING TO CURSOR
	public void findMouseAngle() 
	{
		mouse = MouseInfo.getPointerInfo(); //mouse cursor
		double screenX = 0; // x location of frame on screen
		double screenY = 0; // y location of frame on screen
		try{
			screenX = this.getLocationOnScreen().x-4;
			screenY =  this.getLocationOnScreen().y-46;
		}catch (Exception ar){
			System.out.println(ar.getMessage());
		}
		//System.out.println(screenY + "-------------");
		double yL = mouse.getLocation().getY() - 50 - (Math.rint(ship.getYPos()) + screenY); // y difference for mouse and ship
		double xL = mouse.getLocation().getX() - 10 - (Math.rint(ship.getXPos()) + screenX); // x difference for mouse and ship
		double rL = Math.rint(Math.sqrt(Math.pow(yL, 2) + Math.pow(xL,2))); // length to find angle
		
		if (rL == 0) rL = 0.01; // cannot divide by 0; use number close to 0 i.e 0.01
		mouseAngle = Math.asin(yL/rL); // find angle
		System.out.println(mouseAngle);
		
		// use related acute angle to find real angle
		if (xL < 0 && yL >= 0)
			mouseAngle = Math.PI - Math.abs(mouseAngle);
		else if (xL < 0 && yL < 0)
			mouseAngle = Math.PI + Math.abs(mouseAngle);
	
		mouseAngle = Math.rint(mouseAngle * (180/Math.PI));

		if (angle != mouseAngle) turn = true; else turn = false;
	}
	
	// check ship collision
	public void checkShipHit(HitBox obj)
	{
		// RESET LOCATION OF SHIP
		if (!shipHittable) 
		{
			if (shipHitDelay.getTimePassed() == 3)
			{
				if (!shipAllowControl)
				{	ship.setXPos(WIDTH/2);
					ship.setYPos(HEIGHT/2);
				}
				shipAllowControl = true;
			}
			else if (shipHitDelay.getTimePassed() == 6)
			{
				shipHittable = true;
			}
		}
		
		// CHECK IF SHIP IS HIT BY ASTEROID OR BULLET
		for (int z = 0; z < shipHitBoxLeft.length; z++)
		{
			HitBox l = shipHitBoxLeft[z];
			HitBox r = shipHitBoxRight[z];
			if (shipHittable)
			{
				if (l.isTouched(obj) || r.isTouched(obj))
				{
					shipHittable = false;
					shipAllowControl = false;
					shipHitDelay.resetTime();
					playerStat.subLife();
					
					if (obj instanceof Asteroid) 
					{
						newAng = ship.getMovementAngle();
						if (Math.rint(ship.getVelocity()) <= 2) 
							newAng = ((Asteroid) obj).getAngle();
						((Asteroid) obj).doDmg(500);
					}
					else if (obj instanceof UFOEnemy)
					{
						((UFOEnemy) obj).doDmg(500);
					}
					else if (obj instanceof Bullets)
					{
						allObj.remove(obj);
					}
					
					FX_Explosion exp = (new FX_Explosion(8,5,2,ship.getXPos(), ship.getYPos(),new Color(250,250,250)));
					allFXExp.add(exp);
					allFXDots.add(exp.explode());
					
					ship.setXPos(9999);
					ship.setYPos(9999);
					ship.setForce(0);
					ship.setdAddX(0);
					ship.setdAddY(0);
				}
			}
		}
	}
	
	public void run() 
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY); 
		while (true)
		{
			// MAKE SHIP SHOOT BULLETS
			allObj.trimToSize();
			if (mouseShoot && shipAllowControl)
			{
				shipHittable = true;
				delayShoot++;
				
				if (delayShoot > 6) // HOW FAST TO SHOOT
				{	
					int spread = rnd.nextInt(spreadAmt - (-spreadAmt) + 1) + (-spreadAmt); // random spread amount
					if (spreadAmt <= 3) spreadAmt++;
					delayShoot = 0;
					
					// number of bullets to shoot at once
					for (int i = 1,ang = 0,a = -1; i <= numberOfBullets; i++) // make bullets 
					{
						if (i % 2 == 0) ang+=5; // how wide each bullet is from each other
						allObj.add(new Bullets("SHIP",(int)ship.getPtFront().getX(),(int)ship.getPtFront().getY(), 15, (int)ship.getdAddX(), (int)ship.getdAddY(), ship.getAngle() + (ang*a) + spread));
						a*=-1;
					}
				}
			}else{
				spreadAmt = 1; // SPREAD OF BULLET
			}
			
			// SEND SHIP TO HYPERSPACE!
			if (shipHyperSpace)
			{
				if (shipHitDelay.getTimePassed() == 3)
				{
					ship.setXPos(rnd.nextInt(WIDTH - (1) + 1) + (1));
					ship.setYPos(rnd.nextInt(HEIGHT - (1) + 1) + (1));
					shipAllowControl = true;
					shipHittable = true;
					shipHyperSpace = false;
				}
			}
			
// ********** KEEP TRACK OF ALL OBJECTS IN GAME **********
			for (int i = 0; i < allObj.size(); i++)
			{
				Locateable obj = (Locateable)allObj.get(i);
				if (obj instanceof Bullets)
				{
					Bullets b = (Bullets) obj;
					b.Move();
					if (b.getTypeB().equals("UFO"))
						checkShipHit(b); // check if ufo bullet hits ship
					
					if (b.isAlive() != true){
						allObj.remove(b);
					} 
				}
				else if (obj instanceof Asteroid)
				{
					Asteroid as = (Asteroid) obj;
					newAng = as.getAngle();
					as.setFillorNoFill(false);
					as.Move();
					
					// CHECK IF ASTEROID IS HIT BY BULLET
					for (int a = 0; a < allObj.size(); a++)
					{
						if (allObj.get(a) instanceof Bullets)
						{
							Bullets b = (Bullets) allObj.get(a);
							if (as.isTouched(b))
							{
								FX_Explosion exp = (new FX_Explosion(3,3,1,b.getXPos() + (b.getWidth()/2), b.getYPos() + (b.getHeight()/2),new Color(10,200,250)));
								allFXExp.add(exp);
								allFXDots.add(exp.explode());
								as.doDmg(b.getDamage());
								if (((Bullets) b).getTypeB().equals("SHIP") && !as.isAlive())
								{
									// ADD TO SCORE DEPENDING ON THE ASTEROID SIZE
									switch(as.getRockSize()) 
									{
										case 1: playerStat.addScore(100); break;
										case 2: playerStat.addScore(50); break;
										case 3: playerStat.addScore(20); break;
										case 4: playerStat.addScore(10); break;
									}
								}
								as.setFillorNoFill(true);
								allObj.remove(b);
							} 
						}
					}
					checkShipHit(as); // check if ship is hit by asteroid
					
					// CHECK IF ASTEROID IS STILL ALIVE
					if (!as.isAlive())
					{
						FX_Explosion exp = (new FX_Explosion(8,4,2,as.getXPos() + (as.getWidth()/2), as.getYPos() + (as.getHeight()/2), new Color(10,250,250)));
						allFXExp.add(exp);
						allFXDots.add(exp.explode());
						
						if (as.getRockSize() > 1)
						{
							allObj.add(new Asteroid(as.getRockSize()-1,as.getXPos(),as.getYPos(),rnd.nextInt((3 - 2) + 1) + 2, newAng + (rnd.nextInt((30 - 10) + 1) + 10)));
							allObj.add(new Asteroid(as.getRockSize()-1,as.getXPos(),as.getYPos(),rnd.nextInt((3 - 2) + 1) + 2, newAng - (rnd.nextInt((30 - 10) + 1) + 10)));
							
							numberOfAsteroids += 2;
						}
						
						numberOfAsteroids -= 1;
						
						// if last asteroid
						if (numberOfAsteroids == 0)
						{
							initnumberOfAsteroids +=1;
							generateAsteroids(as.getXPos(),as.getYPos());
							playerStat.addLevel();
						}
						allObj.remove(as);
					}
				}
				else if (obj instanceof UFOEnemy)
				{
					UFOEnemy ufo = (UFOEnemy) obj;
					Bullets ufoB = ufo.shoot(ship);
					ufo.Move();
					if (ufoB != null)
					{
						allObj.add(ufoB);
					}
					
					// check for objects that has collided with UFO
					for (int a = 0; a < allObj.size(); a++)
					{
						if (!(allObj.get(a) instanceof UFOEnemy) && !(allObj.get(a) instanceof Rocket))
						{
							HitBox b = (HitBox) allObj.get(a);
							if (ufo.isTouched(b))
							{
								if (b instanceof Bullets)
								{
									if (((Bullets) b).getTypeB().equals("SHIP"))
									{
										FX_Explosion exp = (new FX_Explosion(4,3,1,b.getXPos() + (b.getWidth()/2), b.getYPos() + (b.getHeight()/2), new Color(10,200,000)));
										allFXExp.add(exp);
										allFXDots.add(exp.explode());
										ufo.doDmg(((Bullets) b).getDamage());
										if (!ufo.isAlive())
										{
											switch(ufo.getUFOType()) 
											{
												case 1: playerStat.addScore(1000); break;
												case 2: playerStat.addScore(200); break;
											}
										}
										
										allObj.remove(b);
									}
								} else { // else touched by asteroid
									((Asteroid) b).doDmg(500);
									ufo.doDmg(400);
								}
							} 
						}
					}
					
					checkShipHit(ufo); // check if ship is hit by ufo
					
					// check if ufo is still alive
					if (!ufo.isAlive()){
						FX_Explosion exp = (new FX_Explosion(8,5,2,ufo.getXPos() + (ufo.getWidth()/2), ufo.getYPos() + (ufo.getHeight()/2), new Color(10,200,000)));
						allFXExp.add(exp);
						allFXDots.add(exp.explode());
						allObj.remove(ufo);
						UFOSpawnDelay.resetTime();
						UFOAlive = false;
					} 
				}
				
				// loop location
				if (obj.getXPos() > WIDTH + 30){
					obj.setXPos(0 - 25);
				} else if (obj.getXPos() < 0 - 30){
					obj.setXPos(WIDTH + 25);
				}
				
				if (obj.getYPos() > HEIGHT + 30){
					obj.setYPos(0 - 35);
				} else if (obj.getYPos() < 0 - 40){
					obj.setYPos(HEIGHT + 25);
				}
				
			}// END FOR LOOP
			
			// delete finished explosions
			for (int i = 0; i < allFXExp.size(); i++)
			{
				allFXExp.get(i).Move();
				if (!allFXExp.get(i).isAlive())
				{
					allFXExp.remove(i);
					allFXDots.remove(i);
				}
				
			}
				
			// LENGTH OF THRUSTER GRAPHICS
			if (keyPressed && shipAllowControl){
				if (keyForward){
					if (lenFireF <= 25)lenFireF += 0.5;
				}else{
					if (lenFireB <= 8)lenFireB += 0.5;
				}	
			}else{
				lenFireF = 4;
				lenFireB = 2;
			}
			
			findMouseAngle();
			if (turn){ // delete this if statement
				angle = mouseAngle;
		        angle %= 360;
		    }
			
			// after death allow ship to move
			if (shipAllowControl)
			{
				ship.setVXInitial(ship.getdAddX());
				ship.setVYInitial(ship.getdAddY());
				ship.setAngle(angle);
				ship.Move(keyPressed);
			}
			
			// ATTACH HITBOXES TO SHIP
			int lenOfLine = 40;
			for (int i = 0, b = lenOfLine/(shipHitBoxLeft.length-1); i < shipHitBoxLeft.length; i++) 
			{
				shipHitBoxLeft[i].setXPos(((int) (Math.cos((angle-10) * (Math.PI/180)) * (i*b)) + ship.getPt1RightLine().getX()) );
				shipHitBoxLeft[i].setYPos(((int) (Math.sin((angle-10) * (Math.PI/180)) * (i*b)) + ship.getPt1RightLine().getY()) );
				shipHitBoxRight[i].setXPos(((int) (Math.cos((angle+10) * (Math.PI/180)) * (i*b)) + ship.getPt1LeftLine().getX()) );
				shipHitBoxRight[i].setYPos(((int) (Math.sin((angle+10) * (Math.PI/180)) * (i*b)) + ship.getPt1LeftLine().getY()) );
			}
			
			try { 
				Thread.sleep(19);
			}catch (InterruptedException ex){ 
				System.out.println(ex.getMessage());
			}
			
			// SPAWN A NEW UFO IF NUMBER OF ASTEROIDS ARE GREATER THAN 2
			if (numberOfAsteroids > 2)
				generateUFO();
			else 
				UFOSpawnDelay.resetTime();
			
			// CHECK IF GAME OVER
			if (playerStat.getLives() <= 0 && shipHitDelay.getTimePassed() == 3)
				newGame();
			
			repaint();
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY); 
		}
	}
	
	public void update (Graphics g) 
	{ // initialize buffer 
		if (dbImage == null){ 
			dbImage = createImage(WIDTH, HEIGHT); 
			dbg = dbImage.getGraphics (); 
		} 

		// clear screen in rocketLifeIMG 
		dbg.setColor(new Color(50,50,50)); 
		dbg.fillRect(0, 0, WIDTH, HEIGHT); 
		
		
		// draw elements in dbg 
		dbg.setColor(getForeground()); 
		paint (dbg); 

		// draw image on the screen 
		g.drawImage (dbImage, 0, 0, this); 
	} 
	
	public void paint (Graphics og) 
	{ 
		Graphics2D g = (Graphics2D) og;
		Color shipColor = null;
		
		if(shipHittable)
			shipColor = new Color(230,230,230);
		else
			shipColor = new Color(000,250,000);
		
		g.setColor (shipColor);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints. KEY_DITHERING ,RenderingHints.VALUE_DITHER_ENABLE);
		
		
		// DRAW LIVES
		for (int l = 0, a = 5; l < playerStat.getLives(); l++, a+=5)
		{
			g.drawImage(rocketLifeIMG, 130 + (25 * l) + a,0 + 90,130 + (18 + (25 * l)) + a,35 + 90, 0, 0, 17, 35, this);
		}
		g.setFont(new Font("Hyperspace", Font.BOLD, 26)); 
		g.drawString("SCORE: " + playerStat.getScore(), 133, 152);
		g.drawString("HIGH SCORE: " + playerStat.getHighScore(), 133, 176);
		// ***************************************************
		
		
		// DRAW CONTROLS
		g.setFont(new Font("Hyperspace", Font.BOLD, 14)); 
		g.setColor(new Color(255,115,000));
		g.drawString("CONTROLS:", 133, HEIGHT - 120);
		g.setColor(new Color(255,115,50));
		g.drawString("W - FORWARD THRUST", 133, HEIGHT - 100);
		g.drawString("S - BACKWARD THRUST", 133, HEIGHT - 80);
		g.drawString("SPACE BAR - HYPER SPACE", 133, HEIGHT - 60);
		g.drawString("MOUSE - TURN/AIM", 133, HEIGHT - 40);
		g.drawString("LEFT CLICK - SHOOT", 133, HEIGHT - 20);
		// ***************************************************
		
		
		// DRAW SHIP
		g.setColor (shipColor);
		g.drawOval ((int)ship.getXPos(), (int)ship.getYPos(), 8, 8); 
		
		g.drawLine(
				(int)ship.getXPos() + 4, (int)ship.getYPos() + 4, 
				(int)ship.getPtFront().getX(), (int)ship.getPtFront().getY());
		g.drawLine(
				(int)ship.getPt1RightLine().getX(), (int)ship.getPt1RightLine().getY(), 
				(int)ship.getPtFront().getX(), (int)ship.getPtFront().getY());
		g.drawLine(
				(int)ship.getPt1LeftLine().getX(), (int)ship.getPt1LeftLine().getY(), 
				(int)ship.getPtFront().getX(), (int)ship.getPtFront().getY());
		// ***************************************************
		
		
		// DRAW THOSE THRUSTERS THINGS
		if (keyPressed)
		{
			g.setColor(new Color(10,200,220));
			if (keyForward)
			{
				g.drawLine(
						(int)((Math.cos((angle-130) * (Math.PI / 180))) * (5)) + (int)ship.getXPos() + 4, 
						(int)((Math.sin((angle-130) * (Math.PI / 180))) * (5)) + (int)ship.getYPos() + 4, 
						(int)((Math.cos((angle-180) * (Math.PI / 180))) * (lenFireF)) + (int)ship.getXPos() + 4, // as lenFireF increases length of line
						(int)((Math.sin((angle-180) * (Math.PI / 180))) * (lenFireF)) + (int)ship.getYPos() + 4);
				
				g.drawLine(
						(int)((Math.cos((angle+130) * (Math.PI / 180))) * (5)) + (int)ship.getXPos() + 4, 
						(int)((Math.sin((angle+130) * (Math.PI / 180))) * (5)) + (int)ship.getYPos() + 4, 
						(int)((Math.cos((angle-180) * (Math.PI / 180))) * (lenFireF)) + (int)ship.getXPos() + 4, 
						(int)((Math.sin((angle-180) * (Math.PI / 180))) * (lenFireF)) + (int)ship.getYPos() + 4);
			} else {
				g.drawLine(
						(int) ((Math.cos((angle-90) * (Math.PI / 180))) * (8)) + (int)ship.getXPos() + 4, 
						(int) ((Math.sin((angle-90) * (Math.PI / 180))) * (8)) + (int)ship.getYPos() + 4, 
						(int)((Math.cos((angle) * (Math.PI / 180))) * (lenFireB)) + (int)ship.getXPos() + 4 + (int) ((Math.cos((angle-90) * (Math.PI / 180))) * (12)), 
						(int)((Math.sin((angle) * (Math.PI / 180))) * (lenFireB)) + (int)ship.getYPos() + 4 + (int) ((Math.sin((angle-90) * (Math.PI / 180))) * (12)));
				g.drawLine(
						(int) ((Math.cos((angle+90) * (Math.PI / 180))) * (8)) + (int)ship.getXPos() + 4, 
						(int) ((Math.sin((angle+90) * (Math.PI / 180))) * (8)) + (int)ship.getYPos() + 4, 
						(int)((Math.cos((angle) * (Math.PI / 180))) * (lenFireB)) + (int)ship.getXPos() + 4 + (int) ((Math.cos((angle+90) * (Math.PI / 180))) * (12)), 
						(int)((Math.sin((angle) * (Math.PI / 180))) * (lenFireB)) + (int)ship.getYPos() + 4 + (int) ((Math.sin((angle+90) * (Math.PI / 180))) * (12)));
				
			}
		}
		// ****************************************************
		
		
		ArrayList<Bullets> lines = new ArrayList<Bullets>();
		
		// SHOW BULLETS AND ASTEROIDS
		for (int i = 0; i < allObj.size(); i++)
		{
			if (allObj.get(i) instanceof Bullets)
			{
				Bullets b = (Bullets) allObj.get(i);
				if (b.getTypeB().equals("UFO"))
					g.setColor(new Color(00,255,00));
				else
					g.setColor(new Color(250,250,250));
				
				lines.add(b);
				g.fillOval((int)b.getXPos(), (int)b.getYPos(), (int)b.getRadius()*2, (int)b.getRadius()*2);
			} 
			else if (allObj.get(i) instanceof Asteroid)
			{
				Asteroid b = (Asteroid) allObj.get(i);
				
				if (numberOfAsteroids > 1 || (numberOfAsteroids == 1 && ((Asteroid) allObj.get(i)).getRockSize() != 1)) 
				{
					g.setColor(new Color(10,200,220));
				} else { // if last asteroid remaining then make asteroid red and show next level
					g.setColor(new Color(200,000,020));
					g.setFont(new Font("Hyperspace", Font.BOLD, 28)); 
					g.drawString("LEVEL " + (playerStat.getLevel() + 1), (int)b.getXPos() - 42, (int)b.getYPos() - 10);
				}
				
				if (!b.getFillOrNoFill())
				{
					g.drawPolygon(b.getPolygonX(), b.getPolygonY(), 8);
					//g.drawRect((int)b.getXPos(), (int)b.getYPos(), (int)b.getWidth(), (int)b.getHeight());
				} else {
					g.fillPolygon(b.getPolygonX(), b.getPolygonY(), 8);
				}
				
			}
			else if (allObj.get(i) instanceof UFOEnemy) 
			{
				g.setColor(new Color(10,200,000));
				UFOEnemy b = (UFOEnemy) allObj.get(i);
				if (b.getUFOType() == 2)
					g.drawImage(rocketLifeIMG,(int)b.getXPos(), (int)b.getYPos(), (int)b.getXPos() + (int)b.getWidth(), (int)b.getYPos() + (int)b.getHeight(), 16, 0, 40, 15, this);
				else 
					g.drawImage(rocketLifeIMG,(int)b.getXPos(), (int)b.getYPos(), (int)b.getXPos() + (int)b.getWidth(), (int)b.getYPos() + (int)b.getHeight(), 16, 16, 31, 25, this);

			}
		}
		
	   for (int i = 0, counter = 0; i < lines.size(); i++)
		{
			if (counter == 1){
				counter = 0;
				g.drawLine((int)lines.get(i).getXPos(), (int)lines.get(i).getYPos(), (int)lines.get(i-1).getXPos(), (int)lines.get(i-1).getYPos());
			}
			counter++;
		}
		
		// DRAW EXPLOSIONS
		for (int i = 0; i < allFXDots.size(); i++)
		{
			for (int a = 0; a < allFXDots.get(i).length; a++)
			{
				g.setColor (allFXExp.get(i).getColor());
				g.drawRect((int)allFXDots.get(i)[a].getXPos(), (int)allFXDots.get(i)[a].getYPos(), (int)allFXDots.get(i)[a].getWidth(), (int)allFXDots.get(i)[a].getHeight());
			}
		}
	}
	
	// KEYBOARD PRESS
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_W) // forward thrust
		{
			keyPressed = true;
			keyForward = true;
			lenFireB = 2;
			ship.setForce(200);
			
		} 
		else if (e.getKeyCode() == KeyEvent.VK_S) // backward thrust
		{
			keyPressed = true;
			keyForward = false;
			lenFireF = 4;
			ship.setForce(-100);
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) // hyper space
		{
			if (shipHittable)
			{
				shipHyperSpace = true;
				shipHittable = false;
				shipAllowControl = false;
				//ship.setForce(0);
				ship.setdAddX(0);
				ship.setdAddY(0);
				ship.setXPos(9999);
				ship.setYPos(9999);
				shipHitDelay.resetTime();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S)
		{
			keyPressed = false;
			keyForward = false;
			ship.setForce(0);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			mouseShoot = true;
		} 
	}
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		mouseShoot = false;
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
