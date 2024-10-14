package inkball;

import processing.core.PImage;

//import org.checkerframework.checker.units.qual.min;

//import processing.core.*;

public class Ball implements TileContent {
    PImage sprite;
    int colorIndex;
    double velocityX;
    double velocityY;
    float posX, posY;
    int scoreIncrease, scoreDecrease;
    int ballScore = 0;
    static String level;
    float plusMod;
    float minusMod;
    boolean hit;
    boolean notSet = true;
    float size; // Current size of the ball
    static final float SHRINK_RATE = 1.0f; // Amount to shrink each frame
    static final float MIN_SIZE = 5.0f; // Minimum size to shrink to

    public Ball(int colorIndex, int startX, int startY) {
        this.colorIndex = colorIndex;
        setSpriteAndScores(colorIndex);
        changeMods();

        this.size = 25;

        // Initialize ball position
        this.posX = startX * App.CELLSIZE;
        this.posY = startY * App.CELLSIZE + App.TOPBAR;

        // Random velocity vector, either -2 or 2
        this.velocityX = (Math.random() < 0.5) ? -2 : 2;
        this.velocityY = (Math.random() < 0.5) ? -2 : 2;

        System.out.println("Ball" + this.colorIndex + "score = "+ this.scoreIncrease);
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
        posX += velocityX;
        posY += velocityY;
    }

    public void bounceOffBoundary(Board board) {
        // Check for boundary collisions and reverse direction
        if (posX <= 0 || posX >= App.WIDTH - App.CELLSIZE) {
            velocityX *= -1;  // Reverse X direction
        }
        if (posY <= App.TOPBAR || posY >= App.HEIGHT - App.CELLSIZE) {
            velocityY *= -1;  // Reverse Y direction
        }

        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                Tile tile = board.grid[y][x];
                if (tile.hasContent() && tile.getContent() instanceof Wall) {
                    Wall wall = (Wall) tile.getContent();
                    float wallX = wall.getX();
                    float wallY = wall.getY();

                    // X-direction check
                    if (posY >= wallY - 0.8 * App.CELLSIZE && posY <= wallY + 0.8 * App.CELLSIZE) {
                        if (Math.abs(posX - wallX) <= App.CELLSIZE) {
                            velocityX *= -1;

                            // Change ball type and update score settings if it hits a different wall type
                            if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
                                this.colorIndex = wall.getWallType();
                                setSpriteAndScores(this.colorIndex);  // Update sprite and scores based on new color
                                System.err.println("Ball type " + this.colorIndex + " placed");
                            }
                        }
                    }

                    // Y-direction check
                    if (posX >= wallX - 0.8 * App.CELLSIZE && posX <= wallX + 0.8 * App.CELLSIZE) {
                        if (Math.abs(posY - wallY) <= App.CELLSIZE) {
                            velocityY *= -1;

                            // Change ball type and update score settings if it hits a different wall type
                            if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
                                this.colorIndex = wall.getWallType();
                                setSpriteAndScores(this.colorIndex);  // Update sprite and scores based on new color
                                System.err.println("Ball type " + this.colorIndex + " placed");
                            }
                        }
                    }
                }

                if (tile.hasContent() && tile.getContent() instanceof Hole) {
                    Hole hole = (Hole) tile.getContent();

                    // Check if the ball's position is in the hole's pixel positions
                    if ((hole.checkCollision(this) && this.colorIndex == hole.holeIndex) || (hole.checkCollision(this) && 
                    (this.colorIndex == 0 || hole.holeIndex == 0))) {
                        this.hit = true;
                        scorePlus();
                        this.notSet = false;
                    }else if(hole.checkCollision(this) && this.colorIndex != hole.holeIndex){
                        this.hit = true;
                        scoreMinus();
                        this.notSet = false;
                    }
                }

            }
        }
    }

    public boolean scorePlus() {
        // Only increase the score if certain conditions are met
        if (hit && notSet) {
            ballScore += increaseScore();
            System.out.println("Ball " + this.colorIndex + " went to hole, score is now +"+ this.scoreIncrease);
            return true;
        }
        return false;
    }

    public boolean scoreMinus() {
        // Only increase the score if certain conditions are met
        if (hit && notSet) {
            System.out.println("Ball "+this.colorIndex+" went to Hole, score -"+ this.scoreDecrease);
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

    public void shrink() {
        if (size >= 0) {
            size -= SHRINK_RATE; // Decrease size
        }
    }

    public float getSize() {
        return size; // Method to return current size
    }

    @Override
    public String toString() {
        return "ball";
    }

    @Override
    public void draw(App app, int x, int y) {
        if (sprite != null) {
            app.image(sprite, posX, posY, size, size);
        } else {
            App.println("Ball not loading properly");
        }
    }
}

