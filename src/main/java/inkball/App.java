
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

    public static final int FPS = 30;

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
    boolean timerRunning; // Timer state
    private int lastMillis;

    //line
    boolean canDraw;
    ArrayList<Line> drawnLines = new ArrayList<>(); // Store all drawn lines
    ArrayList<ArrayList<Line>> allLines = new ArrayList<>();
    PVector lastPoint; // To store the last drawn point

    //spawn rate
    int ballReleaseInterval;
    long lastBallReleaseTime;
    int ballsReleased;
    int count = 0;

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
        

    
        //canDraw = false;                     // Set-Reset drawing state
    
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
                canDraw = false; // Stop drawing
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
        // Only allow drawing if the game is not paused
        if (mouseButton == LEFT && timerRunning && !paused) {
            canDraw = true;
        } else {
            canDraw = false; // Ensure canDraw is false if the game is paused or timer is not running
        }
    }

	
    @Override
    public void mouseDragged(MouseEvent e) {
        PVector currentPoint = new PVector(mouseX, mouseY);
        
        if (lastPoint != null && mouseButton == LEFT && timerRunning && !paused) {
            // Create a new line segment from the last point to the current point
            drawnLines.add(new Line(lastPoint.copy(), currentPoint.copy())); // Store the line segment
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
        
        // When the mouse is released, add the entire drawn line to allLines and clear drawnLines
        if (!drawnLines.isEmpty()) {
            allLines.add(new ArrayList<>(drawnLines)); // Add the entire line
        }
    }


    
    
    @SuppressWarnings("rawtypes")
    public void ballCheck() {
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
            ball.draw(this);  // Draw the ball
    
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
                        if((ball.colorIndex == hole.holeIndex || hole.holeIndex == 0 || ball.colorIndex == 0) && !ball.hit){
                            ball.scorePlus();
                            ball.hit = !ball.hit;
                        }else if (!ball.hit){
                            ball.scoreMinus();
                            ball.hit = !ball.hit;
                        }
                        
                        break;  // Exit the loop if the ball has interacted with a hole
                    }
                }
            }
            checkLevelEnd();

            // Check for collisions between the ball and the full lines

            Iterator<ArrayList<Line>> lineIterator = allLines.iterator();
            while (lineIterator.hasNext()) {
                ArrayList<Line> fullLine = lineIterator.next();
                boolean lineHit = false; // Track if any part of the line is hit

                for (Line line : fullLine) {
                    if (line.checkCollision(ball)) {
                        // If any part of the line is hit, mark it as hit and set velocity
                        PVector newVelocity = line.calculateNewVelocity(ball);
                        ball.velocity.set(newVelocity);
                        lineHit = true; // Mark the whole line as hit
                        break;
                    }
                }

                if (lineHit) {
                    lineIterator.remove(); // Remove the entire line if hit
                }
            }
            
        }
        
    }
    
    


	@Override
    public void draw() {
        background(225);
        rect(0, 32, CELLSIZE * currentBalls.size(), 32);
        board.draw(this);
        
        // Check if time has run out
        if (timeRemaining <= 0) {
            timerRunning = false; // Stop the timer
            timeRemaining = 0; // Ensure time remaining is set to zero
            gameOver(); // Display game over message

            // Move to the next level
            goToNextlevel();
            setup(); // Reset the game state for the new level
            return; // Exit the draw method to prevent further drawing in this frame
        }

        if (timerRunning) {
            ballCheck();
        }

        // Update timer logic
        if (timerRunning && !paused) {
            int currentMillis = millis();
            if (currentMillis - lastMillis >= 1000) { // Check if one second has passed
                timeRemaining--; // Decrease time remaining by 1 second
                lastMillis = currentMillis; // Update the lastMillis
            }
        }

        // Render timer and score
        fill(0);
        textSize(20);
        text("Timer: " + timeRemaining, 450, TOPBAR);
        text("Score: " + score, 450, TOPBAR / 2);

        // If the game is paused, display the paused message
        if (paused) {
            textSize(30);
            text(" *** PAUSED ***", 150, TOPBAR);
        }

        // // Draw the lines
        stroke(0);
        strokeWeight(10);
        for (Line line : drawnLines) {
            line.draw(this);
        }
        
    }

    public boolean allBallsHit(){
        for(Ball b : currentBalls){
            if(b.hit && !b.checked){
                count++;
                b.checked = true;
            }
        }
        if(count == currentBalls.size()){
            return true;
        }
        return false;
    }

    public void checkLevelEnd() {
        if(allBallsHit()){
            double timeBonus = 1 * 15; // 1 unit every 0.067 seconds means 15 units per second
            score += timeBonus;
    
            // Call the next level method
            goToNextlevel();
            setup();
        }
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
        currentLevelIndex = 0;
        setup();
        score = 0;
        drawnLines.clear();
        allLines.clear();

    }

    public void goToNextlevel(){
        if(currentLevelIndex > 2){
            currentLevelIndex = 0;
        }else{
        currentLevelIndex++;
        }
        allLines.clear();
        drawnLines.clear();
    }

    public void gameOver(){
        textSize(28);
        text("=== TIME'S UP === ", 150,TOPBAR);
    }
   


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
