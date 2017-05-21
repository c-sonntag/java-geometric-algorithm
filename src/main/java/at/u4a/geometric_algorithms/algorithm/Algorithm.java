package at.u4a.geometric_algorithms.algorithm;

import java.util.function.Supplier;
import javax.swing.ImageIcon;
import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;

public enum Algorithm {

    /* ENUM */

    Triangulation("Triangulation", "triangulation.png", at.u4a.geometric_algorithms.algorithm.Triangulation.Builder::new), //
    ConvexEnvelope("ConvexEnvelope", "convex.png", at.u4a.geometric_algorithms.algorithm.ConvexEnvelope.Builder::new),
    SegmentIntersection("SegmentIntersection", "segment_intersection.png", at.u4a.geometric_algorithms.algorithm.SegmentIntersection.Builder::new),
    SegmentIntersectionQuadratic("SegmentIntersection", "segment_intersection.png", at.u4a.geometric_algorithms.algorithm.SegmentIntersectionQuadratic.Builder::new);


    /* PUBLIC STATIC */

    public static String iconRessource = "icons/algorithm/";

    /* PUBLIC */

    public final String tip;
    public final String icon;
    public final Supplier<InterfaceAlgorithmBuilder> supplier;

    /* PRIVATE */

    private Algorithm(String tip, String icon) {
        this(tip, icon, null);
    }

    private Algorithm(String tip, String icon, Supplier<InterfaceAlgorithmBuilder> supplier) {
        this.tip = tip;
        this.icon = icon;
        this.supplier = supplier;
    }

    private ImageIcon mutableImageIcon = null;
    
    public ImageIcon getImageIcon() {
        if (mutableImageIcon == null)
            mutableImageIcon = new ImageIcon(Algorithm.iconRessource + icon);
        return mutableImageIcon;
    }
}
