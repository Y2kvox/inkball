package inkball;

import processing.core.PImage;
import processing.core.PVector;

public class Ball implements TileContent {
    PImage sprite;
    int colorIndex;
    PVector velocity;  // Vector for velocity
    PVector position;  // Vector for position
    int scoreIncrease, scoreDecrease;
    int ballScore = 0;
    static String level;
    float plusMod;
    float minusMod;
    boolean hit;
    boolean notSet = true;
    float size;  // Current size of the ball
    static final float SHRINK_RATE = 1.0f;  // Amount to shrink each frame
    float radius = 16;

    public Ball(int colorIndex, int startX, int startY) {
        this.colorIndex = colorIndex;
        setSpriteAndScores(colorIndex);
        changeMods();
        

        this.size = 25;

        // Initialize ball position using PVector
        this.position = new PVector(startX * App.CELLSIZE, startY * App.CELLSIZE + App.TOPBAR);

        // Initialize random velocity vector, either -2 or 2 for both x and y
        this.velocity = new PVector((Math.random() < 0.5) ? -2 : 2, (Math.random() < 0.5) ? -2 : 2);

        System.out.println("Ball " + this.colorIndex + " score = " + this.scoreIncrease);
    }

    // Set sprite and scores based on the ball's colorIndex
    public void setSpriteAndScores(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < App.ballsprite.length) {
            this.sprite = App.ballsprite[colorIndex];
        } else {
            System.out.println("Invalid ball: " + colorIndex);
        }

        // Update scores based on the ball color
        switch (this.colorIndex) {
            case 0:
                this.scoreIncrease = 70;
                this.scoreDecrease = 0;
                break;
            case 1:
            case 2:
            case 3:
                this.scoreIncrease = 50;
                this.scoreDecrease = 25;
                break;
            case 4:
                this.scoreIncrease = 100;
                this.scoreDecrease = 100;
                break;
        }
    }

    public void move() {
        // Update position by adding velocity to it
        position.add(velocity);
    }

    // public void bounceOffBoundary(Board board) {
    //     // Check for boundary collisions and reverse direction
    //     if (position.x <= 0 || position.x >= App.WIDTH - App.CELLSIZE) {
    //         velocity.x *= -1;  // Reverse X direction
    //     }
    //     if (position.y <= App.TOPBAR || position.y >= App.HEIGHT - App.CELLSIZE) {
    //         velocity.y *= -1;  // Reverse Y direction
    //     }
    
    //     // Iterate over each tile on the board
    //     for (int y = 0; y < board.height; y++) {
    //         for (int x = 0; x < board.width; x++) {
    //             Tile tile = board.grid[y][x];
    
    //             // Wall collision detection
    //             if (tile.hasContent() && tile.getContent() instanceof Wall) {
    //                 Wall wall = (Wall) tile.getContent();
    //                 PVector wallPos = new PVector(wall.getX(), wall.getY());
    
    //                 // X-direction check
    //                 if (position.y >= wallPos.y - 6 * App.CELLSIZE && position.y <= wallPos.y + 6 * App.CELLSIZE) {
    //                     if (PVector.dist(new PVector(position.x, wallPos.y), wallPos) <= App.CELLSIZE) {
    //                         velocity.x *= -1;
    //                         updateBallTypeIfNeeded(wall);
    //                     }
    //                 }
    //                 // Y-direction check
    //                 if (position.x >= wallPos.x - 6 * App.CELLSIZE && position.x <= wallPos.x + 6 * App.CELLSIZE) {
    //                     if (PVector.dist(new PVector(wallPos.x, position.y), wallPos) <= App.CELLSIZE) {
    //                         velocity.y *= -1;
    //                         updateBallTypeIfNeeded(wall);
    //                     }
    //                 }
    //             }
    
    //             // Hole collision detection
    //             if (tile.hasContent() && tile.getContent() instanceof Hole) {
    //                 Hole hole = (Hole) tile.getContent();
    
    //                 if (hole.checkCollision(this)) {
    //                     this.hit = true;
                        
    //                     if (this.colorIndex == hole.holeIndex || this.colorIndex == 0 || hole.holeIndex == 0) {
    //                         scorePlus();
    //                     } else {
    //                         scoreMinus();
    //                     }
                        
    //                     this.notSet = false;
    //                 }
                    
    //             }
    //         }
    //     }
    // }

    // Handle collision with a wall


    // Function to handle ball collision with a wall
    public void handleCollision(Wall wall) {
        // Calculate ball's edges
        float ballLeft = this.position.x - this.radius;
        float ballRight = this.position.x + this.radius;
        float ballTop = this.position.y - this.radius;
        float ballBottom = this.position.y + this.radius;

        // Calculate wall's edges
        float wallLeft = wall.position.x;
        float wallRight = wall.position.x + wall.size; // Assuming wall.size gives the width/height
        float wallTop = wall.position.y;
        float wallBottom = wall.position.y + wall.size;

        // Check for collision
        if (ballRight > wallLeft && ballLeft < wallRight && ballBottom > wallTop && ballTop < wallBottom) {
            // Find the overlap on each side
            float overlapLeft = ballRight - wallLeft;
            float overlapRight = wallRight - ballLeft;
            float overlapTop = ballBottom - wallTop;
            float overlapBottom = wallBottom - ballTop;

            // Determine the smallest overlap
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            // Change direction based on the side of collision
            if (minOverlap == overlapLeft) {
                // Collision from the left side
                this.velocity.x *= -1;  // Reverse X direction
                this.position.x = wallLeft - this.radius; // Move the ball outside the wall
            } else if (minOverlap == overlapRight) {
                // Collision from the right side
                this.velocity.x *= -1;  // Reverse X direction
                this.position.x = wallRight + this.radius; // Move the ball outside the wall
            } else if (minOverlap == overlapTop) {
                // Collision from the top side
                this.velocity.y *= -1;  // Reverse Y direction
                this.position.y = wallTop - this.radius; // Move the ball outside the wall
            } else if (minOverlap == overlapBottom) {
                // Collision from the bottom side
                this.velocity.y *= -1;  // Reverse Y direction
                this.position.y = wallBottom + this.radius; // Move the ball outside the wall
            }
            updateBallTypeIfNeeded(wall);  // Update ball type if needed
        }
    }


    
    
    private void updateBallTypeIfNeeded(Wall wall) {
        if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
            this.colorIndex = wall.getWallType();
            setSpriteAndScores(this.colorIndex);  // Update sprite and scores based on new color
            //System.err.println("Ball type " + this.colorIndex + " placed");
        }
    }
    
    

    public float getRadius() {
        return radius;
    }

    public boolean scorePlus() {
        if (hit && notSet) {
            ballScore += increaseScore();
            return true;
        }
        return false;
    }

    public boolean scoreMinus() {
        if (hit && notSet) {
            ballScore -= decreaseScore();
            return true;
        }
        return false;
    }

    public float getScore() {
        return ballScore;
    }

    public float increaseScore() {
        return this.scoreIncrease * plusMod;
    }

    public float decreaseScore() {
        return this.scoreDecrease * minusMod;
    }

    public static void setLevel(String str) {
        level = str;
    }

    public void changeMods() {
        switch (level) {
            case "level1.txt":
                plusMod = 1f;
                minusMod = 1f;
                break;
            case "level2.txt":
                plusMod = 1.2f;
                minusMod = 1.2f;
                break;
            case "level3.txt":
                plusMod = 1.3f;
                minusMod = 1.3f;
                break;
            default:
                break;
        }
    }

    public void shrink(PVector holeCenter) {
        if (size > 0) {
            // Calculate the direction towards the center of the hole
            PVector directionToCenter = PVector.sub(holeCenter, position);
            
            // Move the object towards the center by a fraction proportional to the shrinking rate
            position.add(directionToCenter.mult(SHRINK_RATE / size));
            
            // Decrease the size
            size -= SHRINK_RATE;
            
            if (size < 0) {
                size = 0;  // Ensure size doesn't go negative
            }
        }
    }
    

    public float getSize() {
        return size;  // Method to return current size
    }

    @Override
    public String toString() {
        return "ball";
    }

    @Override
    public void draw(App app, int x, int y) {
        if (sprite != null) {
            app.image(sprite, position.x, position.y, size, size);
        } else {
            App.println("Ball not loading properly");
        }
    }
}