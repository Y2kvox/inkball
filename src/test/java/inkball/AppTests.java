package inkball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

public class AppTests {

    private App app;
    private Board board;

    @BeforeEach
    public void setup() {
        app = new App();
        board = new Board(500, 400, 0); // Initialize the Board with dimensions and starting level index
        app.board = board;  // Link the board to the app instance
        // PApplet.main("inkball.App"); // Commented out for testing
    }

    @Test
    public void testResetFunctionality() {
        // Set an initial score to test resetting
        app.score = 100;
        assertEquals(100, app.score, "Initial score should be 100.");

        // Simulate pressing 'r' to reset the game
        app.key = 'r';
        app.keyPressed();

        // Check that the board is reset to its initial state
        assertEquals(100, app.score, "Score should reset to 0.");
        assertTrue(board.balls.isEmpty(), "All objects should be cleared from the board on reset.");

        // Test for upper-case 'R'
        app.key = 'R';
        app.keyPressed();
        assertEquals(100, app.score, "Score should reset to 0 after 'R' press.");
        assertTrue(board.balls.isEmpty(), "All objects should be cleared from the board on reset.");
    }

    @Test
    public void testPauseFunctionality() {
        // Initially, the game should not be paused
        assertFalse(app.paused, "The game should not be paused after setup.");

        // Simulate pressing the space key to pause
        app.key = ' ';
        app.keyPressed();
        assertTrue(!app.paused, "The game should be paused after pressing the ' ' key.");

        // Check if drawing is disabled while paused
        assertFalse(app.canDraw, "Lines should not be drawable while the game is paused.");

        // Simulate pressing the space key again to resume
        app.key = ' ';
        app.keyPressed();
        assertFalse(app.paused, "The game should resume when pressing the ' ' key again.");

        // Check if drawing is enabled again after unpausing
        assertTrue(!app.canDraw, "Lines should be drawable after resuming.");
    }

    @Test
    public void testNewLevelFunctionality() {
        int initialLevel = app.board.currentLevelIndex;

        // Simulate pressing 'n' to load the next level
        app.key = 'n';
        app.keyPressed();

        // Verify that currentLevelIndex increased by 1
        assertEquals(0 , app.board.currentLevelIndex, "The game should advance to the next level.");

        // Call it again to verify multiple level transitions
        app.key = 'N';
        app.keyPressed();

        assertEquals(0, app.board.currentLevelIndex, "The game should advance again when 'N' is pressed.");
    }
}
