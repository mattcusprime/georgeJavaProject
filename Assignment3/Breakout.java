/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;
	/** Diameter of the ball in pixels */
	private static final int BALL_DIAM = BALL_RADIUS*2;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Ball starting x-coordinate */
	private static final int X_START = (WIDTH/2)-BALL_RADIUS;	
	
/** Ball starting y-coordinate */
	private static final int Y_START = (HEIGHT/2)-BALL_RADIUS;	
	
/** Starting # of bricks */	
	private static final int B_START = NBRICK_ROWS*NBRICKS_PER_ROW;
	
/**Animation cycle delay */
	private static final int DELAY = 20;

	
/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		gameSetup();
		playGame();
		}
		
/** creates the bricks, creates the paddle, starts the score tracker, starts the brick counter. */
	public void gameSetup(){
		bricksSetup();
		createTracker();
		startCounter();
		createPaddle();
	}
	
/** Sets up the bricks with colors. */
		private void bricksSetup(){			
				/**xStart is the x coordinate for the first brick on the base row */	
			double xStart = (double) (getWidth() - ((NBRICKS_PER_ROW -1) * BRICK_SEP) - (NBRICKS_PER_ROW*BRICK_WIDTH))/2;
				/**yStart is the y coordinate for the first brick on the base row */
			double yStart = (double) (BRICK_Y_OFFSET + (BRICK_HEIGHT * (NBRICK_ROWS - 1))+((NBRICK_ROWS -1)*BRICK_SEP));
			for (int i=0; i<10; i++){
				for (int j=0; j < NBRICKS_PER_ROW; j++){
					double x = xStart + (BRICK_WIDTH + BRICK_SEP)*j;
					double y = yStart - (BRICK_HEIGHT + BRICK_SEP)*i;
					G3DRect brick = new G3DRect(x,y,BRICK_WIDTH, BRICK_HEIGHT);					
					switch (i){
						case 0: brick.setColor(Color.CYAN);break;
						case 1: brick.setColor(Color.CYAN);break;
						case 2: brick.setColor(Color.GREEN);break;
						case 3: brick.setColor(Color.GREEN);break;
						case 4: brick.setColor(Color.YELLOW);break;
						case 5: brick.setColor(Color.YELLOW);break;
						case 6: brick.setColor(Color.ORANGE);break;
						case 7: brick.setColor(Color.ORANGE);break;
						case 8: brick.setColor(Color.RED);break;
						case 9: brick.setColor(Color.RED);break;
						default: break;
					}
					brick.setFilled(true);
					add(brick);
				}
			}
		}
	
/**this section starts the brick counter, score counter, and turns counter. 
 * declaring counter and turnsleft to change value between methods  **/
		private int counter;
		private int turnsleft;
		
		private void startCounter(){
		counter = B_START;
		turnsleft = NTURNS;
		}
		
/**this section creates the score tracker 25 pixels below the paddle*/
		private GLabel label;
		private int score = 0;
		
		private void createTracker(){
			label = new GLabel("Score: " + score);
			add(label, (getWidth()-PADDLE_WIDTH)/2, getHeight()-PADDLE_Y_OFFSET + 25);
		}
		
/**this section creates the paddle */
		private GRoundRect paddle;
		
		private void createPaddle(){
			paddle = new GRoundRect ((getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT,PADDLE_WIDTH,PADDLE_HEIGHT);
			paddle.setFilled(true);
			add(paddle);
			addMouseListeners();	
		}

		
/**this section assigns gobj to object clicked with mouse. */
		private GObject gobj;
		private GPoint last;
		
		public void mousePressed(MouseEvent e){
			last = new GPoint(e.getPoint());
			gobj = getElementAt(last);
		}
/**this second sets parameters for dragging the paddle back and forth with the mouse*/
		public void mouseDragged(MouseEvent e){
				/**moves the paddle with the mouse on the x-axis only */
			if (gobj == paddle){
				double xChange = 0;
				/**keeps the paddle from going thru the left wall */
				if(gobj.getX()+ e.getX() - last.getX()<= 0){
					xChange = 0;
				/**keeps the paddle from going thru the right wall */
				} else if(gobj.getX()+e.getX() - last.getX()>=getWidth()-PADDLE_WIDTH){
					xChange = 0;
				/**calculates paddle movement within the walls */
				} else{
					xChange = e.getX() - last.getX();
				}
				gobj.move(xChange,0);
				label.move(xChange,0);
				last = new GPoint(e.getPoint());
			}
		}

	private GLabel message;
	private GLabel clicknext;
	
	public void playGame(){
		/**if there are turns left and bricks left, start the turn**/
		if(turnsleft>0 && counter>0) {
			createBall();
			turnsMessages();
			removeMessages();
			playTurn();
		/**if there are no turns left, end the game and set up for replay.**/
		} else if (turnsleft ==0){
			loseMessages();
			score = 0;
			removeAll();
			gameSetup();
			playGame();
		}
	}
	/**messages that appear before the turns **/
				
		private void turnsMessages(){
		/**message displays the number of turns left and directs the user to click to start **/
			message = new GLabel(turnsleft + " turn" + (turnsleft == 1 ? "" : "s")+ " left.");
			clicknext = new GLabel("Click anywhere to start. Good luck!");
		/**double mx, my is the set point for the message label. It is centered in the x-axis,
		* and two ball diameters below the ball starting point on the y-axis.	**/
			double mx = ( getWidth() - message.getWidth() ) /2;
			double my = ( HEIGHT/2 + BALL_DIAM*2 );
		/**double cx, cy is the set point for the clicknext label. It is centered in the x-axis,
		* and three ball diameters below the ball starting point on the y-axis. **/
			double cx = ( getWidth() - clicknext.getWidth() ) /2;
			double cy = ( HEIGHT/2 + BALL_DIAM*3);
			add(message, mx, my);
			add(clicknext, cx, cy);
			waitForClick();
		}
		
		private void winMessages(){
		/**message displays winning message and directs the user to click for new game **/
			message = new GLabel("YOU WON! CONGRATS!");
			clicknext = new GLabel("Click anywhere to start a new game.");
		/**double mx, my is the set point for the message label. It is centered in the x-axis,
		* and two ball diameters below the ball starting point on the y-axis.	**/
			double mx = ( getWidth() - message.getWidth() ) /2;
			double my = ( HEIGHT/2 + BALL_DIAM*2 );
		/**double cx, cy is the set point for the clicknext label. It is centered in the x-axis,
		* and three ball diameters below the ball starting point on the y-axis. **/
			double cx = ( getWidth() - clicknext.getWidth() ) /2;
			double cy = ( HEIGHT/2 + BALL_DIAM*3);			
			add(message, mx, my);
			add(clicknext, cx, cy);
			waitForClick();
		}
		
		private void loseMessages(){
		/**message displays losing message and directs the user to click for new game **/
			message = new GLabel("YOU LOSE :(");
			clicknext = new GLabel("Click anywhere to start a new game.");
		/**double mx, my is the set point for the message label. It is centered in the x-axis,
		* and two ball diameters below the ball starting point on the y-axis.	**/
			double mx = ( getWidth() - message.getWidth() ) /2;
			double my = ( HEIGHT/2 + BALL_DIAM*2 );
		/**double cx, cy is the set point for the clicknext label. It is centered in the x-axis,
		* and three ball diameters below the ball starting point on the y-axis. **/
			double cx = ( getWidth() - clicknext.getWidth() ) /2;
			double cy = ( HEIGHT/2 + BALL_DIAM*3);			
			add(message, mx, my);
			add(clicknext, cx, cy);
			waitForClick();
			}
		
		private void removeMessages(){
			remove (message);
			remove (clicknext);
		}
		
	/** this section creates the ball */
		private GOval ball;
		
		private void createBall(){
			ball = new GOval(X_START, Y_START, BALL_RADIUS*2, BALL_RADIUS*2);
			ball.setFilled(true);
			add(ball);
		}
		
	/**vx is change in ball's x-coordinate, vy is change in ball's y-coordinate.**/
		private double vx, vy;
		
		
	/** creates the random generator as rgen. **/
		private RandomGenerator rgen = RandomGenerator.getInstance();
				
		private void playTurn(){
		/**sets initial vy at 3.0, and doubles it after 7 bricks are removed.**/
		
			if (counter <=B_START-7){
				vy = 6.0;
			}else {
				vy = 3.0;
			}
			
		/** Randomizes the vx between -3.0 and 3.0. **/
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)){ 
				vx = -vx;
				}
		/**while ball is within the bounds, move (and bounce) the ball, and check for collisions.**/
			while (ball.getY()<getHeight()&&ball.getX()<getWidth()){
				pause(DELAY);
				moveBall();
				checkForCollisions();
			}
		}
		
			    
		private void moveBall(){
			double bx = ball.getX();
		    double by = ball.getY();
		    
		    /**If ball falls through floor, remove ball, deduct one turn, and start game at next turn. **/
		    if (by > getHeight() - BALL_DIAM){
		    	remove (ball);
		    	turnsleft--;
		    	pause(DELAY);
		    	playGame();
		    	}
		    /**if ball hits the either wall, bounce in opposite direction. **/
		    if(bx < 0 || bx > getWidth() - BALL_DIAM){
				vx = -vx;
		    	}
		    /**if ball hits the ceiling, bounce down. **/
			if (by < 0 ){
		    	vy = -vy;
				}	
			/**move the ball after change in direction above**/
		    ball.move(vx, vy);
			}
		
		private GObject getCollidingObject(){
			/** returns the object that collides with the four  corners of the square enclosing the ball.
			 * ballX and ballY are the X and Y coordinates of the upper left hand corner of the square.
			 * the other 3 corners are found by adding the BALL_DIAM to the upper left X and Y coordinates**/
			double ballX = ball.getX();
			double ballY = ball.getY();
			
			/** returns colliding object with upper left ball corner **/
			if(getElementAt(ballX,ballY)!=null){
				return getElementAt(ballX,ballY);
			}
			/** returns colliding object with upper right ball corner */
			else if(getElementAt(ballX +BALL_DIAM, ballY)!=null){
				return getElementAt(ballX+BALL_DIAM,ballY);
			}
			/** returns colliding object with lower left ball corner */
			else if(getElementAt(ballX,ballY+BALL_DIAM)!=null){
				return getElementAt(ballX,ballY+BALL_DIAM);
			}
			/** returns colliding object with lower right ball corner */
			else if(getElementAt(ballX+BALL_DIAM,ballY+BALL_DIAM)!=null){
				return getElementAt(ballX+BALL_DIAM,ballY+BALL_DIAM);
			}
			/** returns null when there is no colliding object with the ball square. */
			else {
				return null;
			}
		}
		/**checks for Collisions with paddle and bricks */
		private void checkForCollisions(){
			GObject collider = getCollidingObject();
			/** if ball hits paddle, moves ball opposite direction */
				if (collider == paddle){
						vy = -vy;
						ball.move(vx,vy);
					
			/** if ball hits label, do nothing */
				} else if (collider == label){
				
			/** if ball hits brick, removes brick, moves ball opposite direction, decrements counter */
				} else if (collider !=null){
					remove (collider);
					counter--;
					score = B_START - counter;
					label.setLabel("Score: " + score);
					vy = -vy;
					if (counter == B_START-7){
						vy = vy*2;
					}
					ball.move(vx,vy);
				}
			/** if all bricks are removed, "You won!" message, click to start again.**/
				if (counter == 0){
					winMessages();
					score = 0;
					removeAll();
					gameSetup();
					playGame();
				}
			}
	
	}
		
	
