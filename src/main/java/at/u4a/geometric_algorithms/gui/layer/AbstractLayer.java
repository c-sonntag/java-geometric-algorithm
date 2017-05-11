package at.u4a.geometric_algorithms.gui.layer;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;

public abstract class AbstractLayer {

    static public enum AuthorizedAction {
        Delete, ApplyAlgorithm
    };

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

    protected final EnumSet<AuthorizedAction> authorized = EnumSet.allOf(AuthorizedAction.class);

    /* ABSTRACT PUBLIC FUNCTION - LAYER */

    abstract public AbstractShape getShape();

    abstract public LayerCategory getCategory();

    abstract public String getLayerType();

    abstract public ImageIcon getLayerTypeIcon();

    abstract public boolean isContener();

    abstract public boolean isDeletable();

    abstract public Vector<AbstractLayer> getSubLayer();

    abstract public void accept(InterfaceGraphicVisitor visitor);

    /* ABSTRACT PUBLIC FUNCTION - SHAPES */

    abstract public void translate(Point p);

    abstract public boolean contains(Point p);

    abstract public InterfaceMapper getTopContainMappedComposition(Point p);

    /** @todo InterfaceGeometric getContains(Point p); */

    /* PUBLIC FUCNTION */

    public AbstractLayer() {
        this(null);
    }

    public AbstractLayer(AbstractLayer parent) {
        this.parent = parent;
        this.active = true;
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

    public String toString() {
        return getLayerName();
    }

    public EnumSet<AuthorizedAction> getAuthorized() {
        return authorized;
    }

    public boolean isAuthorized(AuthorizedAction aa) {
        return authorized.contains(aa);
    }

}
