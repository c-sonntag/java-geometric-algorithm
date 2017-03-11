package at.u4a.geometric_algorithms.gui.tools;

import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import at.u4a.geometric_algorithms.gui.tools.state.*;

public enum Tool {

    /* ENUM */

    /*
     * Rectangle(KeyCode.R, RectangleDrawerState0::new), Circle(KeyCode.C,
     * CircleDrawerState0::new), Escape(KeyCode.ESCAPE, NullDrawerState::new);
     */

    // Null(null, (EnumSet<KeyCode>) null, ToolCategory.Invisible, null,
    // NullToolState::new), //
    Escape(null, KeyCode.ESCAPE, ToolCategory.Invisible, null, NullToolState::new), //

    Selection("Outil Selection", KeyCode.V, ToolCategory.Transform, "selection.png", SelectionToolState::new), //
    DirectSelection("Outil Selection directe", KeyCode.A, ToolCategory.Transform, "selection_direct.png"), //

    CloudOfPoint("Outil Nuage de Point", KeyCode.P, ToolCategory.ShapeSetter, "cloud_of_point.png"), //
    LotOfSegment("Outil Ensemble de Segments", KeyCode.S, ToolCategory.ShapeSetter, "lot_of_segment.png"), //
    TraceOfPoint("Outil Tracé de Point", KeyCode.T, ToolCategory.ShapeSetter, "trace_of_point.png"), //

    ShapeRectangle("Outil Rectangle", EnumSet.of(KeyCode.SHIFT, KeyCode.R), ToolCategory.Shape, "shape_square.png"), //
    ShapeElipse("Outil Elipse", EnumSet.of(KeyCode.SHIFT, KeyCode.E), ToolCategory.Shape, "shape_circle.png"), //

    ShapeConvexePoligon("Outil Polygone Convexe", EnumSet.of(KeyCode.SHIFT, KeyCode.C), ToolCategory.Shape, "shape_convexe_poligon.png"), //
    ShapeSimplePoligon("Outil Polygone Simple", EnumSet.of(KeyCode.SHIFT, KeyCode.S), ToolCategory.Shape, "shape_simple_poligon.png", SimplePolygonToolState::new), //
    ShapeArbitraryPoligon("Outil Polygone Arbitraire", EnumSet.of(KeyCode.SHIFT, KeyCode.A), ToolCategory.Shape, "shape_arbitrary_polygon.png"), //

    Text("Outil Texte", EnumSet.of(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.T), ToolCategory.Extended, "text.png"), //
    Curve("Outil Courbe", EnumSet.of(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.C), ToolCategory.Extended, "curve.png"), //

    CameraMove("Outil Courbe", EnumSet.of(KeyCode.SPACE), ToolCategory.Camera, "move.png"), //
    CameraZoomIn("Outil Courbe", EnumSet.of(KeyCode.CONTROL, KeyCode.PLUS), ToolCategory.Camera, "zoom_in.png"), //
    CameraZoomOut("Outil Courbe", EnumSet.of(KeyCode.CONTROL, KeyCode.MINUS), ToolCategory.Camera, "zoom_out.png"); //

    /* PRIVATE STATIC */

    private static Map<EnumSet<KeyCode>, Tool> mutableKeyCodeMap;

    /* PUBLIC STATIC */

    public static String iconRessource = "icons/tools/";

    public static Tool getByKeyCode(EnumSet<KeyCode> codes) {

        if (mutableKeyCodeMap == null) {
            mutableKeyCodeMap = new HashMap<EnumSet<KeyCode>, Tool>();
            for (Tool tool : values())
                if ((tool.codes != null) && (tool.supplier != null))
                    mutableKeyCodeMap.put(tool.codes, tool);
        }

        return mutableKeyCodeMap.get(codes);
    }

    /* PUBLIC */

    public final String tip;
    public final EnumSet<KeyCode> codes;
    public final ToolCategory category;
    public final String icon;
    public final Supplier<ToolState> supplier;

    public String getKeyCodeStr() {
        if (codes == null)
            return null;
        //
        String concat = "";
        Iterator<KeyCode> codes_it = codes.iterator();
        while (codes_it.hasNext()) {
            if (!concat.isEmpty())
                concat += " + ";
            concat += codes_it.next();
        }
        //
        if (concat.isEmpty())
            return null;
        else
            return concat;
    }

    /* PRIVATE */

    private Tool(String tip, KeyCode code, ToolCategory category, String icon) {
        this(tip, EnumSet.of(code), category, icon);
    }

    private Tool(String tip, EnumSet<KeyCode> codes, ToolCategory category, String icon) {
        this(tip, codes, category, icon, null);
    }

    private Tool(String tip, KeyCode code, ToolCategory category, String icon, Supplier<ToolState> supplier) {
        this(tip, EnumSet.of(code), category, icon, supplier);
    }

    private Tool(String tip, EnumSet<KeyCode> codes, ToolCategory category, String icon, Supplier<ToolState> supplier) {
        this.tip = tip;
        this.codes = codes;
        this.category = category;
        this.icon = icon;
        this.supplier = supplier;
    }

    /*
     * private final String Tip; private final String Category; //private final
     * Supplier<DrawerState> supplier; Tool(KeyCode code, Supplier<DrawerState>
     * supplier) { this.code = code; this.supplier = supplier; }
     */

}
