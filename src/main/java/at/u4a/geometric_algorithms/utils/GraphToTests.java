package at.u4a.geometric_algorithms.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import at.u4a.geometric_algorithms.algorithm.*;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.gui.layer.*;
import at.u4a.geometric_algorithms.gui.tools.Tool;

public class GraphToTests {

    static boolean activeDefault = false;

    static double Square_FACTOR = 5;

    static int Polygon_SIDE_NB = 8;
    static double Polygon_FACTOR = 5;

    static int Cloud_of_Points_NB = 6;
    static double Cloud_of_Points_FACTOR = 2;

    public static void defaultGraph(LayerManager lm) {
        Random rnd = new Random();

        //
        if (activeDefault) {
            lm.addLayerAndSelectIt(rectangle(rnd.nextLong(), Square_FACTOR));
            lm.addLayerAndSelectIt(polygon(rnd.nextLong(), Polygon_SIDE_NB, Polygon_FACTOR, false));
            lm.addLayerAndSelectIt(triangulation(polygon(rnd.nextLong(), Polygon_SIDE_NB, Polygon_FACTOR, false)));
            lm.addLayerAndSelectIt(triangulation(polygon(rnd.nextLong(), Polygon_SIDE_NB, Polygon_FACTOR, true)));
            lm.addLayerAndSelectIt(cloud_of_points(rnd.nextLong(), Cloud_of_Points_NB, Cloud_of_Points_FACTOR));
            lm.addLayerAndSelectIt(convex_envelope(cloud_of_points(rnd.nextLong(), Cloud_of_Points_NB, Cloud_of_Points_FACTOR)));
        }
    }

    /* ****** GENERATE LAYER MENU ****** */

    private static boolean makeOptionPane(JPanel panel, String title) {
        return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, panel, //
                title, JOptionPane.OK_CANCEL_OPTION);
    }

    private static void addPolygon(LayerManager lm) {
        Random rnd = new Random();
        JSpinner seed = new JSpinner(new SpinnerNumberModel(rnd.nextLong(), Long.MIN_VALUE, Long.MAX_VALUE, 1));
        JSpinner side = new JSpinner(new SpinnerNumberModel(6, 1, 1000000, 1));
        JSpinner factor = new JSpinner(new SpinnerNumberModel(1, 1, 100, 0.5));
        JCheckBox reverseCheck = new JCheckBox("");
        JPanel pRoot = new JPanel();
        pRoot.setLayout(new BoxLayout(pRoot, BoxLayout.Y_AXIS));
        
        JPanel p = new JPanel();
        p.add(Box.createHorizontalStrut(5));
        p.add(new JLabel("Side:"));
        p.add(side);
        p.add(Box.createHorizontalStrut(5));
        p.add(new JLabel("Factor:"));
        p.add(factor);
        p.add(Box.createHorizontalStrut(5));
        p.add(new JLabel("Reverse:"));
        p.add(reverseCheck);
        pRoot.add(p);
        
        JPanel pSeed = new JPanel();
        pSeed.add(new JLabel("Seed:"));
        pSeed.add(seed);
        pRoot.add(pSeed);
       
        //
        if (makeOptionPane(pRoot, "Polygon generator"))
            lm.addLayerAndSelectIt(polygon( //
                    ((Number) seed.getValue()).longValue(), //
                    ((Number) side.getValue()).intValue(), //
                    ((Number) factor.getValue()).doubleValue(), //
                    reverseCheck.isSelected())//
            );
    }

    public static void addGenerateLayerMenu(LayerManager lm, JMenu mnGenerate) {

        JMenuItem mntmMonotonePolygon = new JMenuItem("MonotonePolygon");
        mntmMonotonePolygon.addActionListener((arg) -> addPolygon(lm));
        mnGenerate.add(mntmMonotonePolygon);

    }

    /* ****** ALGORITHM LAYER ****** */

    private static AbstractLayer convex_envelope(AbstractLayer layer) {
        ConvexEnvelope.Builder ceb = new ConvexEnvelope.Builder();
        return ceb.builder(layer);
    }

    static AbstractLayer triangulation(AbstractLayer layer) {
        Triangulation.Builder tb = new Triangulation.Builder();
        return tb.builder(layer);
    }

    /* ****** SHAPE LAYER ****** */

    static AbstractLayer rectangle(long seed, double factor) {
        Random rnd = new Random(seed);
        return new GeometricLayer<Rectangle>(//
                new Rectangle(//
                        new Point(rnd.nextInt(100) + 100 * factor, rnd.nextInt(100) + 20 * factor), //
                        new Point(rnd.nextInt(100) + 20 * factor, rnd.nextInt(100) + 20 * factor))//
                , Tool.ShapeRectangle);
    }

    static AbstractLayer polygon(long seed, int nb, double factor, boolean reverse) {
        Random rnd = new Random(seed);

        Polygon poly = new Polygon();
        // poly.origin.set(300 + rnd.nextInt(400), 50 + rnd.nextInt(100));
        poly.origin.set(100 * factor, 50);

        poly.addPoint(new Point(0, 0));

        if (reverse)
            for (int i = 1; i <= nb; i++)
                poly.addPoint(new Point(-rnd.nextInt(80) * factor, 20 * i * factor));
        else
            for (int i = 1; i <= nb; i++)
                poly.addPoint(new Point(rnd.nextInt(80) * factor, 20 * i * factor));

        poly.addPoint(new Point(0, (nb + 1) * 20 * factor));

        if (reverse)
            for (int i = nb; i > 0; i--)
                poly.addPoint(new Point(rnd.nextInt(80) * factor, 20 * i * factor));
        else
            for (int i = nb; i > 0; i--)
                poly.addPoint(new Point(-rnd.nextInt(80) * factor, 20 * i * factor));

        return new GeometricLayer<Polygon>(poly, Tool.ShapeSimplePoligon);
    }

    private static AbstractLayer cloud_of_points(long seed, int nb, double factor) {
        Random rnd = new Random(seed);

        CloudOfPoints cloudOfPoint = new CloudOfPoints();
        cloudOfPoint.origin.set(200 + rnd.nextInt(100), 200 + rnd.nextInt(100));

        for (int i = 0; i < nb; i++) {
            cloudOfPoint.addPoint(new Point(200 - rnd.nextInt(200) * factor, 200 - rnd.nextInt(200) * factor));
        }

        return new GeometricLayer<CloudOfPoints>(cloudOfPoint, Tool.CloudOfPoint);
    }

}
