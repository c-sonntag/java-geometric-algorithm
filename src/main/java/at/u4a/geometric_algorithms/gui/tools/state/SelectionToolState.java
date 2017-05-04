package at.u4a.geometric_algorithms.gui.tools.state;

import java.util.List;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerManager;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SelectionToolState extends ToolState {

    /* CONST STATIC */

    private final long MOUSE_MOVE_SCAN_MS = 10;
    private final Cursor DEFAULT_CURSOR = Cursor.DEFAULT;

    /* */

    private LayerManager lm = null;
    private Drawer d = null;

    /* */

    public void init(Drawer drawer) {
        d = drawer;
        lm = drawer.getDS().getLayerMannager();
    }

    /* TOOLBAR */

    public void valid(Drawer drawer) {
    }

    public void cancel(Drawer drawer) {
    }

    public Boolean needValidOperation() {
        return false;
    }

    /* MOUSE */

    protected AbstractLayer overlay = null;
    protected InterfaceGeometric ig = null;
    protected boolean inMove = false;

    protected Point currentPoint = new Point();

    public void mouseEntered(Drawer drawer) {
        if (ig != null)
            drawer.setCursor(Cursor.MOVE);
        else
            drawer.setCursor(DEFAULT_CURSOR);
    }

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {

        if (ig != null) {
            inMove = true;
            // startPoint = new Point(event.getX(), event.getY());
            // translatePoint = new Point(0, 0);
        }

        /*
         * if (!isLeftClick(event)) return; if (currentState != State.Started)
         * return; // addPlace(context, event.getX(), event.getY());
         */
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {

        // inMove = false;

        /*
         * if (!isLeftClick(event)) return; // if (currentState ==
         * State.Waiting) { currentState = State.Started;
         * poly.origin.set(event.getX(), event.getY()); addPlace(context,
         * event.getX(), event.getY()); } inPlace = false;
         */
    }

    private long moveScanChrono = 0;

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {

        //
        currentPoint.set(event.getX(), event.getY());

        //
        if (!inMove) {

            //
            final long currentTimeMs = java.lang.System.currentTimeMillis();
            if ((currentTimeMs - moveScanChrono) < MOUSE_MOVE_SCAN_MS)
                return;
            moveScanChrono = currentTimeMs;

            //
            if(lm == null)
                return;
            
            ///
            overlay = lm.getTopContainedShape(currentPoint);

            //
            if ((overlay != null) && (d != null)) {
                d.setCursor(Cursor.CROSSHAIR);
            } else {
                d.setCursor(DEFAULT_CURSOR);
            }

            // this.x = event.getX();
            // this.y = event.getY();
            // context.repaint();
        }

    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        // painter.visit(new Rectangle(new Point(x, y), new Point(200, 200)));
        /*
         * GraphicsContext context = painter.getGrapicsContext();
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5); context.strokeRect(x, y,
         * 200, 200); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
