package at.u4a.geometric_algorithms.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import at.u4a.geometric_algorithms.gui.element.ColorChooserButton;
import at.u4a.geometric_algorithms.gui.element.LabelCategory;
import at.u4a.geometric_algorithms.gui.layer.LayerCategory;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.CardLayout;

public class DisignerTest {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DisignerTest window = new DisignerTest();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public DisignerTest() {
        initialize();
    }

    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 107);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        testLayerTreeLayerInfoWithPanel();
        
    }
    
    private void testLayerTreeLayerInfo() {
        
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        panel.add(toolBar, BorderLayout.CENTER);
        
        JCheckBox chckbxActive = new JCheckBox();
        chckbxActive.setSelected(true);
        chckbxActive.setOpaque(false);
        chckbxActive.setToolTipText("Active it");
        toolBar.add(chckbxActive);
        
        toolBar.addSeparator();
        
        LabelCategory lblCategoryGen = new LabelCategory();
        toolBar.add(lblCategoryGen);
        
        toolBar.addSeparator();
        
        JLabel lblLayerType = new JLabel("_LayerType_");
        lblLayerType.setIcon(new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png"));
        toolBar.add(lblLayerType);
        
        toolBar.addSeparator();
        
        JLabel lblLayerName = new JLabel("_LayerName_");
        toolBar.add(lblLayerName);
        
        toolBar.addSeparator();
        
        ColorChooserButton ccTest1 = new ColorChooserButton(Color.CYAN);
        ccTest1.setToolTipText("Yooo");
        toolBar.add(ccTest1);
       
        ColorChooserButton ccTest2 = new ColorChooserButton(Color.CYAN);
        ccTest2.setToolTipText("YoooPP");
        toolBar.add(ccTest2);
        
        toolBar.addSeparator();
        
        JPanel panel_1 = new JPanel();
        toolBar.add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

        //setMenuToBottomLeftAlignement(mnNewMenu);
        
        //lblLayerCategory.pack();
        //lblLayerCategory.setBorder(new EmptyBorder(0, 5, 0, 5));
        //toolBar.pack();
    }
    
private void testLayerTreeLayerInfoWithPanel() {
        
        JPanel toolBar = new JPanel();
        //toolBar.setBackground(Color.MAGENTA);
        frame.getContentPane().add(toolBar, BorderLayout.CENTER);
        
        /*JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        panel.add(toolBar, BorderLayout.CENTER);*/
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        
        JCheckBox chckbxActive = new JCheckBox();
        chckbxActive.setSelected(true);
        chckbxActive.setOpaque(false);
        chckbxActive.setToolTipText("Active it");
        toolBar.add(chckbxActive);
        
        //toolBar.addSeparator();
        toolBar.add(Box.createRigidArea(new Dimension(5,0)));
        
        LabelCategory lblCategoryGen = new LabelCategory(LayerCategory.Algorithm);
        toolBar.add(lblCategoryGen);
        
        //toolBar.addSeparator();
        toolBar.add(Box.createRigidArea(new Dimension(5,0)));
        
        JLabel lblLayerType = new JLabel("_LayerType_");
        lblLayerType.setIcon(new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png"));
        toolBar.add(lblLayerType);
        
        //toolBar.addSeparator();
        toolBar.add(Box.createRigidArea(new Dimension(5,0)));
        
        JLabel lblLayerName = new JLabel("_LayerName_");
        toolBar.add(lblLayerName);
        
        //toolBar.addSeparator();
        
        toolBar.add(Box.createRigidArea(new Dimension(5,0)));
        
        ColorChooserButton ccTest1 = new ColorChooserButton(Color.CYAN);
        ccTest1.setToolTipText("Yooo");
        toolBar.add(ccTest1);
       
        ColorChooserButton ccTest2 = new ColorChooserButton(Color.CYAN);
        ccTest2.setToolTipText("YoooPP");
        toolBar.add(ccTest2);
        
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolBar.add(panel);
        panel.setLayout(new GridLayout(1, 0, 0, 0));

        //setMenuToBottomLeftAlignement(mnNewMenu);
        
        //lblLayerCategory.pack();
        //lblLayerCategory.setBorder(new EmptyBorder(0, 5, 0, 5));
        //toolBar.pack();
    }
    


}
