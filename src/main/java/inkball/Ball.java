package inkball;

import processing.core.PImage;
import processing.core.PVector;

public class Ball{
    PImage sprite;
    int colorIndex;
    PVector velocity;  // Vector for velocity
    PVector position;  // Vector for position
    int scoreIncrease, scoreDecrease;
    int ballScore = 0;
    static String level;
    float plusMod;
    float minusMod;
    boolean hit, checked,rightHole,isOut;
    boolean notSet = true, once = true;
    float size;  // Current size of the ball
    static final float SHRINK_RATE = 1.0f;  // Amount to shrink each frame
    float radius = 16;
    Brick lastCollidedBrick = null;

    /**
     * Constructs a Ball with the specified color index and starting position.
     * 
     * @param colorIndex the color index of the ball.
     * @param startX the starting x-coordinate of the ball.
     * @param startY the starting y-coordinate of the ball.
     */
    public Ball(int colorIndex, int startX, int startY) {
        this.rightHole = true;
        this.colorIndex = colorIndex;
        setSpriteAndScores(colorIndex);
        changeMods();
        

        this.size = 25;

        // Initialize ball position using PVector
        this.position = new PVector(startX * App.CELLSIZE, startY * App.CELLSIZE + App.TOPBAR);

        // Initialize random velocity vector, either -2 or 2 for both x and y
        this.velocity = new PVector((Math.random() < 0.5) ? -2 : 2, (Math.random() < 0.5) ? -2 : 2);

        //System.out.println("Ball " + this.colorIndex + " score = " + this.scoreIncrease);
    }

    /**
     * Sets the sprite and scores based on the ball's color index.
     * 
     * @param colorIndex the color index of the ball.
     */
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
    
    // Update position by adding velocity to it
    public void move() {

        position.add(velocity);
    }

    

    /**
     * Handles the collision of the ball with a wall.
     * This method checks if the ball collides with the given wall and adjusts the ball's position and velocity accordingly.
     * 
     * @param wall the wall to check for collision.
     */
    public void handleCollision(Wall wall) {
        // Calculate ball's edges
        float ballLeft = this.position.x - this.radius;
        float ballRight = this.position.x + this.radius;
        float ballTop = this.position.y - this.radius;
        float ballBottom = this.position.y + this.radius;

        // Calculate wall's edges
        float wallLeft = wall.position.x;
        float wallRight = wall.position.x + wall.size/2; // Assuming wall.size gives the width/height
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

    /**
     * Handles the collision of the ball with a brick if it is enabled. Otherwise passes through the brick.
     * This method checks if the ball collides with the given brick and adjusts the ball's position and velocity accordingly.
     * It also decreases the strength of the brick if a collision occurs.
     * 
     * @param brick the brick to check for collision.
     */
    public void handleBrickCollision(Brick brick) {
        if(!brick.enabled){
            return;
        }
        if (brick.checkBrickCollision(this)) {
            // Only decrease strength if the ball is colliding with a new brick
            if (lastCollidedBrick != brick) {
                brick.decreaseStrength();
                lastCollidedBrick = brick; // Set this brick as the last collided one
            }
        } else if (lastCollidedBrick == brick) {
            // Reset the collision when the ball is no longer touching this brick
            lastCollidedBrick = null;
        }
        // Calculate ball's edges
        float ballLeft = this.position.x - this.radius;
        float ballRight = this.position.x + this.radius;
        float ballTop = this.position.y - this.radius;
        float ballBottom = this.position.y + this.radius;

        // Calculate  brick's edges
        float  brickLeft =  brick.position.x;
        float  brickRight =  brick.position.x +  brick.size/2; // Assuming  brick.size gives the width/height
        float  brickTop =  brick.position.y;
        float  brickBottom =  brick.position.y +  brick.size;

        // Check for collision
        if (ballRight >  brickLeft && ballLeft <  brickRight && ballBottom >  brickTop && ballTop <  brickBottom) {
            // Find the overlap on each side
            float overlapLeft = ballRight -  brickLeft;
            float overlapRight =  brickRight - ballLeft;
            float overlapTop = ballBottom -  brickTop;
            float overlapBottom =  brickBottom - ballTop;

            // Determine the smallest overlap
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            // Change direction based on the side of collision
            if (minOverlap == overlapLeft) {
                // Collision from the left side
                this.velocity.x *= -1;  // Reverse X direction
                this.position.x =  brickLeft - this.radius; // Move the ball outside the  brick
            } else if (minOverlap == overlapRight) {
                // Collision from the right side
                this.velocity.x *= -1;  // Reverse X direction
                this.position.x =  brickRight + this.radius; // Move the ball outside the  brick
            } else if (minOverlap == overlapTop) {
                // Collision from the top side
                this.velocity.y *= -1;  // Reverse Y direction
                this.position.y =  brickTop - this.radius; // Move the ball outside the  brick
            } else if (minOverlap == overlapBottom) {
                // Collision from the bottom side
                this.velocity.y *= -1;  // Reverse Y direction
                this.position.y =  brickBottom + this.radius; // Move the ball outside the  brick
            }
        }
    }

    /**
     * Updates the ball's type if it collides with a wall of a different type.
     * If the wall's type is not 0 and is different from the ball's current color index,
     * the ball's color index is updated to match the wall's type, and the sprite and scores are updated accordingly.
     * 
     * @param wall the wall to check for type update.
     */
    private void updateBallTypeIfNeeded(Wall wall) {
        if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
            this.colorIndex = wall.getWallType();
            setSpriteAndScores(this.colorIndex);  // Update sprite and scores based on new color
            //System.err.println("Ball type " + this.colorIndex + " placed");
        }
    }

    /**
     * Returns the radius of the ball.
     * 
     * @return the radius of the ball.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Returns the modified score added when the ball hits a target.
     * If the ball has not hit a target and the score has not been set, the score is increased.
     * 
     * @return the modified score added, or 0 if the ball has already hit a target or the score has been set.
     */
    public float scorePlus() {
        if (!hit && notSet) {
            ballScore += increaseScore();
            return ballScore;
        }
        return 0;
    }

    /**
     * Returns the modified score subtracted when the ball misses a target.
     * If the ball has not hit a target and the score has not been set, the score is decreased.
     * 
     * @return the modified score subtracted, or 0 if the ball has already hit a target or the score has been set.
     */
    public float scoreMinus() {
        if (!hit && notSet) {
            ballScore -= decreaseScore();
            return ballScore;
        }
        return 0;
    }

    /**
     * Returns the current score of the ball.
     * 
     * @return the current score of the ball.
     */
    public float getScore() {
        return this.ballScore;
    }

    /**
     * Increases the score based on the ball's score increase value and a modifier.
     * 
     * @return the increased score.
     */
    public float increaseScore() {
        return this.scoreIncrease * plusMod;
    }

    /**
     * Decreases the score based on the ball's score decrease value and a modifier.
     * 
     * @return the decreased score.
     */
    public float decreaseScore() {
        return this.scoreDecrease * minusMod;
    }

    /**
     * Sets the current level of the game.
     * 
     * @param str the level to set.
     */
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

    /*
     * Shrinks the ball towards the center of the hole.
     * @param holeCenter the center of the hole to shrink towards.
     */
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
                ballScore = 0;
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

    public void draw(App app) {
        if (sprite != null) {
            app.image(sprite, position.x, position.y, size, size);
        } else {
            App.println("Ball not loading properly");
        }
    }

    public void draw(App app, int x, int y) {
        if (sprite != null) {
            app.image(sprite, x,y, 25,25);
        } else {
            App.println("Ball not loading properly");
        }
    }
}