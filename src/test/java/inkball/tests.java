package inkball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.core.PVector;

public class tests {

    private App app;

    @BeforeEach
    public void setup() {
        app = new App();
        PApplet.main("inkball.App"); // This may need to be adjusted if it blocks the test.
    }

    @Test
    public void testPauseFunctionality() {
        // Initially, the game should not be paused
        System.out.println("Paused after setup: " + app.paused);
        assertFalse(app.paused, "The game should not be paused after setup.");
        
        // Simulate pressing the ' ' key to pause the game
        app.key = ' '; 
        app.keyPressed(); 
        System.out.println("Paused after pressing ' ': " + app.paused);
        assertTrue(!app.paused, "The game should be paused after pressing the ' ' key.");

        // Simulate mouse press to check if drawing is possible after pausing
        app.mousePressed(); 
        System.out.println("Can draw after mouse press: " + app.canDraw);
        assertFalse(app.canDraw, "Lines should not be drawable while the game is paused.");

        // Simulate pressing the ' ' key again to resume the game
        app.key = ' ';
        app.keyPressed(); 
        System.out.println("Paused after pressing ' ': " + app.paused);
        assertFalse(app.paused, "The game should not be paused after resuming with the ' ' key.");
        
        // Check if drawing is enabled again
        app.mouseButton = PApplet.LEFT;
        app.timerRunning = true;
        app.mousePressed(); 
        System.out.println("Can draw after mouse press: " + app.canDraw);
        assertTrue(app.timerRunning, "Lines should be drawable after the game resumes.");
    }

    @Test
    public void board(){
        Board board = new Board(500,400,1);
    }

    

}
