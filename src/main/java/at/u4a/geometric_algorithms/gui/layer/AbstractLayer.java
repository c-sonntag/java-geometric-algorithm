package at.u4a.geometric_algorithms.gui.layer;

import java.awt.Color;
import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public abstract class AbstractLayer {

    /* PUBLIC STRUCT */

    public class ColorSet {
        public Color color;
        public String name;
    }

    /* PRIVATE VAR */
    
    private String layerName;

    private AbstractLayer parent;
    
    private boolean active;

    /* PROTECTED */

    protected Vector<ColorSet> colors;

    /* ABSTRACT PUBLIC FUNCTION */

    abstract public AbstractShape getShape();

    abstract public LayerCategory getCategory();

    abstract public String getLayerType();

    abstract public String getLayerTypeIconName();

    abstract public Boolean isContener();

    abstract public Boolean isDeletable();

    abstract public Vector<AbstractLayer> getSubLayer();

    abstract public void accept(InterfaceShapePainterVisitor visitor);

    /* PUBLIC FUCNTION */

    public AbstractLayer() {
        this(null);
    }

    public AbstractLayer(AbstractLayer parent) {
        this.parent = parent;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public Vector<ColorSet> getColor() {
        return colors;
    }

    public AbstractLayer getParent() {
        return parent;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean b) {
        active = b;
    }

}
