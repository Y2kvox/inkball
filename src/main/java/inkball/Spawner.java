package inkball;

import java.util.List;
import java.util.ArrayList;

import processing.core.PImage;

public class Spawner implements TileContent {
    PImage sprite;
    List<int[]> coords = new ArrayList<>();

    int x,y;

    public Spawner(){
        this.sprite = App.spawnsprite;
    }


    public void addCoords(int x, int y) {
        coords.add(new int[] {x, y});
    }

    public List<int[]> getCoords() {
        return coords;  // Return all the coordinates
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
