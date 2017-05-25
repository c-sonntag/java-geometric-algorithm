package at.u4a.geometric_algorithms.gui.element;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.print.Book;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer.ColorSet;
import at.u4a.geometric_algorithms.gui.layer.LayerCategory;
import at.u4a.geometric_algorithms.gui.layer.LayerManager;

/**
 * 
 * @author Christophe Sonntag
 * @see Source for work :
 *      http://www.java2s.com/Tutorial/Java/0240__Swing/CreatingaCustomRenderer.htm
 *      http://www.java2s.com/Code/Java/Swing-JFC/TreeCellRenderer.htm
 *      https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
 *
 */

public class LayerTree extends JTree {

    protected final LayerManager lm;
    protected final DrawerScene ds;

    public LayerTree(DrawerScene ds) {

        // super(simpleTest());

        /// ///

        super(new Object[] {});

        // super(lm.getLayers());

        // super(editorLayerTest());

        LayerNodeRenderer renderer = new LayerNodeRenderer();

        this.setEditable(true);
        this.setCellRenderer(renderer);
        this.setCellEditor(new LayerNodeEditor(this));

        //
        // getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {

                lm.layerTree_clearSelectedLayers();
                for (TreePath path : selectionModel.getSelectionPaths()) {
                    Object component = path.getLastPathComponent();
                    if (component instanceof DefaultMutableTreeNode) {
                        Object node = ((DefaultMutableTreeNode) component).getUserObject();
                        if (node instanceof AbstractLayer)
                            lm.layerTree_addSelectedLayer((AbstractLayer) node);
                    }
                }
                lm.layerTree_refresh();
            }

        });

        //
        expandRow(0);

        /// ///

        this.ds = ds;
        this.lm = ds.getLayerManager();
        lm.setControllerTree(this);

        //

        reload();

    }

    private void insertLayer(DefaultMutableTreeNode node, AbstractList<AbstractLayer> layers) {
        for (AbstractLayer layer : layers) {
            DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(layer);
            if (layer.isContener())
                insertLayer(dmtn, layer.getSubLayer());
            node.add(dmtn);
        }
    }

    public void reload() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        // lm.setSelectedLayer(null);

        rootNode.removeAllChildren();

        insertLayer(rootNode, lm.getLayers());

        // The method below rebuit the JTree in my app - you should do the same
        // here
        // this.createFileTree(rootNode, rootFile); // rescan the file structure

        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        ((DefaultTreeModel) treeModel).reload();
    }

    /** @see http://stackoverflow.com/questions/8210630/how-to-search-a-particular-node-in-jtree-and-make-that-node-expanded */
    private Vector<TreePath> find(DefaultMutableTreeNode root, AbstractList<AbstractLayer> nodes) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        Vector<TreePath> paths = new Vector<TreePath>();

        while (e.hasMoreElements()) {

            DefaultMutableTreeNode mutableNode = e.nextElement();
            Object userObject = mutableNode.getUserObject();

            //
            if (userObject instanceof AbstractLayer) {
                AbstractLayer al = (AbstractLayer) userObject;

                if (nodes.contains(al))
                    paths.add(new TreePath(mutableNode.getPath()));
            }
        }
        return paths.isEmpty() ? null : paths;
    }

    public void selectNodes(AbstractList<AbstractLayer> nodes) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        Vector<TreePath> findNodes = find(rootNode, nodes);

        if (findNodes != null) {
            TreePath[] arr = (TreePath[]) findNodes.toArray(new TreePath[findNodes.size()]);
            setSelectionPaths(arr);
        }
    }

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

    static private final ImageIcon noLayerImageIcon = new ImageIcon("icons/layer/no_layer.png");

    class LayerNodeRenderer implements TreeCellRenderer {
        // class LayerNodeRenderer extends DefaultTreeCellRenderer {

        // private JCheckBox leafRenderer = new JCheckBox();

        // JPanel renderer = new JPanel();
        JPanel renderer = new JPanel(new BorderLayout());

        private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        public LayerNodeRenderer() {

            // super(renderer);

            renderer.setFocusable(true);
            renderer.setRequestFocusEnabled(true);
            renderer.setOpaque(true);

            Dimension rigidAreaSize = new Dimension(5, 0);

            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();

            toolBar = new JPanel();
            // toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            renderer.add(toolBar);
            // toolBar.setLayout(new GridLayout());
            toolBar.setOpaque(false);

            chckbxActive = new JCheckBox();
            chckbxActive.setSelected(true);
            chckbxActive.setOpaque(false);
            chckbxActive.setToolTipText("Active it");
            chckbxActive.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    saveNode();
                    ds.repaint();
                }
            });
            toolBar.add(chckbxActive);

            lblCategoryGen = new LayerCategoryLabel(LayerCategory.Geometry);
            toolBar.add(lblCategoryGen);

            lblLayerType = new JLabel("");
            lblLayerType.setIcon(noLayerImageIcon);
            toolBar.add(lblLayerType);

            lblLayerName = new JLabel("_LayerName_");
            toolBar.add(lblLayerName);

            colorChooserButtonList = new ArrayList<ColorChooserButton>();

        }

        private final JPanel toolBar;

        final ArrayList<ColorChooserButton> colorChooserButtonList;

        final LayerCategoryLabel lblCategoryGen;
        final JLabel lblLayerName;
        final JCheckBox chckbxActive;
        final JLabel lblLayerType;

        AbstractLayer currentNode = null;

        final Color backgroundSelectionColor;
        final Color backgroundNonSelectionColor;

        private void saveColor(Vector<ColorSet> css) {
            if (css == null)
                return;

            if (!colorChooserButtonList.isEmpty()) {
                Iterator<ColorChooserButton> ccb_it = colorChooserButtonList.iterator();
                Iterator<ColorSet> css_it = css.iterator();
                //
                while (ccb_it.hasNext()) {
                    ColorChooserButton ccb = ccb_it.next();
                    ColorSet cs = css_it.next();
                    cs.setAwtColor(ccb.getSelectedColor());
                }
                //
                ds.repaint();
            }
        }

        private void colorChanged(Color newColor) {
            if (currentNode != null)
                saveColor(currentNode.getColor());
        }

        private void reloadColor(Vector<ColorSet> css) {
            if (css == null)
                return;

            //
            if (!colorChooserButtonList.isEmpty()) {
                Iterator<ColorChooserButton> ccb_it = colorChooserButtonList.iterator();
                Iterator<ColorSet> css_it = css.iterator();
                //
                while (ccb_it.hasNext()) {
                    ColorChooserButton ccb = ccb_it.next();
                    ColorSet cs = css_it.next();
                    ccb.setSelectedColor(cs.getAwtColor(), false);
                }

            } else {

                //
                for (ColorSet cs : css) {
                    ColorChooserButton ccb = new ColorChooserButton(cs.getAwtColor());
                    ccb.setToolTipText(cs.name);
                    ccb.addColorChangedListener((c) -> colorChanged(c));
                    colorChooserButtonList.add(ccb);
                    toolBar.add(ccb);
                }
            }
        }

        public void update(AbstractLayer node) {
            lblCategoryGen.set(node.getCategory());
            // lblLayerType.setText(node.getLayerType());
            lblLayerType.setToolTipText(node.getLayerType());
            lblLayerType.setIcon(node.getLayerTypeIcon());
            lblLayerName.setText(node.getLayerName());
            chckbxActive.setSelected(node.isActive());

            reloadColor(node.getColor());

            renderer.setVisible(true);
            renderer.doLayout();
            renderer.validate();
            renderer.revalidate();

            // defaultRenderer.se

            currentNode = node;
        }

        protected void saveNode() {
            if (currentNode == null)
                return;

            //
            currentNode.setActive(chckbxActive.isSelected());
            saveColor(currentNode.getColor());

            /** @todo gerer le rename */
            // currentNode.setLayerName(lblLayerName.getText());
        }

        protected AbstractLayer getNode() {
            if (currentNode == null)
                return null;

            saveNode();
            return currentNode;
        }

        protected JPanel getRenderer() {
            return renderer;
        }

        public boolean containEditableClick(int x, int y, Rectangle pRect) {

            // int xPositionIntoNode = pRect.height

            int xOrigin = (x-pRect.x);
            int yOrigin = (y-pRect.y);
            
            boolean inX = (xOrigin>=0 && xOrigin<=30) || (xOrigin>=90);
            boolean inY = (yOrigin>=0 && yOrigin<=30);
            
           if( inX && inY )
            return true;

            /* Component[] com_l = toolBar.getComponents();
             * for(Component c : com_l) {
             * 
             * if((c instanceof JCheckBox) || (c instanceof ColorChooserButton))
             * {
             * 
             * 
             * Point cLoc = c.getLocation); Dimension cSize = c.getSize();
             * 
             * int cX = cLoc.x; int cY = cLoc.y; int cHeight = cSize.height; int
             * cWidth = cSize.width;
             * 
             * boolean inX = ( (x >= (pRect.x + cX)) && (x <= (pRect.x + cX +
             * cWidth )) ); boolean inY = ( (y >= (pRect.y + cY)) && (y <=
             * (pRect.y + cY + cHeight )) );
             * 
             * if( inX && inY ) return true;
             * 
             * //c.getX() //c.getWidth(); }
             * 
             * 
             * }
             */
            /*
             * Dimension panel = renderer.getPreferredSize(); Dimension d =
             * chckbxActive.getPreferredSize(); pRect.setSize(new
             * Dimension(d.width, pRect.height));
             * 
             * if (pRect.contains(x, y)) { chckbxActive.setBounds(new
             * Rectangle(0, 0, d.width, pRect.height)); return true; }
             */

            return false;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {

                renderer.setEnabled(tree.isEnabled());
                renderer.setFont(tree.getFont());

                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

                if (userObject instanceof AbstractLayer) {

                    AbstractLayer node = (AbstractLayer) userObject;
                    update(node);

                    if (selected) {
                        renderer.setBackground(backgroundSelectionColor);
                    } else {
                        renderer.setBackground(backgroundNonSelectionColor);
                    }

                    return renderer;
                }
            }

            return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }

    }

    class LayerNodeEditor extends AbstractCellEditor implements TreeCellEditor {

        private static final long serialVersionUID = 4900398139830702089L;

        LayerNodeRenderer renderer = new LayerNodeRenderer();

        ChangeEvent changeEvent = null;

        JTree tree;

        public LayerNodeEditor(JTree tree) {
            this.tree = tree;
        }

        public Object getCellEditorValue() {
            // JPanel render = renderer.getRenderer();
            // CheckBoxNode checkBoxNode = new CheckBoxNode(render.getName(),
            // render.get);
            // return new AbstractLayer("a", true);
            // AbstractLayer node = renderer.getNode();
            // ds.repaint();

            // return null
            return renderer.getNode();
        }

        // @Override
        public boolean isCellEditable___(EventObject event) {
            boolean returnValue = false;
            if (event instanceof MouseEvent) {

                MouseEvent mouseEvent = (MouseEvent) event;
                TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());

                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                        Object userObject = treeNode.getUserObject();
                        // returnValue = ((treeNode.isLeaf()) && (userObject
                        // instanceof AbstractLayer));
                        returnValue = (userObject instanceof AbstractLayer);
                    }
                }
            }
            return returnValue;
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
                MouseEvent me = (MouseEvent) e;
                TreePath path = tree.getPathForLocation(me.getX(), me.getY());
                Rectangle r = tree.getPathBounds(path);
                if (r == null) {
                    return false;
                }

                return renderer.containEditableClick(me.getX(), me.getY(), r);
            }
            return false;
        }

        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {

            Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

            ItemListener itemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (stopCellEditing()) {
                        fireEditingStopped();
                    }
                }
            };

            if (editor instanceof JPanel) {
                fireEditingStopped();
            }

            return editor;
        }
    }

}
