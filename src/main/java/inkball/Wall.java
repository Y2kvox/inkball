package inkball;

import processing.core.PImage;

public class Wall implements TileContent {
    PImage sprite; // A single PImage for the wall
    float drawX;
    float drawY;
    int wallType;

    public Wall(int i) {
        // Select the appropriate sprite based on the value of i
        if (i >= 0 && i < App.wallsprite.length) {
            this.sprite = App.wallsprite[i]; // Correctly assign a single PImage from the array
        } else {
            System.out.println("Invalid wall type: " + i);
            this.sprite = null;  // Handle error, e.g., by setting sprite to null or a default image
        }

    }

    @Override
    public String toString() {
        return "wall";
    }

    public int getWallType(){
        return this.wallType;
    }


    @Override
    public void draw(App app, int x, int y) {
        float drawX = x * App.CELLSIZE;
        float drawY = y * App.CELLSIZE + App.TOPBAR;
        this.drawX = drawX;
        this.drawY = drawY;

        // Only draw if sprite is valid
        if (sprite != null) {
            app.image(sprite, drawX, drawY);
        }else{
            App.println("Wall not laoding properly");
        }
    }
}

