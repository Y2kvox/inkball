package inkball;

public class Tile<T extends TileContent> {
    private T content;   // Generic type T constrained to TileContent
    boolean safe;

    public Tile() {
        this.content = null;
    }

    public void setContent(T item) {
        this.content = item;
    }

    public void clear() {
        this.content = null;
    }

    public boolean hasContent() {
        return content != null;
    }

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
