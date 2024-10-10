package inkball;

import processing.core.PImage;

public class Ball implements TileContent {
    PImage sprite;
    int colorIndex;
    int velocityX;
    int velocityY;
    float posX, posY;

    public Ball(int colorIndex, int startX, int startY) {
        if (colorIndex >= 0 && colorIndex < App.ballsprite.length) {
            this.sprite = App.ballsprite[colorIndex];
        } else {
            System.out.println("Invalid ball: " + colorIndex);
        }
        this.colorIndex = colorIndex;

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
                    float drawX = x * App.CELLSIZE;
                    float drawY = y * App.CELLSIZE + App.TOPBAR;

                    // Original X-direction check
                    if (posY >= drawY - 0.8 * App.CELLSIZE && posY <= drawY + 0.8 * App.CELLSIZE) {
                        if ((posX + App.CELLSIZE <= drawX + 0.5 * App.CELLSIZE && posX >= drawX - App.CELLSIZE) ||
                            (posX >= drawX + App.CELLSIZE - 0.5 * App.CELLSIZE && posX + App.CELLSIZE <= drawX + 2 * App.CELLSIZE)) {
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
                    if (posX >= drawX - 0.8 * App.CELLSIZE && posX <= drawX + 0.8 * App.CELLSIZE) {
                        if ((posY + App.CELLSIZE <= drawY + 0.5 * App.CELLSIZE && posY >= drawY - App.CELLSIZE) ||
                            (posY >= drawY + App.CELLSIZE - 0.5 * App.CELLSIZE && posY + App.CELLSIZE <= drawY + 2 * App.CELLSIZE)) {
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
}
