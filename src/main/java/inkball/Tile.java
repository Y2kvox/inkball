package inkball;

public class Tile{
    private TileContent content;
    boolean safe;
    boolean holepart;

    public Tile() {
        this.content = null;
    }

    public void setContent(TileContent item) {
        this.content = item;
    }

    public void clear() {
        this.content = null;
    }

    public boolean hasContent() {
        return content != null;
    }

    public TileContent getContent() {
        return content;
    }

    public String toString() {
        if (content != null) {
            return content.toString();
        } else {
            return "Empty";
        }
    }
    
    public void setSafe(){
        this.safe = true;
    }

    public boolean getSafe(){
        return this.safe;
    }

    public void setPart(){
        this.holepart = true;
    }

    public boolean getPart(){
        return this.holepart;
    }
    

   
}
