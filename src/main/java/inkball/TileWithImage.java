package inkball;

import processing.core.PImage;

public class TileWithImage implements TileContent{
    PImage tileSprite;

    public TileWithImage() {
        // Load the tile.png image
        this.tileSprite = App.tilesprite;
    }

    
    @Override
    public String toString() {
        return "tile";
    }

    @Override
    public void draw(App app, int x, int y) {
        if (tileSprite != null) {
            float drawX = x * App.CELLSIZE;
            float drawY = y * App.CELLSIZE + App.TOPBAR;
            app.image(tileSprite, drawX, drawY);
        }
    }
}
