import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import java.math.*;
import javax.swing.JPanel;

public class Gameplay extends JPanel implements KeyListener, ActionListener{
	private boolean play = false;
	private boolean win = false;
	private int score = 0;
	
	private int totalBricks = 21;
	
	private int newRow = 4;
	private int newCol = 8;
	
	private Timer timer;
	private int delay = 6;
	
	private int playerX = 310;
	
	private int ballposX = (int)(Math.random()*300+100);
	private int ballposY = 350;
	
	private int ballXdir = -1;
	private int ballYdir = -2;
	
	private MapGenerator map;
	
	public Gameplay() {
		map = new MapGenerator(3, 7);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();
		
	}
	
	public void paint(Graphics g) {
		// background
		g.setColor(Color.black);
		g.fillRect(1, 1, 692, 692);
		
		//
		map.draw((Graphics2D)g);
		
		//borders
		g.setColor(Color.gray);

		g.fillRect(0, 0, 3, 692);
		g.fillRect(0, 0, 691, 3);
		g.fillRect(691, 0, 3, 692);
		
		
		//scores
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString(""+score, 635, 30);
		
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 12));
		g.drawString("Press ESC to Pause The Game", 450, 20);
		
		//player paddle
		g.setColor(Color.gray);
		g.fillRect(playerX, 600, 100, 8);
		
		//ball
		g.setColor(Color.orange);
		g.fillOval(ballposX, ballposY, 20, 20);
		
		if(totalBricks <= 0) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.green);
			
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("You Won! Score: "+ score, 220, 300);
			
			g.setFont(new Font("serif", Font.BOLD, 20));
			g.drawString("Press Enter To Continue", 230, 350);
			
			win = true;
			
		}
		
		if(ballposY > 650) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.red);
			
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Game Over! Score: "+ score, 190, 300);
			
			g.setFont(new Font("serif", Font.BOLD, 20));
			g.drawString("Press Space To Restart", 230, 350);
			
		}
		
		g.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		timer.start();
		if(play) {
			
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 600, 100, 8))) {
				ballYdir = -ballYdir;
			}
			//First map is obj second map is array in MapGenerator class
			A: for(int i = 0; i <map.map.length; i++) {
				for(int j = 0; j<map.map[0].length; j++) {
					if(map.map[i][j] > 0) {
						int brickX = j* map.brickWidth + 80;
						int brickY = i* map.brickHeight + 40;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;
						
						
						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
						Rectangle brickRect = rect;
						
						if(ballRect.intersects(brickRect)) {
							map.setBrickValue(0, i, j);
							totalBricks--;
							score += 5;
							
							if(ballposX +19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
								ballXdir = -ballXdir;
							} else {
								ballYdir = -ballYdir;
							}
							//break label to break out of more than just the one for loop
							break A;
						}
					}
				}
				
			}
			
			ballposX+= ballXdir;
			ballposY+= ballYdir;
			if(ballposX < 0) {
				ballXdir = -ballXdir;
			}
			if(ballposY < 0) {
				ballYdir = -ballYdir;
			}
			if(ballposX > 670) {
				ballXdir = -ballXdir;
			}
		}
		repaint();
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(playerX >= 600) {
				playerX = 600;
			}else {
				moveRight();
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			if(playerX <= 10) {
				playerX = 10;
			}else {
				moveLeft();
			}
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			if(!play) {
				play = true;
				//create new starting point
				ballposX = (int)(Math.random()*100+100);
				//reset defaults
				ballposY = 350;
				ballXdir = -1;
				ballYdir = -2;
				playerX = 310;
				score = 0;
				totalBricks = 21;
				map = new MapGenerator(3, 7);
				
				//resets incase user made it past the first round, still use 3, 7 for map gen
				newRow = 4;
				newCol = 8;
				
				repaint();
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!play &&  win) {
				play = true;
				//reset win to false
				win = false;
				//create new starting position for ball
				ballposX = (int)(Math.random()*100+100);
				//reset defaults with a higher y speed
				ballposY = 300;
				ballXdir = -1;
				ballYdir = -3;
				playerX = 310;
				//make sure you have right total bricks to check for win
				totalBricks = newRow * newCol;
				//use new row and col to make a new map
				map = new MapGenerator(newRow, newCol);
				//increment incase the user wins again (reset to 4, 7 if user loses)
				newRow++;
				newCol++;
				repaint();
			}
		}
		//pause button
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(play == true) {
				play = false;
				
			}else {
				play = true;
			}
					
		}
	}

	public void moveRight() {
		play = true;
		playerX+=20;
	}
	public void moveLeft() {
		play = true;
		playerX-=20;
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}


}
