package at.u4a.geometric_algorithms.gui.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LayerMannager implements Iterable<AbstractLayer> {
    
    private final List<AbstractLayer> layers = new ArrayList<AbstractLayer>(); 
    
    
    public List<AbstractLayer> getContain() {
        
        List<AbstractLayer> contain = ArrayList<AbstractLayer>();
        
        for (int i = shapes.size(); i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(x, y))
                return shape;
        }
        return null;
    }
    
    public Iterator<AbstractLayer> iterator() {
        return layers.iterator();
    }

}

