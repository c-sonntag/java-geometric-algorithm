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
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import at.u4a.geometric_algorithms.gui.element.DrawerScene;
import at.u4a.geometric_algorithms.gui.element.InterfaceDrawerAction;
import at.u4a.geometric_algorithms.gui.element.InterfaceLayerAction;
import at.u4a.geometric_algorithms.gui.element.LayerCategoryLabel;
import at.u4a.geometric_algorithms.gui.element.LayerTree;
import at.u4a.geometric_algorithms.gui.element.StatusBar;
import at.u4a.geometric_algorithms.gui.element.ToolButton;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolCategory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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

public class PrincipalGui extends JFrame {

    private static final long serialVersionUID = 7047807079976701109L;

    /* Initialize Final Variable */

    private final DrawerAction da = new DrawerAction();
    private final LayerAction la = new LayerAction();
    
    private final DrawerScene ds = new DrawerScene(da,la);
    private final LayerTree treeLayer = new LayerTree(ds);
    
    

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PrincipalGui window = new PrincipalGui();
                    window.setVisible(true);

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
        super();
        initializeFrame();

        // Second
        initializeMenuBar();
        initializeToolBarTop();
        initializeToolBarLeft();
        initializeStatusBar();

        // Third
        initializeContent();

        // Last
        revalidate();
        repaint();
    }

    /**
     * GUI repaint action
     */
    public void repaint() {
        super.repaint();
        ds.refresh();
    }

    /**
     * Initialize the frame.
     */
    private void initializeFrame() {
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
    }

    /**
     * Initialize Status bar
     */
    private void initializeStatusBar() {
        StatusBar statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Initialize MenuBar
     */
    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ds.getLayerMannager().clear();
            }
        });
        mntmNew.setIcon(new ImageIcon("icons/action/page_add.png"));
        mnFile.add(mntmNew);

        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.setEnabled(false);
        mntmOpen.setIcon(new ImageIcon("icons/action/page_edit.png"));
        mnFile.add(mntmOpen);

        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.setEnabled(false);
        mntmSave.setIcon(new ImageIcon("icons/action/page_save.png"));
        mnFile.add(mntmSave);

        mnFile.addSeparator();

        JMenuItem mntmOther = new JMenuItem("Other");
        mntmOther.setEnabled(false);
        mnFile.add(mntmOther);

        mnFile.addSeparator();

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                dispose();
            }
        });
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
        getContentPane().add(toolBarLeft, BorderLayout.WEST);

        /* TOOL BUTTON GENERATION */

        ToolCategory lastCategory = null;

        // EnumSet<Tool> toolValues = new EnumSet.allOf(Tool.class);

        for (Tool tool : Tool.values()) {

            if (tool.category.equals(ToolCategory.Invisible))
                continue;

            //
            if (lastCategory != null)
                if (!lastCategory.equals(tool.category))
                    toolBarLeft.addSeparator();
            //

            ToolButton btnTool = new ToolButton(tool);
            toolBarLeft.add(btnTool);
            ds.addToolButton(btnTool);

            //
            lastCategory = tool.category;
        }

    }

    /**
     * Initialize Toolbar Top
     */
    private void initializeToolBarTop() {
        JToolBar toolBarTop = new JToolBar();
        toolBarTop.setFloatable(false);
        getContentPane().add(toolBarTop, BorderLayout.NORTH);

        JToolBar appToolbar = new JToolBar();
        appToolbar.setFloatable(false);
        toolBarTop.add(appToolbar);

        // JButton btnNew = new JButton("New");
        // appToolbar.add(btnNew);
        // btnNew.setIcon(new ImageIcon("icons/action/page_add.png"));

        toolBarTop.addSeparator();

        /*
         * JButton btnNew = new JButton("New"); btnNew.setIcon(new
         * ImageIcon("icons/page_add.png")); toolBarTop.add(btnNew);
         */

        // toolBarTop.addSeparator();

        JToolBar drawerActionToolBar = da.getToolBar();
        drawerActionToolBar.setFloatable(false);
        toolBarTop.add(drawerActionToolBar);
    }

    /**
     * Initialize Content with Canvas and Layer
     */
    private void initializeContent() {
        JPanel panelLayer = initializeContentLayer();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ds.getPanel(), panelLayer);
        getContentPane().add(splitPane, BorderLayout.CENTER);

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

        JToolBar layerActionToolBar = la.getToolBar();
        layerActionToolBar.setRollover(true);
        layerActionToolBar.setFloatable(false);
        panelLayer.add(layerActionToolBar, BorderLayout.SOUTH);

        /* TREE */

        JScrollPane scrollPaneTreeLayer = new JScrollPane();
        panelLayer.add(scrollPaneTreeLayer, BorderLayout.CENTER);
        scrollPaneTreeLayer.setViewportView(treeLayer);

        return panelLayer;
    }

   

    private class LayerAction implements InterfaceLayerAction {

        private final JButton btnLayerDelete;
        private final JToolBar toolbar;

        public LayerAction() {

            toolbar = new JToolBar();
            
            /* TOOLBAR - MENUBAR */

            JPanel panelMenuBar = new JPanel();
            panelMenuBar.setLayout(new BoxLayout(panelMenuBar, BoxLayout.X_AXIS));
            toolbar.add(panelMenuBar);

            JMenuBar menuBar = new JMenuBar();
            menuBar.setAlignmentY(Component.CENTER_ALIGNMENT);
            menuBar.setBorderPainted(false);
            panelMenuBar.add(menuBar);

            // menuBar.setOpaque(false);
            // mnNewMenu.setVerticalAlignment(SwingConstants.BOTTOM);

            JMenu mnNewMenu = new JMenu("F");

            mnNewMenu.setFont(LayerCategoryLabel.font);
            menuBar.add(mnNewMenu);

            JMenuItem mntmNewMenuItem = new JMenuItem("Triangul");
            mnNewMenu.add(mntmNewMenuItem);

            setMenuToBottomLeftAlignement(mnNewMenu);

            /* TOOLBAR - BUTTON */

            btnLayerDelete = new JButton("");
            btnLayerDelete.setEnabled(false);
            btnLayerDelete.setToolTipText("Delete layer");
            btnLayerDelete.setIcon(new ImageIcon("icons/action/cancel.png"));
           

            toolbar.add(btnLayerDelete);

        }

        @Override
        public void addLayerActionListenerOfDelete(Runnable func) {
            btnLayerDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    func.run();
                }
            });
        }

        @Override
        public void setDelete(boolean b) {
            btnLayerDelete.setEnabled(b);
        }
        
        @Override
        public JToolBar getToolBar() {
            return toolbar;
        }
        
        /**
         * This function need to have a content in Menu for change alignement
         */
        private void setMenuToBottomLeftAlignement(JMenu m) {
            m.setMenuLocation((int) -(m.getPopupMenu().getPreferredSize().getWidth() - m.getPreferredSize().getWidth()), (int) -m.getPopupMenu().getPreferredSize().getHeight());
        }

    }

    private class DrawerAction implements InterfaceDrawerAction {

        private final JButton btnValid;
        private final JButton btnCancel;
        private final JToolBar toolbar;

        public DrawerAction() {

            toolbar = new JToolBar();

            btnValid = new JButton("Valid");
            btnValid.setIcon(new ImageIcon("icons/action/tick.png"));

            btnCancel = new JButton("Cancel");
            btnCancel.setIcon(new ImageIcon("icons/action/cancel.png"));

            toolbar.add(btnValid);
            toolbar.add(btnCancel);

        }

        @Override
        public void addDrawerActionListenerOfValid(Runnable func) {
            btnValid.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    func.run();
                }
            });
        }

        @Override
        public void addDrawerActionListenerOfCancel(Runnable func) {
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    func.run();
                }
            });

        }

        @Override
        public void activeDrawerAction(boolean b) {
            toolbar.setVisible(b);
        }

        @Override
        public JToolBar getToolBar() {
            return toolbar;
        }

        @Override
        public void haveValid(boolean b) {
            btnValid.setEnabled(b);
        }

        @Override
        public void haveCancel(boolean b) {
            btnCancel.setEnabled(b);
        }

    }

}
