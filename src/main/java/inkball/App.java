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

    public static final int FPS = 60;

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

    

    //pause
    boolean paused;

    //score
    int score;



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

        board = new Board(BOARD_WIDTH, BOARD_HEIGHT); // Initialize the board
        board.loadLevelFromJson(configPath);
        
        timerDuration = board.getTimeForApp(); // duration
        timeRemaining = timerDuration -1; // countdown
        timerRunning = true;
        lastMillis = millis();
        board.addBalls();


    }

	@Override
    public void keyPressed(KeyEvent event){
        if (key == 'r' || key == 'R'){
            resetGame();
        }else if(key == ' '){
            if(!paused){
                paused = true;
            }else{
                paused = false;
            }
        }
    }

	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        System.out.println("Mouse pressed at: " + mouseX + ", " + mouseY);
        if(mouseButton == LEFT && timerRunning) canDraw = true;


    }
	
    @Override
    public void mouseDragged(MouseEvent e) {
        PVector currentPoint = new PVector(mouseX, mouseY);
        
        if (lastPoint != null && mouseButton == LEFT && timerRunning) {
            // Create a new line segment from the last point to the current point
            drawnLines.add(new Line(lastPoint.copy(), currentPoint.copy())); // Store the line
            stroke(0); // Set stroke color to black
            strokeWeight(10); // Set the line thickness
            line(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
        }
        
        lastPoint = currentPoint; // Update lastPoint to current
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        canDraw = false;
        lastPoint = null;
        System.out.println("Mouse released at: " + mouseX + ", " + mouseY);
		
    }


    /**
     * Draw all elements in the game by current frame.
     */

     public void ballCheck() {
        currentBalls = board.getBalls(); // Assuming this returns a copy or a reference that can be safely iterated over
        score = 0;
    
        // Use an Iterator to avoid ConcurrentModificationException
        Iterator<Ball> iterator = currentBalls.iterator();
        
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.draw(this, (int) ball.posX, (int) ball.posY);
            
            if (!paused && timerRunning) {
                ball.move();
                ball.bounceOffBoundary(board);
                score += ball.getScore();
            }
            
            // Check if the ball should shrink instead of being removed
            if (!ball.notSet) {
                ball.shrink(); // Gradually shrink the ball
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
        text("Timer: "+ timeRemaining, 450, TOPBAR/2);
        text("Score: "+ score, 450, TOPBAR);
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


    

    // Method to reset the game state
    public void resetGame() {
        board.loadLevelFromJson(configPath); // Reload board from JSON file
        board.resetBalls();                  // Reset ball positions, velocities, etc.
        
        // Reset other game variables
        timeRemaining = timerDuration - 1;   // Reset timer
        timerRunning = true;                 // Restart timer
        lastMillis = millis();               // Update lastMillis to current time
        
        // Reset game-related variables
        canDraw = false;                     // Reset drawing state
        // score reset
    }

    public void gameOver(){
        textSize(28);
        text("=== TIME'S UP === ", 150,TOPBAR);
    }



   


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
