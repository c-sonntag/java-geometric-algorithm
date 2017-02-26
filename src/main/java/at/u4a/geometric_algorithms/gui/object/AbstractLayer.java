package at.u4a.geometric_algorithms.gui.object;

import java.awt.Color;
import java.util.Vector;

public abstract class AbstractLayer {

    /* PUBLIC STRUCT */

    public class ColorSet {
        public Color color;
        public String name;
    }

    /* PRIVATE VAR */

    private String layerName;

    /* PROTECTED */

    protected Vector<ColorSet> colors;
    
    /* ABSTRACT PUBLIC FUNCTION */

    abstract public String getLayerType();

    abstract public String getLayerIconName();
    
    abstract public Boolean isContener();
    
    abstract public Vector<AbstractLayer> getSubLayer();

    /* PUBLIC FUCNTION */

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public Vector<ColorSet> getColor() {
        return colors;
    }

}
