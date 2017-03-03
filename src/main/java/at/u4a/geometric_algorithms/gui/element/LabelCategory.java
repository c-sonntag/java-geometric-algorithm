package at.u4a.geometric_algorithms.gui.element;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class LabelCategory extends JLabel {
    
    public static final Font font = new Font("Times New Roman", Font.BOLD | Font.ITALIC, 13);

    public LabelCategory(String symbol, String info) {
        super(symbol);
        setToolTipText(info);
        setFont(font);
        setBorder(new EmptyBorder(0, 0, 0, 2));
    }

}
