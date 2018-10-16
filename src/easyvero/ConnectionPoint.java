package easyvero;

public class ConnectionPoint {
    /*
    A point where the component connects to the board.
    Coordinates are relative to the top left of the component
    */
    
    public int x;
    public int y;

    public ConnectionPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConnectionPoint other = (ConnectionPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
}
