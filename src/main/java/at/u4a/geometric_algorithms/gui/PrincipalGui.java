package at.u4a.geometric_algorithms.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.Button;
import javax.swing.SwingConstants;

import at.u4a.geometric_algorithms.gui.element.StatusBar;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.JPanel;
import java.awt.Canvas;


/**
 * Gui principale de l'application Geométrie Algorithmique
 * 
 * @author Christophe Sonntag. L3 Informaique Luminy. http://u4a.at
 * @version 1.0
 * @see Fx-Swing compatibility : http://docs.oracle.com/javafx/2/swing/swing-fx-interoperability.htm
 *
 */

public class PrincipalGui {

    private final JFrame frame;
    private final JFXPanel fxCanvas;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PrincipalGui window = new PrincipalGui();
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
    public PrincipalGui() {
        
        // Initialize Final
        frame = new JFrame();
        fxCanvas = new JFXPanel();
        
        // Fill Gui
        initializeFrame();
        initializeMenuBar();
        initializeToolBarTop();
        initializeToolBarLeft();
        initializeCanvas();
    }

    /**
     * Initialize the frame.
     */
    private void initializeFrame() {
        frame.setBounds(100, 100, 512, 585);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    /**
     * Initialize MenuBar
     */
    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.setIcon(new ImageIcon("icons/action/page_add.png"));
        mnFile.add(mntmNew);
        
        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.setIcon(new ImageIcon("icons/action/page_edit.png"));
        mnFile.add(mntmOpen);
        
        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.setIcon(new ImageIcon("icons/action/page_save.png"));
        mnFile.add(mntmSave);
        
        mnFile.addSeparator();
        
        JMenuItem mntmOther = new JMenuItem("Other");
        mnFile.add(mntmOther);
        
        mnFile.addSeparator();
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setIcon(new ImageIcon("icons/action/page_delete.png"));
        mnFile.add(mntmExit);
        
    }
    
    
    /**
     * Initialize Toolbar Left
     */
    private void initializeToolBarLeft() {
        
        JToolBar toolBarTopLeft = new JToolBar();
        toolBarTopLeft.setOrientation(SwingConstants.VERTICAL);
        toolBarTopLeft.setFloatable(false);
        frame.getContentPane().add(toolBarTopLeft, BorderLayout.WEST);
        
        JButton btnSelected = new JButton();
        btnSelected.setToolTipText("Outil Selection");
        btnSelected.setIcon(new ImageIcon("icons/tools/cursor.png"));
        toolBarTopLeft.add(btnSelected);
        
        JButton btnDirectSelection = new JButton();
        btnDirectSelection.setToolTipText("Outil Selection directe");
        btnDirectSelection.setIcon(new ImageIcon("icons/tools/cursor_vector.png"));
        toolBarTopLeft.add(btnDirectSelection);
        
        toolBarTopLeft.addSeparator();
        
        JButton btnToolsPointsCloud = new JButton();
        btnToolsPointsCloud.setToolTipText("Outil Nuage de Points");
        btnToolsPointsCloud.setIcon(new ImageIcon("icons/tools/point.png"));
        toolBarTopLeft.add(btnToolsPointsCloud);
        
        JButton btnToolsSegmentsCloud = new JButton();
        btnToolsSegmentsCloud.setToolTipText("Outil Ensemble de Segments");
        btnToolsSegmentsCloud.setIcon(new ImageIcon("icons/tools/segments.png"));
        btnToolsSegmentsCloud.setEnabled(false);
        toolBarTopLeft.add(btnToolsSegmentsCloud);
        
        JButton btnToolsPointLine = new JButton();
        btnToolsPointLine.setToolTipText("Outil Tracé de Point");
        btnToolsPointLine.setIcon(new ImageIcon("icons/tools/chart_line.png"));
        btnToolsPointLine.setEnabled(false);
        toolBarTopLeft.add(btnToolsPointLine);
        
        toolBarTopLeft.addSeparator();
        
        JButton btnToolsRectangle = new JButton();
        btnToolsRectangle.setToolTipText("Outil Rectangle");
        btnToolsRectangle.setIcon(new ImageIcon("icons/tools/shape_square.png"));
        btnToolsRectangle.setEnabled(false);
        toolBarTopLeft.add(btnToolsRectangle);
        
        JButton btnToolsElipse = new JButton();
        btnToolsElipse.setToolTipText("Outil Elipse");
        btnToolsElipse.setIcon(new ImageIcon("icons/tools/shape_circle.png"));
        btnToolsElipse.setEnabled(false);
        toolBarTopLeft.add(btnToolsElipse);
        
        JButton btnToolsConvexePolygon = new JButton();
        btnToolsConvexePolygon.setToolTipText("Outil Polygone Convexe");
        btnToolsConvexePolygon.setIcon(new ImageIcon("icons/tools/shape_convexe_poligon.png"));
        toolBarTopLeft.add(btnToolsConvexePolygon);
        
        JButton btnToolsSimplePolygon = new JButton();
        btnToolsSimplePolygon.setToolTipText("Outil Polygone Simple");
        btnToolsSimplePolygon.setIcon(new ImageIcon("icons/tools/shape_simple_poligon.png"));
        btnToolsSimplePolygon.setEnabled(false);
        toolBarTopLeft.add(btnToolsSimplePolygon);

        JButton btnToolsArbitraryPolygon = new JButton();
        btnToolsArbitraryPolygon.setToolTipText("Outil Polygone Arbitraire");
        btnToolsArbitraryPolygon.setIcon(new ImageIcon("icons/tools/shape_arbitrary_polygon.png"));
        btnToolsArbitraryPolygon.setEnabled(false);
        toolBarTopLeft.add(btnToolsArbitraryPolygon);
        
        toolBarTopLeft.addSeparator();
        
        JButton btnToolsText = new JButton();
        btnToolsText.setToolTipText("Outil Texte");
        btnToolsText.setIcon(new ImageIcon("icons/tools/font.png"));
        btnToolsText.setEnabled(false);
        toolBarTopLeft.add(btnToolsText);
        
        JButton btnToolsCurve = new JButton();
        btnToolsCurve.setToolTipText("Outil Courbe");
        btnToolsCurve.setIcon(new ImageIcon("icons/tools/vector.png"));
        btnToolsCurve.setEnabled(false);
        toolBarTopLeft.add(btnToolsCurve);
        
        StatusBar statusBar = new StatusBar();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        
    }
    
    /**
     * Initialize Toolbar Top
     */
    private void initializeToolBarTop() {
        JToolBar toolBarTop = new JToolBar();
        toolBarTop.setFloatable(false);
        toolBarTop.setRollover(true);
        frame.getContentPane().add(toolBarTop, BorderLayout.NORTH);
        
        JButton btnNew = new JButton("New");
        btnNew.setIcon(new ImageIcon("icons/action/page_add.png"));
        toolBarTop.add(btnNew);
        
        toolBarTop.addSeparator();
        
        /*JButton btnNew = new JButton("New");
        btnNew.setIcon(new ImageIcon("icons/page_add.png"));
        toolBarTop.add(btnNew);*/
        
        toolBarTop.addSeparator();
        
        JButton btnValid = new JButton("Valid");
        btnValid.setIcon(new ImageIcon("icons/action/tick.png"));
        btnValid.setEnabled(false);
        toolBarTop.add(btnValid);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setIcon(new ImageIcon("icons/action/cancel.png"));
        btnCancel.setEnabled(false);
        toolBarTop.add(btnCancel);
    }

    
    
    /**
     * Initialize Canvas
     */
    private void initializeCanvas() {
        frame.getContentPane().add(fxCanvas, BorderLayout.CENTER);
        //
        Platform.runLater(new Runnable() {
            public void run() {
                initializeFX(fxCanvas);
            }
       });
    }
    
    /**
     * Initialize FX (This method is invoked on the JavaFX thread)
     */
    private void initializeFX(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
        
    }
    
    private static Scene createScene() {
        Group  root  =  new  Group();
        Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
        Text  text  =  new  Text();
        
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");

        root.getChildren().add(text);

        return (scene);
    }
    
}
