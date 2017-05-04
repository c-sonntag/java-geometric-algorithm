package at.u4a.geometric_algorithms.geometric.mapper;

import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.geometric.Point;

public class MappedSegment extends Segment implements InterfaceMapper {

    // Force variable for constructor
    public MappedSegment(Point a, Point b) {
        super(a,b);
    }
    
    @SuppressWarnings("unused")
    private MappedSegment() {
        throw new RuntimeException();
    }
    
}
