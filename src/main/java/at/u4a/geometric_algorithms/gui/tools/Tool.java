package at.u4a.geometric_algorithms.gui.tools;

import javafx.scene.input.KeyCode;

import java.util.function.Supplier;

public enum Tool {
    
    /*Rectangle(KeyCode.R, RectangleDrawerState0::new), 
    Circle(KeyCode.C, CircleDrawerState0::new), 
    Escape(KeyCode.ESCAPE, NullDrawerState::new);*/
    
    Selection("Outil Selection",KeyCode.V, ToolCategory.Transform, "cursor_selection.png"),
    DirectSelection("Outil Selection directe",KeyCode.A, ToolCategory.Transform, "cursor_selection.png"),
    
    
    public final String tip;
    public final String icon;
    public final KeyCode code;
    public final ToolCategory category;

    Tool(String tip, KeyCode code, ToolCategory category, String icon) {
        this.tip = tip;
        this.code = code;
        this.category = category;
        this.icon = icon;
    }    
 
   
    
    /*
    
   
    private final String Tip;
    private final String Category;
    
    
    
    
    //private final Supplier<DrawerState> supplier;

    Tool(KeyCode code, Supplier<DrawerState> supplier) {
        this.code = code;
        this.supplier = supplier;
    }

    public static DrawerState get(KeyCode code) {
        for (Tool tool : values()) {
            if(tool.code.equals(code))
            {
                return tool.supplier.get() ;
            }
        }
        return null;

    }
    
    */

}
