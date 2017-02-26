package at.u4a.geometric_algorithms.graphic_adaptor;

import at.u4a.geometric_algorithms.geometric.Shape;
import javafx.scene.canvas.GraphicsContext;

public interface ShapePainter extends Shape {

    void paint(GraphicsContext graphicsContext);
    
}