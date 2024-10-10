package inkball;

import processing.core.PApplet;
import processing.core.PImage;
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

    //time
    private int timerDuration;
    private int timeRemaining;
    private boolean timerRunning; // Timer state
    private int lastMillis;


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
    }

	@Override
    public void keyPressed(KeyEvent event){
        
    }

	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        System.out.println("Mouse pressed at: " + mouseX + ", " + mouseY);
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		
    }


    /**
     * Draw all elements in the game by current frame.
     */

    public void ballCheck(){
        Ball ball = (Ball) board.grid[board.ballx][board.bally].getContent();
        ball.move();
        ball.bounceOffBoundary(board);  // Bounce off walls if necessary
        ball.draw(this, board.bally, board.ballx);  // Draw ball at new position
    }
	@Override
    public void draw() {

        
        
        //----------------------------------
        //display Board for current level:
        background(225);
        // Draw the board
        board.draw(this); // Draw the board
        // Move and bounce all balls
        ballCheck();
        
        //----------------------------------
        //display score
        //----------------------------------
        if (timerRunning) {
            int currentMillis = millis();
            if (currentMillis - lastMillis >= 1000) { // Check if one second has passed
                timeRemaining--; // Decrease time remaining by 1 second
                lastMillis = currentMillis; // Update the lastMillis
            }

            // Check if the timer has reached zero
            if (timeRemaining <= 0) {
                timerRunning = false; // Stop the timer
                timeRemaining = 0;
                // You can also trigger an event here when the timer ends
            }
        }
        fill(0);
        textSize(20);
        text("Timer: "+ timeRemaining, 450, 25);
        text("Score: "+ 0, 450, 50);
        
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



   


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
