package at.u4a.geometric_algorithms.gui.layer;

import javafx.scene.paint.Color;
import java.util.EnumSet;
import java.util.Vector;
import java.util.Random;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;

public abstract class AbstractLayer {

    static public enum AuthorizedAction {
        Delete, ApplyAlgorithm, DeleteAlgorithm
    };

    /* BASIC CONFIGURATION */
    
    public static EnumSet<AuthorizedAction> getAllAuthorization() {
        return EnumSet.allOf(AuthorizedAction.class);
    }
    
    public static EnumSet<AuthorizedAction> getAuthorizationForGeometricLayer() {
        return EnumSet.of(AuthorizedAction.Delete,AuthorizedAction.ApplyAlgorithm);
    }
    
    public static EnumSet<AuthorizedAction> getAuthorizationForAlgorithmLayer() {
        return EnumSet.of(AuthorizedAction.Delete,AuthorizedAction.ApplyAlgorithm,AuthorizedAction.DeleteAlgorithm);
    }
    
    /* PUBLIC STRUCT */

    public static class ColorSet {
        public Color color;
        public String name;

        public ColorSet(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        /** @see http://stackoverflow.com/a/30466507 */
        public java.awt.Color getAwtColor() {
            return new java.awt.Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getOpacity() * 255));
            // Fx : return javafx.scene.paint.Color.rgb(color.getRed(),
            // color.getGreen(), color.getBlue(), color.getAlpha() / 255.0);
        }

        public void setAwtColor(java.awt.Color newColor) {
            color = javafx.scene.paint.Color.rgb(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), newColor.getAlpha() / 255.0);
        }

        /*  */

        private static Random rnd;

        /** @see http://stackoverflow.com/a/4247219 */
        public static Color rand() {

            if (rnd == null) {
                rnd = new Random();
                rnd.setSeed(11235813);
            }

            final float hue = rnd.nextFloat();
            // Saturation between 0.1 and 0.3
            final float saturation = (rnd.nextInt(3000) + 7000) / 10000f;
            final float luminance = 0.9f;
            final Color color = Color.hsb(hue*360, saturation, luminance);

            return color;
        }
    }

    /* PRIVATE VAR */

    private String layerName;

    private AbstractLayer parent;

    private boolean active;

    /* PROTECTED */

    protected Vector<ColorSet> colors;

    protected final EnumSet<AuthorizedAction> authorized;

    /* ABSTRACT PUBLIC FUNCTION - LAYER */

    abstract public AbstractShape getShape();
    
    abstract public AbstractAlgorithm getAlgorithm();

    abstract public LayerCategory getCategory();
    
    abstract public void restoreAuthorization();

    abstract public String getLayerType();

    abstract public ImageIcon getLayerTypeIcon();

    abstract public boolean isContener();

    abstract public boolean isDeletable();

    abstract public Vector<AbstractLayer> getSubLayer();

    abstract public void accept(InterfaceGraphicVisitor visitor);
    
    abstract public int hashCode();

    /* ABSTRACT PUBLIC FUNCTION - SHAPES */

    abstract public void translate(Point p);

    abstract public boolean contains(Point p);

    abstract public InterfaceMapper getTopContainMappedComposition(Point p);

    /** @todo InterfaceGeometric getContains(Point p); */

    /* PUBLIC FUCNTION */

    public AbstractLayer(EnumSet<AuthorizedAction> authorized) {
        this(null, authorized); 
    }

    public AbstractLayer(AbstractLayer parent,EnumSet<AuthorizedAction> authorized) {
        this.parent = parent;
        this.active = true;
        this.authorized = authorized; 
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
