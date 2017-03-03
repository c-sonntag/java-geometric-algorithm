package at.u4a.geometric_algorithms.gui.tools;

import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.function.Supplier;

import at.u4a.geometric_algorithms.gui.tools.state.NullToolState;

public enum Tool {
   

    /*
     * Rectangle(KeyCode.R, RectangleDrawerState0::new), Circle(KeyCode.C,
     * CircleDrawerState0::new), Escape(KeyCode.ESCAPE, NullDrawerState::new);
     */

    Selection("Outil Selection", KeyCode.V, ToolCategory.Transform, "selection.png"), //
    DirectSelection("Outil Selection directe", KeyCode.A, ToolCategory.Transform, "selection_direct.png"), //

    CloudOfPoint("Outil Nuage de Point", KeyCode.P, ToolCategory.ShapeSetter, "cloud_of_point.png"), //
    LotOfSegment("Outil Ensemble de Segments", KeyCode.S, ToolCategory.ShapeSetter, "lot_of_segment.png"), //
    TraceOfPoint("Outil Tracé de Point", KeyCode.T, ToolCategory.ShapeSetter, "trace_of_point.png"), //

    ShapeRectangle("Outil Rectangle", EnumSet.of(KeyCode.SHIFT, KeyCode.R), ToolCategory.Shape, "shape_square.png"), //
    ShapeElipse("Outil Elipse", EnumSet.of(KeyCode.SHIFT, KeyCode.E), ToolCategory.Shape, "shape_circle.png"), //
    ShapeConvexePoligon("Outil Polygone Convexe", EnumSet.of(KeyCode.SHIFT, KeyCode.C), ToolCategory.Shape, "shape_convexe_poligon.png"), //

    ShapeSimplePoligon("Outil Polygone Simple", EnumSet.of(KeyCode.SHIFT, KeyCode.S), ToolCategory.Shape, "shape_simple_poligon.png"), //
    ShapeArbitraryPoligon("Outil Polygone Arbitraire", EnumSet.of(KeyCode.SHIFT, KeyCode.S), ToolCategory.Shape, "shape_arbitrary_polygon.png"), //

    Text("Outil Texte", EnumSet.of(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.T), ToolCategory.Extended, "text.png"), //
    Curve("Outil Courbe", EnumSet.of(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.C), ToolCategory.Extended, "curve.png"); //
    
    public static String iconRessource = "icons/tools/";

    public final String tip;
    public final EnumSet<KeyCode> codes;
    public final ToolCategory category;
    public final String icon;
    public final Supplier<ToolState> supplier;

    Tool(String tip, KeyCode code, ToolCategory category, String icon) {
        this(tip, EnumSet.of(code), category, icon);
    }

    Tool(String tip, EnumSet<KeyCode> codes, ToolCategory category, String icon) {
        this(tip, codes, category, icon, null);
    }

    Tool(String tip, KeyCode code, ToolCategory category, String icon, Supplier<ToolState> supplier) {
        this(tip, EnumSet.of(code), category, icon, supplier);
    }

    Tool(String tip, EnumSet<KeyCode> codes, ToolCategory category, String icon, Supplier<ToolState> supplier) {
        this.tip = tip;
        this.codes = codes;
        this.category = category;
        this.icon = icon;
        this.supplier = supplier;
    }

    /*
     * 
     * 
     * private final String Tip; private final String Category;
     * 
     * 
     * 
     * 
     * //private final Supplier<DrawerState> supplier;
     * 
     * Tool(KeyCode code, Supplier<DrawerState> supplier) { this.code = code;
     * this.supplier = supplier; }
     * 
     * public static DrawerState get(KeyCode code) { for (Tool tool : values())
     * { if(tool.code.equals(code)) { return tool.supplier.get() ; } } return
     * null;
     * 
     * }
     * 
     */

}
