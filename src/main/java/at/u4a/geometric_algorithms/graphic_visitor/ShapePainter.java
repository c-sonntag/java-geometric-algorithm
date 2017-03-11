package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ShapePainter extends GeometricPainter implements InterfaceShapePainterVisitor {

    public void visit(Polygon poly) {

    }

    public void visit(CloudOfSegments clouds) {

    }

    public void visit(Rectangle poly) {
        gc.setStroke(Color.RED);
        //
        gc.strokeRect(poly.origin.x, poly.origin.y, poly.size.x, poly.size.y);
    }

}
