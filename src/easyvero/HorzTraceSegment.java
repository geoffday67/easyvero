package easyvero;

public class HorzTraceSegment {
    public int row;
    public int start;
    public int end;
    
    public HorzTraceSegment(int row) {
        this.row = row;
    }
    
    public HorzTraceSegment(int row, int start, int end) {
        this.row = row;
        this.start = start;
        this.end = end;
    }
    
    public boolean contains(int x, int y) {
        return (y == row) && (x >= start) && (x <= end);
    }
}
