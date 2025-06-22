package inkball;


/**
 * Updates the pixel positions based on the hole's position.
 * This method recalculates the pixel positions within the hole's area.
 */
public class Tile<T extends TileContent> {
    private T content;   // Generic type T constrained to TileContent
    boolean safe;

    /**
     * Constructs an empty Tile.
     * The content is initialized to null.
     */
    public Tile() {
        this.content = null;
    }

    /**
     * Sets the content of the tile.
     * 
     * @param item the content to set.
     */
    public void setContent(T item) {
        this.content = item;
    }


    /**
     * Checks if the tile has content.
     * 
     * @return true if the tile has content, false otherwise.
     */
    public boolean hasContent() {
        return content != null;
    }

    /**
     * Returns the content of the tile.
     * 
     * @return the content of the tile, or null if the tile is empty.
     */
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        if (content != null) {
            return content.toString();
        } else {
            return "Empty";
        }
    }

    public void setSafe() {
        this.safe = true;
    }

    public boolean getSafe() {
        return this.safe;
    }

    public void setUnsafe() {
        this.safe = false;
    }
}
