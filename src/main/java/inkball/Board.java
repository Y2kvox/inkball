package inkball;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class Board {
    int width;
    int height;
    Tile[][] grid;
    boolean lock;
    int row;
    int col;
    int ballx, bally;



    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new Tile();
            }
        }
    }

    public void placeItem(int x, int y, TileContent item) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x].setContent(item);
        } else {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
    }

    public void setWallType(Wall wall, int num){
        wall.wallType = num;
    }

    public void loadLevel(String filename) {
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
                                placeItem(x, y, new Ball(colorIndex,x,y));
                                //placeItem(x+1, y, new TileWithImage());
                            } else {
                                placeItem(x, y, new Ball(0,x,y));
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
                            if (!grid[y][x].getSafe()) {
                                Wall wall1 = new Wall(1);
                                setWallType(wall1, 1);  // Set wall type to 1
                                placeItem(x, y, wall1);
                                System.out.println("Placed Wall of type 1 at (" + x + ", " + y + ")");
                            } else {
                                placeItem(x, y, new TileWithImage());
                            }
                            break;

                        case '2':
                            if (!grid[y][x].getSafe()) {
                                Wall wall2 = new Wall(2);
                                setWallType(wall2, 2);  // Set wall type to 2
                                placeItem(x, y, wall2);
                                System.out.println("Placed Wall of type 2 at (" + x + ", " + y + ")");
                            } else {
                                placeItem(x, y, new TileWithImage());
                            }
                            break;

                        case '3':
                            if (!grid[y][x].getSafe()) {
                                Wall wall3 = new Wall(3);
                                setWallType(wall3, 3);  // Set wall type to 3
                                placeItem(x, y, wall3);
                                System.out.println("Placed Wall of type 3 at (" + x + ", " + y + ")");
                            } else {
                                placeItem(x, y, new TileWithImage());
                            }
                            break;

                        case '4':
                            if (!grid[y][x].getSafe()) {
                                Wall wall4 = new Wall(4);
                                setWallType(wall4, 4);  // Set wall type to 4
                                placeItem(x, y, wall4);
                                System.out.println("Placed Wall of type 4 at (" + x + ", " + y + ")");
                            } else {
                                placeItem(x, y, new TileWithImage());
                            }
                            break;

                            //spawner
                        case 'S':
                            placeItem(x, y, new Spawner());
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
    
        // Now, draw the objects like balls, spawners, and holes on top of the tile backgrounds
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

    public void loadLevelFromJson(String filename){
        ObjectMapper mapper = new ObjectMapper();

        try{
            Map<String,Object> jsonData = mapper.readValue(new File(filename), Map.class);

            if (jsonData.containsKey("levels")){
                List<Map<String, Object>> levels = (List<Map<String, Object>>) jsonData.get("levels");
                Map<String, Object> firstLevel = levels.get(0);
                String layoutFile = (String) firstLevel.get("layout");
                loadLevel(layoutFile);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
       
    
}
