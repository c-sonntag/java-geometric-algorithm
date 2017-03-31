package at.u4a.geometric_algorithms.gui.element;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.print.Book;
import java.util.EventObject;
import java.util.Random;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerCategory;
import at.u4a.geometric_algorithms.gui.layer.LayerMannager;

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

    protected final LayerMannager lm;
    protected final DrawerScene ds;

    public LayerTree(DrawerScene ds) {

        // super(simpleTest());

        /// ///

        super(new Object[] {});

        // super(lm.getLayers());

        // super(editorLayerTest());

        LayerNodeRenderer renderer = new LayerNodeRenderer();
        this.setCellRenderer(renderer);
        this.setEditable(true);
        this.setCellEditor(new LayerNodeEditor(this));

        /// ///

        // super(editorTest());

        // CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        // this.setCellRenderer(renderer);
        // this.setEditable(true);
        // this.setCellEditor(new CheckBoxNodeEditor(this));

        /// ///

        // super(createNodes());
        // TreeCellRenderer renderer = new LayerTreeCell();
        // this.setCellRenderer(renderer);

        /// ///

        // super(simpleTest2());
        // TreeCellRenderer renderer = new LayerNonEditCellRenderer();
        // this.setCellRenderer(renderer);

        /// ///
        /// ///

        this.ds = ds;
        this.lm = ds.getLayerMannager();
        lm.setControllerTree(this);

        //

        reload();

    }

    /*
     * public void reload() { ObjectNode selectedNode = this.getSelectedNode();
     * Vector allNodes = this.getNodes((ObjectNode) treeModel.getRoot()); Vector
     * visibleNodes = new Vector(); for (int i = 0; i < allNodes.size(); i++) {
     * if (this.isVisible(new TreePath(((ObjectNode)
     * allNodes.elementAt(i)).getPath()))) {
     * visibleNodes.add(allNodes.elementAt(i)); } } ((DefaultTreeModel)
     * this.getModel()).reload(); for (int i = 0; i < visibleNodes.size(); i++)
     * { this.scrollPathToVisible(new TreePath(((ObjectNode)
     * visibleNodes.elementAt(i)).getPath())); } if (selectedNode != null) {
     * TreePath path = new TreePath(selectedNode.getPath());
     * this.setSelectionPath(path); this.requestFocus(); } }
     */

    public void reloadO() {

        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto((MutableTreeNode) this, root, root.getChildCount());

        // DefaultTreeModel model = (DefaultTreeModel)getModel();
        // DefaultMutableTreeNode root =
        // (DefaultMutableTreeNode)model.getRoot();
        // root.add(new DefaultMutableTreeNode("another_child"));
        // root.add(new DefaultMutableTreeNode(lm.getLayers()));
        // root.add();
        // model.reload();
        // model.
        // model.reload( root );
    }

    public void reload() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        // rootNode.setUserObject(lm.getLayers()); // rename the root node
        // rootNode.add( new DefaultMutableTreeNode("another_child") );
        // rootNode.add( new DefaultMutableTreeNode(lm.getLayers()) );

        // rootNode.setUserObject(lm.getLayers());

        for (AbstractLayer l : lm.getLayers()) {
            rootNode.add(new DefaultMutableTreeNode(l));
        }
        // The method below rebuit the JTree in my app - you should do the same
        // here
        // this.createFileTree(rootNode, rootFile); // rescan the file structure
        ((DefaultTreeModel) treeModel).reload();
    }

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

   

    class LayerNodeRenderer implements TreeCellRenderer {

        // private JCheckBox leafRenderer = new JCheckBox();

         //JPanel renderer = new JPanel();
        JPanel renderer = new JPanel(new BorderLayout());

        private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        public LayerNodeRenderer() {

            Dimension rigidAreaSize = new Dimension(5, 0);

            // Font fontValue;
            // fontValue = UIManager.getFont("Tree.font");
            // if (fontValue != null) {
            // leafRenderer.setFont(fontValue);
            // }
            // Boolean booleanValue = (Boolean)
            // UIManager.get("Tree.drawsFocusBorderAroundIcon");
            // leafRenderer.setFocusPainted((booleanValue != null) &&
            // (booleanValue.booleanValue()));

            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();


            JPanel toolBar = new JPanel();    
            //toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            renderer.add(toolBar);
            //toolBar.setLayout(new GridLayout());
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

            // toolBar.addSeparator();
            // toolBar.add(rigidArea);
            //toolBar.add(Box.createRigidArea(rigidAreaSize));

            lblCategoryGen = new LabelCategory(LayerCategory.Geometry);
            toolBar.add(lblCategoryGen);

            // toolBar.addSeparator();
            // toolBar.add(rigidArea);
            //toolBar.add(Box.createRigidArea(rigidAreaSize));

            lblLayerType = new JLabel("_LayerType_");
            lblLayerType.setIcon(new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png"));
            toolBar.add(lblLayerType);

            // toolBar.addSeparator();
            // toolBar.add(rigidArea);
            //toolBar.add(Box.createRigidArea(rigidAreaSize));

            lblLayerName = new JLabel("_LayerName_");
            toolBar.add(lblLayerName);

            // toolBar.addSeparator();
            // toolBar.add(rigidArea);
            //toolBar.add(Box.createRigidArea(rigidAreaSize));

            ColorChooserButton ccTest1 = new ColorChooserButton(Color.CYAN);
            ccTest1.setToolTipText("Yooo");
            toolBar.add(ccTest1);

            ColorChooserButton ccTest2 = new ColorChooserButton(Color.CYAN);
            ccTest2.setToolTipText("YoooPP");
            toolBar.add(ccTest2);

            // toolBar.addSeparator();
            // toolBar.add(rigidArea);
            //toolBar.add(Box.createRigidArea(rigidAreaSize));

            // toolBar.pack();

            // JPanel panel_1 = new JPanel();
            // toolBar.add(panel_1);
            // panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

        }

        final LabelCategory lblCategoryGen;
        final JLabel lblLayerName;
        final JCheckBox chckbxActive;
        final JLabel lblLayerType;

        AbstractLayer currentNode = null;

        final Color backgroundSelectionColor;
        final Color backgroundNonSelectionColor;

        public void update(AbstractLayer node) {
            lblCategoryGen.set(node.getCategory());
            lblLayerType.setText(node.getLayerType());
            lblLayerType.setIcon(node.getLayerTypeIcon());
            lblLayerName.setText(node.getLayerName());
            chckbxActive.setSelected(node.isActive());
            renderer.setVisible(true);
            renderer.doLayout();
            renderer.validate();
            renderer.revalidate();
            currentNode = node;
        }

        protected void saveNode() {
            if (currentNode == null)
                return;

            //
            currentNode.setActive(chckbxActive.isSelected());
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

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component returnValue = null;
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {

                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

                // if (userObject instanceof Employee) {
                // Employee e = (Employee) userObject;
                // firstNameLabel.setText(e.firstName);
                // lastNameLabel.setText(e.lastName);
                // salaryLabel.setText("" + e.salary);
                // if (selected) {
                // renderer.setBackground(backgroundSelectionColor);
                // } else {
                // renderer.setBackground(backgroundNonSelectionColor);
                // }
                // renderer.setEnabled(tree.isEnabled());
                // returnValue = renderer;
                // }

                if (userObject instanceof AbstractLayer) {

                    AbstractLayer node = (AbstractLayer) userObject;

                    update(node);

                    if (selected) {
                        renderer.setBackground(backgroundSelectionColor);
                    } else {
                        renderer.setBackground(backgroundNonSelectionColor);
                    }

                    /** @todo voir plutot avec le parent */
                    renderer.setEnabled(tree.isEnabled());
                    returnValue = renderer;

                }

            }
            if (returnValue == null) {
                returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }

            return returnValue;
        }

        // public Component getTreeCellRendererComponent(JTree tree, Object
        // value, boolean selected, boolean expanded, boolean leaf, int row,
        // boolean hasFocus) {
        //
        // Component returnValue;
        // if (leaf) {
        // String stringValue = tree.convertValueToText(value, selected,
        // expanded, leaf, row, false);
        // leafRenderer.setText(stringValue);
        // leafRenderer.setSelected(false);
        // leafRenderer.setEnabled(tree.isEnabled());
        /// if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
        // Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
        // if (userObject instanceof CheckBoxNode) {
        // CheckBoxNode node = (CheckBoxNode) userObject;
        // leafRenderer.setText(node.getText());
        // leafRenderer.setSelected(node.isSelected());
        // }
        // }
        // returnValue = leafRenderer;
        // } else {
        // returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
        // value, selected, expanded, leaf, row, hasFocus);
        // }
        // return returnValue;
        // }
    }

    class LayerNodeEditor extends AbstractCellEditor implements TreeCellEditor {
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

            // return null;
            return renderer.getNode();
        }

        public boolean isCellEditable(EventObject event) {
            boolean returnValue = false;
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                        Object userObject = treeNode.getUserObject();
                        returnValue = ((treeNode.isLeaf()) && (userObject instanceof AbstractLayer));
                    }
                }
            }
            return returnValue;
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

            // if (editor instanceof JCheckBox) {
            // ((JCheckBox) editor).addItemListener(itemListener);
            // }

            if (editor instanceof JPanel) {

                // ((JPanel) editor). addItemListener(itemListener);

                fireEditingStopped();
                // fireTreeNodesChanged();
                // fireEditingCanceled();

                // ((JPanel) editor).addFocusListener(l);

                // ((JPanel) editor).addItemListener(itemListener);

            }

            return editor;
        }
    }

}
