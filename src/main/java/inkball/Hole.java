package inkball;

import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Hole implements TileContent {
    PImage sprite;
    int holeIndex;
    int drawX;
    int drawY;
    List<PVector> pixelPositions; // List to store pixel positions of the hole

    /**
     * Constructs a Hole with the specified index.
     * 
     * @param holeIndex the index of the hole, which determines its appearance.
     */
    public Hole(int holeIndex) {
        this.holeIndex = holeIndex;
        if (holeIndex >= 0) {
            this.sprite = App.holesprite[holeIndex];
        } else {
            System.out.println("Invalid hole: " + holeIndex);
        }

        
        this.pixelPositions = new ArrayList<>(); // Initialize the pixel positions list
    }

    /**
     * Returns the index of the hole.
     * 
     * @return the index of the hole.
     */
    public int getHoleIndex() {
        return this.holeIndex;
    }

    /**
     * Sets the x-coordinate of the hole's position.
     * 
     * @param n the x-coordinate to set.
     */
    public void setX(int n) {
        this.drawX = n;
        updatePixelPositions(); // Update pixel positions
    }

    /**
     * Sets the y-coordinate of the hole's position.
     * 
     * @param n the y-coordinate to set.
     */
    public void setY(int n) {
        this.drawY = n;
        updatePixelPositions(); // Update pixel positions
    }

    /**
     * Updates the pixel positions based on the hole's position.
     * This method recalculates the pixel positions within the hole's area.
     */
    private void updatePixelPositions() {
        pixelPositions.clear(); // Clear previous pixel positions
        int width = App.CELLSIZE * 2; // Assuming the hole takes up a 2x2 tile area
        int height = App.CELLSIZE * 2;

        for (int x = drawX; x < drawX + width; x++) {
            for (int y = drawY; y < drawY + height; y++) {
                pixelPositions.add(new PVector(x, y)); // Store each pixel position
            }
        }
    }

    @Override
    public String toString() {
        return "hole";
    }

    @Override
    public void draw(App app, int x, int y) {
        if (sprite != null) {
            drawX = x * App.CELLSIZE;
            drawY = y * App.CELLSIZE + App.TOPBAR; 
            float width = 2 * App.CELLSIZE;
            float height = 2 * App.CELLSIZE;
            app.image(sprite, drawX, drawY, width, height);
            updatePixelPositions(); // Update pixel positions when drawing
            // Optionally draw the pixel positions
            // app.fill(255, 0, 0, 100);
            // for (PVector pixel : pixelPositions) {
            //     app.rect(pixel.x, pixel.y, 1, 1); // Draw small rectangles for each pixel
            // }
        }
    }

    // Method to check if a ball touches any pixel of the hole
    public boolean checkCollision(Ball ball) {
        for (PVector pixel : pixelPositions) {
            if (PVector.dist(new PVector(ball.position.x, ball.position.y), pixel) < 1) {
                return true; // Collision detectedz
            }
        }
        return false; // No collision
    }

    public PVector getHoleCenter() {
        float centerX = drawX + App.CELLSIZE;  // Half the width of the hole
        float centerY = drawY + App.CELLSIZE;  // Half the height of the hole
        return new PVector(centerX, centerY);  // Return the center as a PVector
    }

    public void adjustBallVelocity(Ball ball) {
        PVector holeCenter = getHoleCenter();
        float distance = PVector.dist(ball.position, holeCenter); // Calculate distance to hole center

        if (distance < 100) { // Adjust if the ball is within a certain distance
            float speedIncrease = (100 - distance) * 0.01f; // Increase factor (0.1 can be adjusted)
            ball.velocity.add(PVector.sub(holeCenter, ball.position).normalize().mult(speedIncrease));
        }
    }
    

    
}
