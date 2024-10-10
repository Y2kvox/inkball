package inkball;

import processing.data.*;
import java.util.*;

public class Level {
    public String layout;
    public int time;
    public int spawnInterval;
    public float scoreIncreaseModifier;
    public float scoreDecreaseModifier;
    public List<String> balls;

    public Level(JSONObject levelData) {
        this.layout = levelData.getString("layout");
        this.time = levelData.getInt("time");
        this.spawnInterval = levelData.getInt("spawn_interval");
        this.scoreIncreaseModifier = (float) levelData.getDouble("score_increase_from_hole_capture_modifier");
        this.scoreDecreaseModifier = (float) levelData.getDouble("score_decrease_from_wrong_hole_modifier");
        this.balls = new ArrayList<>();
        
        JSONArray ballsArray = levelData.getJSONArray("balls");
        for (int i = 0; i < ballsArray.size(); i++) {
            this.balls.add(ballsArray.getString(i));
        }
    }
}

