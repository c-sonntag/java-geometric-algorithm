package at.u4a.geometric_algorithms.algorithm;

import java.util.function.Supplier;
import javax.swing.ImageIcon;
import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;

public enum Algorithm {

    /* ENUM */

    Triangulation("Triangulation", "triangulation.png", at.u4a.geometric_algorithms.algorithm.Triangulation.Builder::new);


    /* PUBLIC STATIC */

    public static String iconRessource = "icons/algorithm/";

    /* PUBLIC */

    public final String tip;
    public final String icon;
    public final Supplier<AlgorithmBuilderInterface> supplier;

    /* PRIVATE */

    private Algorithm(String tip, String icon) {
        this(tip, icon, null);
    }

    private Algorithm(String tip, String icon, Supplier<AlgorithmBuilderInterface> supplier) {
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
