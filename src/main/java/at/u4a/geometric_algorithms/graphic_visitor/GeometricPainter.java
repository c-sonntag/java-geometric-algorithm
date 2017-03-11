package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GeometricPainter {

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

    /* PAINTER OF GEOMETRIC */

    static public void paint(Point p, GraphicsContext gc, boolean selected) {
        gc.setStroke(selected ? selectedColor : pointColor);
        //
        gc.fillOval((int) (p.x - POINT_SIZE), (int) (p.y - POINT_SIZE), 2 * POINT_SIZE + 1, 2 * POINT_SIZE + 1);
        gc.fillOval((int) (p.x - 2 * POINT_SIZE), (int) (p.y - 2 * POINT_SIZE), 2 * 2 * POINT_SIZE, 2 * 2 * POINT_SIZE);

    }

    static public void paint(Line l, GraphicsContext gc, boolean selected) {
        gc.setStroke(selected ? selectedColor : segmentColor);
        //
        gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
    }
}