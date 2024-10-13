package inkball;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Hole implements TileContent {
    PImage sprite;
    int holeIndex;
    int drawX;
    int drawY;
    List<PVector> pixelPositions; // List to store pixel positions of the hole

    public Hole(int holeIndex) {
        if (holeIndex >= 0) {
            this.sprite = App.holesprite[holeIndex];
        } else {
            System.out.println("Invalid hole: " + holeIndex);
        }

        this.holeIndex = holeIndex;
        this.pixelPositions = new ArrayList<>(); // Initialize the pixel positions list
    }

    public int getHoleIndex() {
        return this.holeIndex;
    }

    public int getX() {
        return this.drawX;
    }

    public int getY() {
        return this.drawY;
    }

    public void setX(int n) {
        this.drawX = n;
        updatePixelPositions(); // Update pixel positions
    }

    public void setY(int n) {
        this.drawY = n;
        updatePixelPositions(); // Update pixel positions
    }

    // Update the pixel positions based on the hole's position
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
            // Optionally draw the pixel positions for debugging
            // app.fill(255, 0, 0, 100);
            // for (PVector pixel : pixelPositions) {
            //     app.rect(pixel.x, pixel.y, 1, 1); // Draw small rectangles for each pixel
            // }
        }
    }

    // Method to check if a ball touches any pixel of the hole
    public boolean checkCollision(Ball ball) {
        for (PVector pixel : pixelPositions) {
            if (PVector.dist(new PVector(ball.posX, ball.posY), pixel) < 32) {
                return true; // Collision detected
            }
        }
        return false; // No collision
    }
}
