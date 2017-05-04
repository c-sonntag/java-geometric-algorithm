package at.u4a.geometric_algorithms.geometric.mapper;

import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;

public class MappedLine extends Line implements InterfaceMapper {

    // Force variable for constructor
    public MappedLine(Point a, Point b) {
        super(a,b);
    }
    
    @SuppressWarnings("unused")
    private MappedLine() {
        throw new RuntimeException();
    }
    
}
