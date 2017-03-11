package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Segment;
import javafx.scene.paint.Color;

public class GeometricPainter extends PainterVisitor implements InterfaceGraphicVisitor {

    /**
     * La couleur d'un point a l'ecran.
     */
    private final static Color pointColor = Color.GRAY;

    /**
     * La couleur d'un segment a l'ecran.
     */
    private final static Color segmentColor = Color.BLUE;

    /// **
    // * La couleur d'une triangulation a l'ecran.
    // */
    // private final static Color triangulationColor = Color.CYAN;

    /**
     * La couleur d'un point selectionne a l'ecran.
     */
    private final static Color selectedColor = Color.RED;

    /**
     * La taille d'un point a l'ecran.
     */
    private final static int POINT_SIZE = 2;

    /* PAINTER */

    public void visit(Point p) {
        gc.setStroke(isSelected ? selectedColor : pointColor);
        //
        gc.fillOval((int) (p.x - POINT_SIZE), (int) (p.y - POINT_SIZE), 2 * POINT_SIZE + 1, 2 * POINT_SIZE + 1);
        gc.fillOval((int) (p.x - 2 * POINT_SIZE), (int) (p.y - 2 * POINT_SIZE), 2 * 2 * POINT_SIZE, 2 * 2 * POINT_SIZE);
    }

    /** TODO doit dessiner une ligne et non un segment ! */
    public void visit(Line l) {
        gc.setStroke(isSelected ? selectedColor : segmentColor);
        //
        gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
    }

    public void visit(Segment s) {
        gc.setStroke(isSelected ? selectedColor : segmentColor);
        //
        gc.strokeLine((int) s.a.x, (int) s.a.y, (int) s.b.x, (int) s.b.y);
    }



}