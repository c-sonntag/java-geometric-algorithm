package at.u4a.geometric_algorithms.gui.element;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import at.u4a.geometric_algorithms.gui.layer.LayerCategory;

public class LayerCategoryLabel extends JLabel {
    
    public static final Font font = new Font("Times New Roman", Font.BOLD | Font.ITALIC, 13);

    public LayerCategoryLabel() {
        setFont(font);
        setBorder(new EmptyBorder(0, 0, 0, 2));
    }
    
    public LayerCategoryLabel(LayerCategory cat) {
        this();
        set(cat);
    }
    
    public void set(LayerCategory cat) {
        setText(cat.symbol);
        setToolTipText(cat.name);
    }

}
