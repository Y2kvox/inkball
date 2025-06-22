package inkball;

/**
 * Represents the content that can be placed on a tile in the Inkball game.
 * Classes implementing this interface must provide a method to draw the content on the game board.
 */
public interface TileContent {
void draw(App app, int x, int y);
}