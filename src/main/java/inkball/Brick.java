package inkball;
import processing.core.PImage;
import processing.core.PVector;
import processing.core.PApplet;

public class Brick implements TileContent {

    PImage sprite;
    int brickType,size;
    int drawX;
    int drawY;
    PVector position;
    boolean checked = true;
    int strength;

    public Brick(int brickType) {
        this.brickType = brickType;
        this.sprite = App.bricksprite[brickType];
        this.size = App.CELLSIZE;
        this.strength = 3;
    }

    public void setPosition(int x, int y){
        this.position = new PVector(x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }

    public void decreaseStrength() {
        this.strength--;
    }

    public boolean isDestroyed() {
        return this.strength <= 0;
    }

    public boolean checkBallType(Ball ball) {
        return (ball.colorIndex == this.brickType) || this.brickType == 0;
    }

    public void replaceWithTileWithImage(Board board) {
        board.placeItem(drawX, drawY, new TileWithImage());
    }

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
        app.image(sprite, drawX, drawY, App.CELLSIZE, App.CELLSIZE);
    }
}
