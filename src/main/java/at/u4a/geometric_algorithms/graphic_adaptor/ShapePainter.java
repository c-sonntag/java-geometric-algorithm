package at.u4a.geometric_algorithms.graphic_adaptor;

import javafx.scene.canvas.GraphicsContext;

public interface ShapePainter extends Shape {

    void paint(GraphicsContext graphicsContext);
    
}