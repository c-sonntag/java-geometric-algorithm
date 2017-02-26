package at.u4a.geometric_algorithms.algorithm;

import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Polygon;

public class PolygonalOperation {
    
    static public CloudOfSegments triangulation(Polygon p) {
        if(p.getType() != Polygon.Type.Monotone)
            throw new RuntimeException("Triangulation need Monotone Polygon");
        
        CloudOfSegments cof = new CloudOfSegments();
        
        
        return cof;
    }

}
