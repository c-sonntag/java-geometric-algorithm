package at.u4a.geometric_algorithms.graphic_adaptor;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import javafx.scene.canvas.GraphicsContext;

public interface InterfaceShapePainter {

    public AbstractShape getShape();

    public void paint(GraphicsContext graphicsContext);

}