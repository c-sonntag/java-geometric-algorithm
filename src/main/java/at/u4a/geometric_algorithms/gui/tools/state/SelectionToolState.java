package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerManager;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class SelectionToolState extends ToolState {

    /* CONST STATIC */

    static protected final long MOUSE_MOVE_SCAN_MS = 10;

    //

    static protected final Cursor CURSOR_DEFAULT = Cursor.DEFAULT;
    static protected final Cursor CURSOR_MOVE = Cursor.MOVE;
    static protected final Cursor CURSOR_HAVE = Cursor.OPEN_HAND;

    /* */

    protected LayerManager lm = null;
    protected Drawer d = null;

    /* */

    public void init(Drawer drawer) {
        d = drawer;
        lm = drawer.getDS().getLayerManager();
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
    protected Point currentPoint = new Point();

    //

    protected Point lastCurrentPoint = new Point();
    protected Point translatePoint = new Point();

    protected boolean inMove = false;

    //

    /* */

    protected boolean isMove() {
        return inMove;
    }

    protected boolean isHave() {
        return overlay != null;
    }

    /* */

    private void setCursor(Drawer d) {
        if (d == null)
            return;
        //
        if (isMove())
            d.setCursor(CURSOR_MOVE);
        else if (isHave())
            d.setCursor(CURSOR_HAVE);
        else
            d.setCursor(CURSOR_DEFAULT);
    }

    public void mouseEntered(Drawer drawer) {
        setCursor(drawer);
    }

    /* */

    protected void setSelected() {
        if (overlay != null)
            lm.setSelectedLayer(overlay);
    }

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        if (isHave()) {
            if (!inMove) {

                //
                lastCurrentPoint.set(currentPoint);
                inMove = true;

                //
                setCursor(d);
            }
        }
        setSelected();
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {

        if (inMove) {
            inMove = false;

            //
            setCursor(d);
        }
    }

    private long moveScanChrono = 0;

    protected void findOverlay(Point p) {
        overlay = lm.getTopContainedShape(p);
    }

    protected void translate(Point p) {
        overlay.translate(p);
    }

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
            findOverlay(currentPoint);

            //
            setCursor(d);

        } else if (isHave()) {
            translatePoint.set(currentPoint.x - lastCurrentPoint.x, currentPoint.y - lastCurrentPoint.y);
            translate(translatePoint);
            lastCurrentPoint.set(currentPoint);
            context.repaint();
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
