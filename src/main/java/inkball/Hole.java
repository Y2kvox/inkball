package inkball;

import processing.core.PImage;

public class Hole implements TileContent {
    PImage sprite;
    int holeIndex;

    public Hole(int holeIndex){
    if(holeIndex >= 0){
        this.sprite = App.holesprite[holeIndex];
    }else{
        System.out.println("Invalid hole: " + holeIndex);
    }

    this.holeIndex = holeIndex;
    }



    @Override
    public String toString() {
        return "hole";
    }


    @Override
    public void draw(App app, int x, int y){
        if(sprite != null){
            float drawX = x * App.CELLSIZE;
            float drawY = y * App.CELLSIZE + App.TOPBAR; 
            float width = 2 * App.CELLSIZE;
            float height = 2 * App.CELLSIZE;
            app.image(sprite, drawX, drawY, width, height);
        }

    }
}
