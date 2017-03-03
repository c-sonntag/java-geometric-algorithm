package at.u4a.geometric_algorithms.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.element.LabelCategory;
import at.u4a.geometric_algorithms.gui.element.LayerTree;
import at.u4a.geometric_algorithms.gui.element.StatusBar;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolCategory;
import at.u4a.geometric_algorithms.gui.tools.state.NullToolState;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Gui principale de l'application Geométrie Algorithmique
 * 
 * @author Christophe Sonntag. L3 Informaique Luminy. http://u4a.at
 * @version 1.0
 * @see Fx-Swing compatibility :
 *      http://docs.oracle.com/javafx/2/swing/swing-fx-interoperability.htm
 *
 */

public class PrincipalGui {

    // Initialize Final Variable
    private final JFrame frame = new JFrame();;
    private final JFXPanel fxCanvas = new JFXPanel();;
    private final LayerTree treeLayer = new LayerTree();;
    private final Drawer drawer = new Drawer();
    private final DrawerContext context = new DrawerContext(drawer);

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

        // First initialization
        initializeCanvas();
        initializeFrame();

        // Second
        initializeMenuBar();
        initializeToolBarTop();
        initializeToolBarLeft();
        initializeStatusBar();

        // Third
        initializeContent();
    }

    /**
     * Initialize the frame.
     */
    private void initializeFrame() {
        frame.setBounds(100, 100, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
    }
    
    /**
     * Initialize Status bar
     */
    private void initializeStatusBar() {
        StatusBar statusBar = new StatusBar();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
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

        JToolBar toolBarLeft = new JToolBar();
        toolBarLeft.setOrientation(SwingConstants.VERTICAL);
        toolBarLeft.setFloatable(false);
        frame.getContentPane().add(toolBarLeft, BorderLayout.WEST);

        /* TOOL BUTTON GENERATION */

        ToolCategory lastCategory = null;

        for (Tool tool : Tool.values()) {
            if (lastCategory != null)
                if (! lastCategory.equals(tool.category))
                    toolBarLeft.addSeparator();
            //

            JButton btnTool = new JButton();
            btnTool.setToolTipText(tool.tip);
            btnTool.setIcon(new ImageIcon(Tool.iconRessource + tool.icon));
            
            if(tool.supplier == null)
                btnTool.setEnabled(false);
                
            toolBarLeft.add(btnTool);
            
            context.addTool(tool, btnTool);
          
            //
            lastCategory = tool.category;
        }


    }
    
    private void uselessInitializeToolBarLeft() {
        
        JToolBar toolBarLeft = new JToolBar();
        toolBarLeft.setOrientation(SwingConstants.VERTICAL);
        toolBarLeft.setFloatable(false);
        frame.getContentPane().add(toolBarLeft, BorderLayout.WEST);
        
        JButton btnSelected = new JButton();
        btnSelected.setToolTipText("Outil Selection");
        btnSelected.setIcon(new ImageIcon("icons/tools/cursor_selection.png"));
        toolBarLeft.add(btnSelected);

        JButton btnDirectSelection = new JButton();
        btnDirectSelection.setToolTipText("Outil Selection directe");
        btnDirectSelection.setIcon(new ImageIcon("icons/tools/cursor_vector.png"));
        toolBarLeft.add(btnDirectSelection);

        toolBarLeft.addSeparator();

        JButton btnToolsPointsCloud = new JButton();
        btnToolsPointsCloud.setToolTipText("Outil Nuage de Points");
        btnToolsPointsCloud.setIcon(new ImageIcon("icons/tools/cloud_of_point.png"));
        toolBarLeft.add(btnToolsPointsCloud);

        JButton btnToolsSegmentsCloud = new JButton();
        btnToolsSegmentsCloud.setToolTipText("Outil Ensemble de Segments");
        btnToolsSegmentsCloud.setIcon(new ImageIcon("icons/tools/segments.png"));
        btnToolsSegmentsCloud.setEnabled(false);
        toolBarLeft.add(btnToolsSegmentsCloud);

        JButton btnToolsPointLine = new JButton();
        btnToolsPointLine.setToolTipText("Outil Tracé de Point");
        btnToolsPointLine.setIcon(new ImageIcon("icons/tools/chart_line.png"));
        btnToolsPointLine.setEnabled(false);
        toolBarLeft.add(btnToolsPointLine);

        toolBarLeft.addSeparator();

        JButton btnToolsRectangle = new JButton();
        btnToolsRectangle.setToolTipText("Outil Rectangle");
        btnToolsRectangle.setIcon(new ImageIcon("icons/tools/shape_square.png"));
        btnToolsRectangle.setEnabled(false);
        toolBarLeft.add(btnToolsRectangle);

        JButton btnToolsElipse = new JButton();
        btnToolsElipse.setToolTipText("Outil Elipse");
        btnToolsElipse.setIcon(new ImageIcon("icons/tools/shape_circle.png"));
        btnToolsElipse.setEnabled(false);
        toolBarLeft.add(btnToolsElipse);

        JButton btnToolsConvexePolygon = new JButton();
        btnToolsConvexePolygon.setToolTipText("Outil Polygone Convexe");
        btnToolsConvexePolygon.setIcon(new ImageIcon("icons/tools/shape_convexe_poligon.png"));
        toolBarLeft.add(btnToolsConvexePolygon);

        JButton btnToolsSimplePolygon = new JButton();
        btnToolsSimplePolygon.setToolTipText("Outil Polygone Simple");
        btnToolsSimplePolygon.setIcon(new ImageIcon("icons/tools/shape_simple_poligon.png"));
        btnToolsSimplePolygon.setEnabled(false);
        toolBarLeft.add(btnToolsSimplePolygon);

        JButton btnToolsArbitraryPolygon = new JButton();
        btnToolsArbitraryPolygon.setToolTipText("Outil Polygone Arbitraire");
        btnToolsArbitraryPolygon.setIcon(new ImageIcon("icons/tools/shape_arbitrary_polygon.png"));
        btnToolsArbitraryPolygon.setEnabled(false);
        toolBarLeft.add(btnToolsArbitraryPolygon);

        toolBarLeft.addSeparator();

        JButton btnToolsText = new JButton();
        btnToolsText.setToolTipText("Outil Texte");
        btnToolsText.setIcon(new ImageIcon("icons/tools/font.png"));
        btnToolsText.setEnabled(false);
        toolBarLeft.add(btnToolsText);

        JButton btnToolsCurve = new JButton();
        btnToolsCurve.setToolTipText("Outil Courbe");
        btnToolsCurve.setIcon(new ImageIcon("icons/tools/vector.png"));
        btnToolsCurve.setEnabled(false);
        toolBarLeft.add(btnToolsCurve);

    }

    /**
     * Initialize Toolbar Top
     */
    private void initializeToolBarTop() {
        JToolBar toolBarTop = new JToolBar();
        toolBarTop.setFloatable(false);
        frame.getContentPane().add(toolBarTop, BorderLayout.NORTH);

        JButton btnNew = new JButton("New");
        btnNew.setIcon(new ImageIcon("icons/action/page_add.png"));
        toolBarTop.add(btnNew);

        toolBarTop.addSeparator();

        /*
         * JButton btnNew = new JButton("New"); btnNew.setIcon(new
         * ImageIcon("icons/page_add.png")); toolBarTop.add(btnNew);
         */

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
     * Initialize Content with Canvas and Layer
     */
    private void initializeContent() {
        JPanel panelLayer = initializeContentLayer();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fxCanvas, panelLayer);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.getLeftComponent().setMinimumSize(new Dimension(300, 0));
        // splitPane.getLeftComponent().setMaximumSize(new Dimension(300,0));
        splitPane.getRightComponent().setMinimumSize(new Dimension(200, 0));
        splitPane.getRightComponent().setPreferredSize(new Dimension(200, 0));

    }

    /**
     * Initialize Content Layer
     */
    private JPanel initializeContentLayer() {

        JPanel panelLayer = new JPanel();
        panelLayer.setLayout(new BorderLayout(0, 0));

        /* TOOLBAR */

        JToolBar toolBarLayer = new JToolBar();
        toolBarLayer.setRollover(true);
        toolBarLayer.setFloatable(false);
        panelLayer.add(toolBarLayer, BorderLayout.SOUTH);

        /* TOOLBAR - MENUBAR */

        JPanel panelMenuBar = new JPanel();
        panelMenuBar.setLayout(new BoxLayout(panelMenuBar, BoxLayout.X_AXIS));
        toolBarLayer.add(panelMenuBar);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setAlignmentY(Component.CENTER_ALIGNMENT);
        menuBar.setBorderPainted(false);
        panelMenuBar.add(menuBar);

        // menuBar.setOpaque(false);
        // mnNewMenu.setVerticalAlignment(SwingConstants.BOTTOM);

        JMenu mnNewMenu = new JMenu("F");

        mnNewMenu.setFont(LabelCategory.font);
        menuBar.add(mnNewMenu);

        JMenuItem mntmNewMenuItem = new JMenuItem("Triangul");
        mnNewMenu.add(mntmNewMenuItem);

        setMenuToBottomLeftAlignement(mnNewMenu);

        /* TOOLBAR - BUTTON */

        JButton btnLayerDelete = new JButton("");
        btnLayerDelete.setEnabled(false);
        btnLayerDelete.setToolTipText("Delete layer");
        btnLayerDelete.setIcon(new ImageIcon("icons/action/cancel.png"));
        toolBarLayer.add(btnLayerDelete);

        /* TREE */

        JScrollPane scrollPaneTreeLayer = new JScrollPane();
        panelLayer.add(scrollPaneTreeLayer, BorderLayout.CENTER);
        scrollPaneTreeLayer.setViewportView(treeLayer);

        return panelLayer;
    }

    /**
     * This function need to have a content in Menu for change alignement
     */
    private void setMenuToBottomLeftAlignement(JMenu m) {
        m.setMenuLocation((int) -(m.getPopupMenu().getPreferredSize().getWidth() - m.getPreferredSize().getWidth()), (int) -m.getPopupMenu().getPreferredSize().getHeight());
    }

    /**
     * Initialize Canvas
     */
    private void initializeCanvas() {

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
        Group root = new Group();
        Scene scene = new Scene(root, Color.ALICEBLUE);

        Text text = new Text();
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");

        root.getChildren().add(text);

        Drawer canvas = new Drawer();

        return (scene);
    }

}
