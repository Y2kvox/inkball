package inkball;
import processing.core.PImage;
import processing.core.PVector;


/**
 * Represents a brick in the Inkball game.
 * Each brick has a type, position, strength, and can interact with balls.
 */
public class Brick implements TileContent {
    public PImage sprite, tileSprite;
    public int brickType,size;
    public PVector position;
    public boolean enabled;
    public int strength;
    public int drawX, drawY;


    /**
     * Constructs a Brick with the specified type.
     * 
     * @param brickType the type of the brick, which determines its appearance and behavior.
     */
    public Brick(int brickType) {
        this.brickType = brickType;
        this.tileSprite = App.tilesprite;
        this.sprite = App.bricksprite[brickType];
        this.size = App.CELLSIZE;
        this.strength = 3;
        this.enabled = true;
    }

    /**
     * Sets the position of the brick on the game grid.
     * 
     * @param x the x-coordinate on the grid.
     * @param y the y-coordinate on the grid.
     */
    public void setPosition(int x, int y){
        this.position = new PVector(x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }


    /**
     * Decreases the strength of the brick by one.
     * If the strength drops to zero or below, the brick is considered destroyed.
     */
    public void decreaseStrength() {
        this.strength--;
        if(strength <= 0) {
            strength = 0;
        }
    }


    /**
     * Checks if the brick is destroyed.
     * 
     * @return true if the brick's strength is zero or less, false otherwise.
     */
    public boolean isDestroyed() {
        return this.strength <= 0;
    }


    /**
     * Checks if the ball's type matches the brick's type.
     * 
     * @param ball the ball to check against the brick.
     * @return true if the ball's color index matches the brick's type or if the brick type is 0, false otherwise.
     */
    public boolean checkBallType(Ball ball) {
        return (ball.colorIndex == this.brickType) || this.brickType == 0;
    }

    
    /**
     * Checks if the ball collides with the brick.
     * 
     * @param ball the ball to check for collision.
     * @return true if the ball collides with the brick and the brick is enabled, false otherwise.
     */
    public boolean checkBrickCollision(Ball ball) {
        // Calculate the brick's bounds
        float brickLeft = this.position.x;
        float brickRight = this.position.x + this.size;
        float brickTop = this.position.y;
        float brickBottom = this.position.y + this.size;
    
        // Check for collision using the ball's position and radius
        boolean collisionX = ball.position.x + ball.radius*2 >= brickLeft && ball.position.x <= brickRight;
        boolean collisionY = ball.position.y + ball.radius*2 >= brickTop && ball.position.y - ball.radius*2 <= brickBottom;
    
        return collisionX && collisionY;
    }

    public void draw(App app, int x, int y) {
        setPosition(x, y);
        drawX = x * App.CELLSIZE;
        drawY = y * App.CELLSIZE + App.TOPBAR;
        if(strength > 0){
            app.image(sprite, drawX, drawY, App.CELLSIZE, App.CELLSIZE);
        }else{
            app.image(tileSprite, drawX, drawY, App.CELLSIZE, App.CELLSIZE);
        }
    }
}
