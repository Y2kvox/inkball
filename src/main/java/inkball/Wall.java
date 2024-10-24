package inkball;


import processing.core.PImage;
import processing.core.PVector;
import processing.core.PApplet;

public class Wall implements TileContent {
    PImage sprite;
    int wallType,size;
    int drawX;
    int drawY;
    PVector position;
    boolean checked = true;

    public Wall(int wallType) {
        this.wallType = wallType;
        this.sprite = App.wallsprite[wallType];
        this.size = App.CELLSIZE;
        
    }

    public int getWallType() {
        return this.wallType;
    }

    public PVector getPosition(){
        return this.position;
    }

    public void setPosition(int x, int y){
        this.position = new PVector(x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }


    // Get edge positions for collision detection
    public float getTopEdge() {
        return position.y;
    }

    public float getBottomEdge() {
        return position.y + this.size;
    }


    public float getRightEdge() {
        return position.x + this.size;
    }

    public float getLeftEdge() {
            return position.x;
    }
    
    // public boolean checkCollision(Ball ball) {
    //     // Calculate the wall's bounds
    //     float wallLeft = this.position.x;
    //     float wallRight = this.position.x + this.size;
    //     float wallTop = this.position.y;
    //     float wallBottom = this.position.y + this.size;
    
    //     // Check for collision using the ball's position and radius
    //     boolean collisionX = ball.position.x + ball.radius*2 >= wallLeft && ball.position.x <= wallRight;
    //     boolean collisionY = ball.position.y + ball.radius*2 >= wallTop && ball.position.y - ball.radius*2 <= wallBottom;
    
    //     return collisionX && collisionY;
    // }
    

    @Override
    public String toString() {
        return "wall";
    }

    @Override
    public void draw(App app, int x, int y) {
        drawX = x * App.CELLSIZE;
        drawY = y * App.CELLSIZE + App.TOPBAR;
        if (checked){
            setPosition(x,y);
            checked = false;
        }
        app.image(sprite, drawX, drawY, App.CELLSIZE, App.CELLSIZE);
        // Draw thin lines over the wall edges
        //app.stroke(255, 0, 0); // Set line color (red, in this case)
        //app.strokeWeight(2); // Set the line thickness

        // Draw the edges of the wall using the edge methods
        // app.line(getLeftEdge(), getTopEdge(), getRightEdge(), getTopEdge()); // Top edge
        // app.line(getRightEdge(), getTopEdge(), getRightEdge(), getBottomEdge()); // Right edge
        // app.line(getRightEdge(), getBottomEdge(), getLeftEdge(), getBottomEdge()); // Bottom edge
        // app.line(getLeftEdge(), getBottomEdge(), getLeftEdge(), getTopEdge()); // Left edge
    }
}
