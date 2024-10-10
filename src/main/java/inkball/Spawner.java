package inkball;

import processing.core.PImage;

public class Spawner implements TileContent {
    PImage sprite;

    public Spawner(){
        this.sprite = App.spawnsprite;
    }

    @Override
    public String toString() {
        return "spawner";
    }

    @Override
    public void draw(App app, int x, int y){
        if (sprite != null){
            float drawX = x * App.CELLSIZE;
            float drawY = y * App.CELLSIZE + App.TOPBAR;
            app.image(sprite, drawX, drawY);
        }else{
            App.println("Spawner not laoding properly");
        }
    }
}
