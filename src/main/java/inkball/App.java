package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 120;

    public String configPath;
    //sprites
    public static PImage[] wallsprite;
    public static PImage[] ballsprite;
    public static PImage[] holesprite;
    public static PImage spawnsprite;
    public static PImage tilesprite;

    Board board; // Add board instance
    ArrayList<int[]> balls;
    ArrayList<Ball> currentBalls;

    //time
    private int timerDuration;
    private int timeRemaining;
    private boolean timerRunning; // Timer state
    private int lastMillis;

    //drawing in mouse
    boolean canDraw;
    ArrayList<Line> drawnLines = new ArrayList<>(); // Store all drawn lines
    PVector lastPoint; // To store the last drawn point

    //spawn rate
    int ballReleaseInterval;
    long lastBallReleaseTime;
    int ballsReleased;

    //pause
    boolean paused;

    //score
    int score = 0, currentLevelIndex = 0;
    boolean tr, one, nPressed;



    public App() {
        this.configPath = "config.json";
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

	@Override
    public void setup() {
        frameRate(FPS);
        loadSprites();

        //set-reset board
        board = new Board(BOARD_WIDTH, BOARD_HEIGHT, currentLevelIndex); // Initialize the board
        board.loadLevelFromJson(configPath);
        
        //set-reset time logic
        timerDuration = board.getTimeForApp(); // duration
        timeRemaining = timerDuration -1; // countdown
        timerRunning = true;
        lastMillis = millis();

        //set-reset balls
        board.addBalls();
        currentBalls = board.getBalls();
        ballReleaseInterval = board.getInterval();

    
        canDraw = false;                     // Set-Reset drawing state
    
        // Set-Reset ball spawning logic
        ballsReleased = 1;                   // Set-Reset released ball count
        lastBallReleaseTime = System.currentTimeMillis(); // Reset the ball release timer


    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (key == 'r' || key == 'R') {
            resetGame();
        } else if (key == ' ') {
            paused = !paused; // Toggle paused state
            
            // If the game is paused, stop the ball release timer
            if (paused) {
                lastBallReleaseTime = System.currentTimeMillis(); // Pause ball spawning
            } else {
                // On resume, adjust the ball release timing
                lastBallReleaseTime = System.currentTimeMillis() - (ballReleaseInterval - (System.currentTimeMillis() - lastBallReleaseTime));
            }
        }else if(key == 'n' || key == 'N'){
            goToNextlevel();
            setup();
        }
    }

	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        if(mouseButton == LEFT && timerRunning) canDraw = true;


    }
	
    @Override
    public void mouseDragged(MouseEvent e) {
        PVector currentPoint = new PVector(mouseX, mouseY);
        
        if (lastPoint != null && mouseButton == LEFT && timerRunning) {
            // Create a new line segment from the last point to the current point
            drawnLines.add(new Line(lastPoint.copy(), currentPoint.copy())); // Store the line
            stroke(0);
            strokeWeight(10);
            line(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
        }
        
        lastPoint = currentPoint; // Update lastPoint to current
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        canDraw = false;
        lastPoint = null;
		
    }



    
    
    public void ballCheck() {
        /*
          Resets the score for the current check and manages the release of new balls based on the interval.
          Draws each released ball, updates its movement, checks for collisions, and handles interactions with holes.
        */
    
        score = 0;  // Reset score for this check
        long currentTime = System.currentTimeMillis();
    
        // Release a new ball every [interval] seconds, if not paused
        if (!paused && ballsReleased < currentBalls.size() && currentTime - lastBallReleaseTime >= ballReleaseInterval) {
            lastBallReleaseTime = currentTime;
            ballsReleased++; // Increase the count of released balls
        }
    
        // Iterate only over released balls
        for (int i = 0; i < ballsReleased; i++) {
            Ball ball = currentBalls.get(i);
            stroke(255,0,0);
            strokeWeight(5);
            ball.draw(this, (int) ball.position.x, (int) ball.position.y);  // Draw the ball
            //point(ball.position.x, ball.position.y);
    
            // Update ball movement and check for collisions if the game is not paused
            if (!paused && timerRunning) {
                ball.move();
                score += ball.getScore();  // Update total score
            }
    
            // Check for collisions with walls
            for (int y = 0; y < board.height; y++) {
                for (int x = 0; x < board.width; x++) {
                    Tile tile = board.grid[y][x];
                    if (tile.hasContent() && tile.getContent() instanceof Wall) {
                        Wall wall = (Wall) tile.getContent();
                        ball.handleCollision(wall);
                    }
                }
            }
    
            // Check if the ball should shrink instead of being removed
            if (ball.notSet && ball.getSize() >= 0) {
                for (Hole hole : board.holes) {
                    PVector holeCenter = hole.getHoleCenter();  // Get the center of the hole
                    // Check if the ball is colliding with the hole
                    if (hole.checkCollision(ball)) {
                        ball.shrink(holeCenter);  // Pass the hole center to the shrink method
                        break;  // Exit the loop if the ball has interacted with a hole
                    }
                }
            }
        }
    }
    
    
    
    
    
     
    
    
    

	@Override
    public void draw() {
        background(225);
        board.draw(this);
        ballCheck();
        
        if (timerRunning && !paused) {
            int currentMillis = millis();
            if (currentMillis - lastMillis >= 1000) { // Check if one second has passed
                timeRemaining--; // Decrease time remaining by 1 second
                lastMillis = currentMillis; // Update the lastMillis
            }

            // Check if the timer has reached zero
            if (timeRemaining <= 0) {
                timerRunning = false; // Stop the timer
                timeRemaining = 0;
            }
        }
        fill(0);
        textSize(20);
        text("Timer: "+ timeRemaining, 450, TOPBAR);
        text("Score: "+ score, 450, TOPBAR/2);
        stroke(0);
        strokeWeight(10);
        if(timeRemaining == 0){
            gameOver();
        }
        if(paused){
            textSize(30);
            text(" *** PAUSED ***", 150,TOPBAR);
        }
        
        for (Line line : drawnLines) {
            line(line.start.x, line.start.y, line.end.x, line.end.y);
        }
        
        
		//----------------------------------
        //----------------------------------
		//display game end message

    }

    //sprite storage
    public void loadSprites(){
        //load the wall sprite
        wallsprite = new PImage[5];
        ballsprite = new PImage[5];
        holesprite = new PImage[5];

        //sapwner
        spawnsprite = loadImage("src/main/resources/inkball/entrypoint.png");
        
        //tile
        tilesprite = loadImage("src/main/resources/inkball/tile.png");
        if(tilesprite == null){
            println("Error: Tile.png could not be loaded.");
        }else{
            println("Tile.png loaded successfully.");
        }

        //actual wall
        for (int i = 0; i < wallsprite.length; i++) {
            wallsprite[i] = loadImage("src/main/resources/inkball/wall" + i + ".png");
            if (wallsprite[i] == null) {
                println("Error: wall" + i + ".png could not be loaded.");
            } else {
                println("wall" + i + ".png loaded successfully.");
            }
        }

        //hole
        for (int i = 0; i < holesprite.length; i++) {
            holesprite[i] = loadImage("src/main/resources/inkball/hole" + i + ".png");
            if (holesprite[i] == null) {
                println("Error: hole" + i + ".png could not be loaded.");
            } else {
                println("hole" + i + ".png loaded successfully.");
            }
        }
        
        // ball
        for (int i = 0; i < 5; i++) {
            ballsprite[i] = loadImage("src/main/resources/inkball/ball" + i + ".png");
        }
    }


    

    public void resetGame() {
        setup();
        score = 0; // necessary for reset logic and setup logic being responsible for levelup
        
        //board.loadLevelFromJson(configPath); // Reload board from JSON file
        // board.resetBalls();                  // Reset ball positions, velocities, etc.
    
        // // Reset other game variables
        // timeRemaining = timerDuration - 1;   // Reset timer
        // timerRunning = true;                 // Restart timer
        // lastMillis = millis();               // Update lastMillis to current time
    
        // canDraw = false;                     // Reset drawing state
    
        // // Reset ball spawning logic
        // ballsReleased = 0;                   // Reset released ball count
        // lastBallReleaseTime = System.currentTimeMillis(); // Reset the ball release timer

    }

    public void goToNextlevel(){
        currentLevelIndex++;
    }

    public void gameOver(){
        textSize(28);
        text("=== TIME'S UP === ", 150,TOPBAR);
    }
   


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
