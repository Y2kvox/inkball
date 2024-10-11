package inkball;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import processing.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Board {
    int width;
    int height;
    Tile[][] grid;
    boolean lock;
    int row;
    int col;
    int ballx, bally;
    int currentLevelIndex; // Track the current level index
    List<Map<String, Object>> levels;
    ArrayList<Ball> balls = new ArrayList<>();
    List<Spawner> spawners;



    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new Tile(); // Ensure each tile is initialized
            }
        }
        this.currentLevelIndex = 0;
        this.balls = new ArrayList<>(); // Initialize the balls list
        this.spawners = new ArrayList<>(); // Initialize the spawners list
    }
    

    private void placeWall(int x, int y, int wallType) {
        if (!grid[y][x].getSafe()) {
            Wall wall = new Wall(wallType);
            setWallType(wall, wallType);
            placeItem(x, y, wall);
        } else {
            placeItem(x, y, new TileWithImage());
        }
    }

    public void placeItem(int x, int y, TileContent item) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x].setContent(item);
        } else {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
    }

    public void setWallType(Wall wall, int num) {
        wall.wallType = num;
    }

    public void loadLevel(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int y = 0;

            while ((line = br.readLine()) != null && y < height) {
                for (int x = 0; x < line.length() && x < width; x++) {
                    char ch = line.charAt(x);
                    Tile tile = grid[y][x];

                    switch (ch) {
                        case 'X':
                            placeItem(x, y, new Wall(0));
                            break;
                        
                            // ball
                        case 'B':
                            if (x + 1 < line.length()) {
                                char colorChar = line.charAt(x + 1);
                                int colorIndex = Character.getNumericValue(colorChar);
                                placeItem(x, y, new TileWithImage());
                                balls.add(new Ball(colorIndex, x, y));
                            }
                            ballx = y;
                            bally = x;
                            grid[y][x+1].setSafe();
                            break;
                        
                        case 'H':
                            if (x + 1 < line.length() && y + 1 < height) {
                                char holeChar = line.charAt(x + 1);
                                int holeIndex = Character.getNumericValue(holeChar);
                                placeItem(x, y, new Hole(holeIndex));
                                
                            } else {
                                placeItem(x, y, new Hole(0));
                            }
                            //setsafe
                            grid[y][x].setSafe();  // Mark current tile as safe
                            grid[y][x+1].setSafe();
                            grid[y+1][x+1].setSafe();
                            grid[y+1][x].setSafe();
                            //holepart
                            grid[y][x].setPart();
                            grid[y][x+1].setPart();
                            grid[y+1][x+1].setPart();
                            grid[y+1][x].setPart();
                            break;
                        
                        
                            case '1':
                            placeWall(x, y, 1);
                            break;
                        case '2':
                            placeWall(x, y, 2);
                            break;
                        case '3':
                            placeWall(x, y, 3);
                            break;
                        case '4':
                            placeWall(x, y, 4);
                            break;

                            //spawner
                        case 'S':
                            Spawner spawn = new Spawner();
                            spawn.addCoords(x, y);
                            placeItem(x, y, spawn);
                            spawners.add(spawn);
                            break;

                            // Empty space, no content, but tile will still be drawn
                        case ' ':
                            if(!grid[y][x].getSafe()){
                                placeItem(x, y, new TileWithImage());
                            }
                            break;
                        default:
                            break;
                    }
                    System.out.print(grid[y][x].getContent() + ", ");
                }
                System.out.println();
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void draw(App app) {

        app.fill(0);
        app.rect(0,25,200,180);
        // First, draw the tile backgrounds
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid[y][x];
                if (tile.hasContent()) {
                    // Draw the tile image if it has a specific background like wall or tile image
                    if (tile.getContent() instanceof TileWithImage) {
                        tile.getContent().draw(app, x, y);
                    }
                } else {
                    // You can also handle cases for default tile backgrounds here
                }
            }
        }
    
        // draw the objects like balls, spawners, and holes on tile backgrounds
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid[y][x];
                if (tile.hasContent()) {
                    // Draw objects that should appear on top (Ball, Hole, Spawner)
                    if (!(tile.getContent() instanceof TileWithImage)) {
                        tile.getContent().draw(app, x, y);
                    }
                }
            }
        }
    }

    //get all spawners coords in a 2d list --> allcords = [[x1,y1], [x2,y2],...,[xN,yN]]
    public List<int[]> getAllSpawnerCoords() {
        List<int[]> allCoords = new ArrayList<>();
        for (Spawner spawner : spawners) {
            allCoords.addAll(spawner.getCoords());
        }
        return allCoords;
    }
    

    //level file pull
    @SuppressWarnings("unchecked")
    public void loadLevelFromJson(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> jsonData = mapper.readValue(new File(filename), Map.class);

            if (jsonData.containsKey("levels")) {
                levels = (List<Map<String, Object>>) jsonData.get("levels");
                loadCurrentLevel(); // Load the current level upon initializing
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the current level based on the currentLevelIndex
    public void loadCurrentLevel() {
        if (currentLevelIndex < levels.size()) {
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            String layoutFile = (String) levelData.get("layout");
            loadLevel(layoutFile);
        } else {
            System.out.println("No more levels to load.");
        }
    }

    // Method to advance to the next level
    public void loadNextLevel() {
        currentLevelIndex++;
        loadCurrentLevel(); // Load the next level
    }

    //time pull
    public Integer getTime(){
        Integer time = 0;
        if (currentLevelIndex < levels.size()) {
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            time = (Integer) levelData.get("time");
        } else {
            System.out.println("No time available to load.");
        }

        return time;
    }

    public Integer getTimeForApp(){
        Integer time = getTime();
        time = (time >= 0) ? time : 0;
        return time;
    }

    // spawn interval pull
    public Integer getInterval(){
        Integer interval = 0;
        if (currentLevelIndex < levels.size()) {
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            interval = (Integer) levelData.get("spawn_interval");
        } else {
            System.out.println("No spawn interval available to load.");
        }

        return interval;
    }

    //ball list
    public void addBalls(){
        int[] s;
        List<int[]> coords = getAllSpawnerCoords();
        Random random = new Random();
        if (coords.isEmpty()){
            throw new IllegalStateException("No coordinates available.");
        }else{
            int index = random.nextInt(coords.size());
            s =  coords.get(index);
        }
        if (currentLevelIndex < levels.size()) {
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            List<String> colors = (List<String>) levelData.get("balls");

            for(String str: colors){
                Integer n = getColorCode(str);
                System.out.print("Ball"+n+", ");
                balls.add(new Ball(n, s[0],s[1]));
            }
            System.out.print("Currently in the array");
        } else {
            System.out.println("No balls available to load.");
        }
    }

    //color convertor
    public Integer getColorCode(String str) {
    Map<String, Integer> colorMap = new HashMap<>();
    colorMap.put("blue", 2);
    colorMap.put("grey", 0);
    colorMap.put("orange", 1);
    colorMap.put("green", 3);
    colorMap.put("yellow", 4);

    return colorMap.get(str);
    }
    
    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void resetBalls() {
        balls.clear();
        addBalls();
    }
    
    
    
       
    
}
