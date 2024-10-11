package inkball;

import processing.core.PImage;
import processing.core.*;

public class Ball implements TileContent {
    PImage sprite;
    int colorIndex;
    int velocityX;
    int velocityY;
    float posX, posY, scoreIncrease, scoreDecrease;

    public Ball(int colorIndex, int startX, int startY) {
        if (colorIndex >= 0 && colorIndex < App.ballsprite.length) {
            this.sprite = App.ballsprite[colorIndex];
        } else {
            System.out.println("Invalid ball: " + colorIndex);
        }
        this.colorIndex = colorIndex;

        //set score
        if(this.colorIndex == 0){
            this.scoreIncrease = 70;
            this.scoreDecrease = 0;
        }else if( this.colorIndex == 1 || this.colorIndex == 2 || this.colorIndex == 3){
            this.colorIndex = 50;
            this.scoreDecrease = 25;
        }else if(this.colorIndex == 4){
            this.scoreIncrease = 100;
            this.scoreDecrease = 100;
        }

        // Initialize ball position
        this.posX = startX * App.CELLSIZE;
        this.posY = startY * App.CELLSIZE + App.TOPBAR;

        // Random velocity vector, either -2 or 2
        this.velocityX = (Math.random() < 0.5) ? -2 : 2;
        this.velocityY = (Math.random() < 0.5) ? -2 : 2;
    }

    @Override
    public String toString() {
        return "ball";
    }

    @Override
    public void draw(App app, int x, int y) {
        if (sprite != null) {
            app.image(sprite, posX, posY);  // Use actual position for drawing
        } else {
            App.println("Ball not loading properly");
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

                    // Original X-direction check
                    if (posY >= wallY - 0.8 * App.CELLSIZE && posY <= wallY + 0.8 * App.CELLSIZE) {
                        if ((posX + App.CELLSIZE <= wallX + 0.5 * App.CELLSIZE && posX >= wallX - App.CELLSIZE) ||
                            (posX >= wallX + App.CELLSIZE - 0.5 * App.CELLSIZE && posX + App.CELLSIZE <= wallX + 2 * App.CELLSIZE)) {
                            velocityX *= -1;
                            
                            // Change ball type if it hits a different wall type
                            if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
                                this.colorIndex = wall.getWallType();
                                this.sprite = App.ballsprite[colorIndex];
                                System.err.println("Ball type " + this.colorIndex + " placed");
                            }
                        }
                    }

                    // Original Y-direction check
                    if (posX >= wallX - 0.8 * App.CELLSIZE && posX <= wallX + 0.8 * App.CELLSIZE) {
                        if ((posY + App.CELLSIZE <= wallY + 0.5 * App.CELLSIZE && posY >= wallY - App.CELLSIZE) ||
                            (posY >= wallY + App.CELLSIZE - 0.5 * App.CELLSIZE && posY + App.CELLSIZE <= wallY + 2 * App.CELLSIZE)) {
                            velocityY *= -1;
                            
                            // Change ball type if it hits a different wall type
                            if (wall.getWallType() != 0 && wall.getWallType() != this.colorIndex) {
                                this.colorIndex = wall.getWallType();
                                this.sprite = App.ballsprite[colorIndex];
                                System.err.println("Ball type " + this.colorIndex + " placed");
                            }
                        }
                    }
                }
            }
        }
    }

    public void bounceOffWall() {
        velocityX *= -1;
        velocityY *= -1;
    }

    public float increaseScore(){
        return this.scoreIncrease;
    }

    public float decreaseScore(){
        return this.scoreDecrease;
    }

    
    
}
