package inkball;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@SuppressWarnings("rawtypes")
public class Board {
    int width;
    int height;
    Tile[][] grid;
    boolean lock;
    int row;
    int col,s;
    int currentLevelIndex; // Track the current level index
    List<Map<String, Object>> levels;
    //ArrayList<Ball> balls;
    LinkedList<Ball> balls;
    List<Spawner> spawners;
    List<int[]> xy;
    List<Hole> holes = new ArrayList<>();
    List<Brick> bricks = new ArrayList<>();
    
    


    public Board(int width, int height, int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
        this.width = width;
        this.height = height;
        grid = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new Tile(); // Ensure each tile is initialized
            }
        }
        this.balls = new LinkedList<>();
        this.spawners = new ArrayList<>(); // Initialize the spawners list
        xy = new ArrayList<>();
        this.s = 0;
    }
    

    private void placeWall(int x, int y, int wallType) {
        if (!grid[y][x].getSafe()) {
            Wall wall = new Wall(wallType);
            setWallType(wall, wallType);
            placeItem(x, y, wall);
        }else {
            placeItem(x, y, new TileWithImage());
        }
    }

    
    @SuppressWarnings("unchecked")
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

    public void addHole(Hole h){
        holes.add(h);
    }

    public void addBrick(Brick b){
        bricks.add(b);
    }

    public List<Hole> getHoleList(){
        return holes;
    }

    public List<Brick> getBrickList(){
        return bricks;
    }

    

    public void loadLevel(String filename) {
        xy.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int y = 0;

            while ((line = br.readLine()) != null && y < height) {
                for (int x = 0; x < line.length() && x < width; x++) {
                    char ch = line.charAt(x);

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
                                xy.add(new int[] {colorIndex,x,y});
                                
                            }
                            grid[y][x+1].setSafe();
                            placeWall(x+1, y, 0); // ensures that the next tile has a tile image, walltype doesn't matter in this case
                            break;
                        
                        case 'H':
                            if (x + 1 < line.length() && y + 1 < height) {
                                char holeChar = line.charAt(x + 1);
                                int holeIndex = Character.getNumericValue(holeChar);
                                Hole hole = new Hole(holeIndex);
                                placeItem(x, y, hole);
                                hole.setX(x);
                                hole.setY(y);
                                addHole(hole);
                                //System.out.println("Hole coords: "+hole.getX()+", "+ hole.getY());
                                
                            } else {
                                placeItem(x, y, new Hole(0));
                            }
                            // Define the offsets for the tiles to be marked as safe and part of the hole
                            int[][] offsets = {
                                {0, 0}, {0, 1},
                                {1, 0}, {1, 1}
                            };

                            // Iterate through each offset and apply the methods
                            for (int[] offset : offsets) {
                                int offsetY = offset[0];
                                int offsetX = offset[1];

                                grid[y + offsetY][x + offsetX].setSafe(); // Mark tile as safe
                            }

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
                        case 'o':
                            placeItem(x, y, new Brick(1));
                            addBrick(new Brick(1));
                            break;
                        case 'b':
                            placeItem(x, y, new Brick(2));
                            addBrick(new Brick(2));
                            break;
                        case 'g':
                            placeItem(x, y, new Brick(3));
                            addBrick(new Brick(3));
                            break;
                        case 'y':
                            placeItem(x, y, new Brick(4));
                            addBrick(new Brick(4));
                            break;
                        case 'w':
                            placeItem(x, y, new Brick(0));
                            addBrick(new Brick(0));
                            break;
                        

                            //spawner
                        case 'S':
                            Spawner spawn = new Spawner();
                            spawn.addCoords(x, y);
                            placeItem(x, y, spawn);
                            spawners.add(spawn);
                            break;

                            //tile image
                        case ' ':
                            if(!grid[y][x].getSafe()){
                                placeItem(x, y, new TileWithImage());
                            }
                            break;
                        default:
                            break;
                    }
                    //System.out.print(grid[y][x].getContent() + ", ");
                }
                //System.out.println();
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    

    //spawners coords in a 2d list --> allcords = [[x1,y1], [x2,y2],...,[xN,yN]]
    public List<int[]> getAllSpawnerCoords() {
        List<int[]> allCoords = new ArrayList<>();
        for (Spawner spawner : spawners) {
            allCoords.addAll(spawner.getCoords());
        }
        return allCoords;
    }
    

    //config file pull level array
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


    //level from json
    public void loadCurrentLevel() {
        if (this.currentLevelIndex < levels.size()) {
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            String layoutFile = (String) levelData.get("layout");
            loadLevel(layoutFile);
            Ball.setLevel(layoutFile);
            System.out.println("file name of config level arrayindex: "+currentLevelIndex+" is "+layoutFile);
        } else {
            System.out.println("No more levels to load.");
        }
    }

    //advance to the next level
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

        return interval* 1000;
    }

    // Ball list
    public void addBalls() {
        if (currentLevelIndex < levels.size()) {
            
            // Add balls based on xy
            for (int[] num : xy) {
                balls.add(new Ball(num[0], num[1], num[2]));
                System.out.print("Ball" + num[0] + ", ");
                System.out.println(num[1] + ", " + num[2]);
            }
            
            Map<String, Object> levelData = levels.get(currentLevelIndex);
            @SuppressWarnings("unchecked")
            List<String> colors = (List<String>) levelData.get("balls");
            for (String str : colors) {
                Integer n = getColorCode(str);
                int[] s = getRandomSpawnerCoords();
                
                if (s != null) {
                    //System.out.print("Ball" + n + ", ");
                    balls.add(new Ball(n, s[0], s[1]));
                    //System.out.println(s[0] + ", " + s[1]);
                }
            }

            
        } else {
            System.out.println("No balls available to load.");
        }
    }

    //coords = [[x1,y1], [x2,y2],...[xN,yN]] --> returns [xM,yM]
    private int[] getRandomSpawnerCoords() {
        List<int[]> coords = getAllSpawnerCoords();
        if (coords.isEmpty()) {
            return null; // Return null if no coordinates are available
        }
        Random random = new Random();
        int index = random.nextInt(coords.size());
        return coords.get(index); // Return the randomly selected coordinates
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
    
    public LinkedList<Ball> getBalls() {
        return balls;
    }

    public void resetBalls() {
        lock = true;
        balls.clear();
        addBalls();
    }
    
    public void draw(App app) {
        // First, draw the tile backgrounds
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid[y][x];
                if (tile.hasContent()) {
                    // Draw the tile image if it has a specific background like wall or tile image
                    if (tile.getContent() instanceof TileWithImage) {
                        tile.getContent().draw(app, x, y);
                    }
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
    
       
    
}