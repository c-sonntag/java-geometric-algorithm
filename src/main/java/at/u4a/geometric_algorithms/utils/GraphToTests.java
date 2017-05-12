package at.u4a.geometric_algorithms.utils;

import java.util.Random;

import at.u4a.geometric_algorithms.algorithm.*;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.gui.layer.*;
import at.u4a.geometric_algorithms.gui.tools.Tool;

public class GraphToTests {

    public static void addGraph(LayerManager layerManager) {
        layerManager.addLayer(rectangle());
        //layerManager.addLayer(polygon());
        layerManager.addLayer(triangulation(polygon()));
    }

    static AbstractLayer rectangle() {
        return new GeometricLayer<Rectangle>(new Rectangle(new Point(10, 10), new Point(100, 100)), Tool.ShapeRectangle);
    }

    static int POLYGON_SIDE_NB = 3;
    static double POLYGON_FACTOR = 3;

    static AbstractLayer polygon() {

        Random rnd = new Random();

        Polygon poly = new Polygon();
        poly.origin.set(300, 100);

        poly.addPoint(new Point(0, 0));

        for (int i = 1; i <= POLYGON_SIDE_NB; i++)
            poly.addPoint(new Point(rnd.nextInt(80) * POLYGON_FACTOR, 20 * i * POLYGON_FACTOR));

        poly.addPoint(new Point(0, (POLYGON_SIDE_NB + 1) * 20 * POLYGON_FACTOR));

        for (int i = POLYGON_SIDE_NB; i > 0; i--)
            poly.addPoint(new Point(-rnd.nextInt(80) * POLYGON_FACTOR, 20 * i  * POLYGON_FACTOR));

        return new GeometricLayer<Polygon>(poly, Tool.ShapeSimplePoligon);
    }

    static AbstractLayer triangulation(AbstractLayer layer) {
        Triangulation.Builder tb = new Triangulation.Builder();
        return tb.builder(layer);
    }

}
