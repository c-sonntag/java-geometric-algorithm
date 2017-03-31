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

    static int count = 0;

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

    // private static Vector editorLayerTest() {
    // AbstractLayer accessibilityOptions[] = { new AbstractLayer("A", false),
    // new AbstractLayer("B", true) };
    // AbstractLayer browsingOptions[] = { new AbstractLayer("C", true), new
    // AbstractLayer("D", true), new AbstractLayer("E", true), new
    // AbstractLayer("F", false) };
    // Vector<AbstractLayer> accessVector = new
    // TreeNodeVector<AbstractLayer>("G", accessibilityOptions);
    // Vector<AbstractLayer> browseVector = new
    // TreeNodeVector<AbstractLayer>("H", browsingOptions);
    // Object rootNodes[] = { accessVector, browseVector };
    // Vector<Object> rootVector = new TreeNodeVector<Object>("Root",
    // rootNodes);
    //
    // return rootVector;
    // }

    // static class TreeNodeVector<E> extends Vector<E> {
    // String name;
    //
    // TreeNodeVector(String name) {
    // this.name = name;
    // }
    //
    // TreeNodeVector(String name, E elements[]) {
    // this.name = name;
    // for (int i = 0, n = elements.length; i < n; i++) {
    // add(elements[i]);
    // }
    // }
    //
    // public String toString() {
    // return "[" + name + "]";
    // }
    // }

    // class LeafCellEditor extends DefaultTreeCellEditor {
    //
    // public LeafCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
    // TreeCellEditor editor) {
    // super(tree, renderer, editor);
    // }
    //
    // public boolean isCellEditable(EventObject event) {
    // boolean returnValue = super.isCellEditable(event);
    // if (returnValue) {
    // Object node = tree.getLastSelectedPathComponent();
    // if ((node != null) && (node instanceof TreeNode)) {
    // TreeNode treeNode = (TreeNode) node;
    // returnValue = treeNode.isLeaf();
    // }
    // }
    // return returnValue;
    // }
    // }

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
            lblLayerType.setText(String.valueOf(count) + " ");
            lblLayerType.setIcon(new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png"));
            toolBar.add(lblLayerType);

            count++;

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
            // lblLayerType.setText(node.getLayerType());
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

            System.out.println("LayerNodeRenderer\t Width(" + String.valueOf(returnValue.getPreferredSize().getWidth()) + ") Height(" + String.valueOf(returnValue.getPreferredSize().getHeight()) + ")  ");

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

            System.out.println("LayerNodeEditor\t Width(" + String.valueOf(editor.getPreferredSize().getWidth()) + ") Height(" + String.valueOf(editor.getPreferredSize().getHeight()) + ")  ");

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

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

    private static Vector editorTest() {
        CheckBoxNode accessibilityOptions[] = { new CheckBoxNode("A", false), new CheckBoxNode("B", true) };
        CheckBoxNode browsingOptions[] = { new CheckBoxNode("C", true), new CheckBoxNode("D", true), new CheckBoxNode("E", true), new CheckBoxNode("F", false) };
        Vector<CheckBoxNode> accessVector = new TreeNodeVector<CheckBoxNode>("G", accessibilityOptions);
        Vector<CheckBoxNode> browseVector = new TreeNodeVector<CheckBoxNode>("H", browsingOptions);
        Object rootNodes[] = { accessVector, browseVector };
        Vector<Object> rootVector = new TreeNodeVector<Object>("Root", rootNodes);

        return rootVector;
    }

    static class TreeNodeVector<E> extends Vector<E> {
        String name;

        TreeNodeVector(String name) {
            this.name = name;
        }

        TreeNodeVector(String name, E elements[]) {
            this.name = name;
            for (int i = 0, n = elements.length; i < n; i++) {
                add(elements[i]);
            }
        }

        public String toString() {
            return "[" + name + "]";
        }
    }

    class LeafCellEditor extends DefaultTreeCellEditor {

        public LeafCellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
            super(tree, renderer, editor);
        }

        public boolean isCellEditable(EventObject event) {
            boolean returnValue = super.isCellEditable(event);
            if (returnValue) {
                Object node = tree.getLastSelectedPathComponent();
                if ((node != null) && (node instanceof TreeNode)) {
                    TreeNode treeNode = (TreeNode) node;
                    returnValue = treeNode.isLeaf();
                }
            }
            return returnValue;
        }
    }

    static class CheckBoxNode {
        String text;

        boolean selected;

        public CheckBoxNode(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean newValue) {
            selected = newValue;
        }

        public String getText() {
            return text;
        }

        public void setText(String newValue) {
            text = newValue;
        }

        public String toString() {
            return getClass().getName() + "[" + text + "/" + selected + "]";
        }
    }

    class CheckBoxNodeRenderer implements TreeCellRenderer {
        private JCheckBox leafRenderer = new JCheckBox();

        private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

        protected JCheckBox getLeafRenderer() {
            return leafRenderer;
        }

        public CheckBoxNodeRenderer() {
            Font fontValue;
            fontValue = UIManager.getFont("Tree.font");
            if (fontValue != null) {
                leafRenderer.setFont(fontValue);
            }
            Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
            leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            Component returnValue;
            if (leaf) {
                String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
                leafRenderer.setText(stringValue);
                leafRenderer.setSelected(false);
                leafRenderer.setEnabled(tree.isEnabled());
                if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof CheckBoxNode) {
                        CheckBoxNode node = (CheckBoxNode) userObject;
                        leafRenderer.setText(node.getText());
                        leafRenderer.setSelected(node.isSelected());
                    }
                }
                returnValue = leafRenderer;
            } else {
                returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }
    }

    class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

        ChangeEvent changeEvent = null;

        JTree tree;

        public CheckBoxNodeEditor(JTree tree) {
            this.tree = tree;
        }

        public Object getCellEditorValue() {
            JCheckBox checkbox = renderer.getLeafRenderer();
            CheckBoxNode checkBoxNode = new CheckBoxNode(checkbox.getText(), checkbox.isSelected());
            return checkBoxNode;
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
                        returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
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
            if (editor instanceof JCheckBox) {
                ((JCheckBox) editor).addItemListener(itemListener);
            }
            return editor;
        }
    }

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

    class LayerNonEditCellRenderer implements TreeCellRenderer {
        JLabel firstNameLabel = new JLabel(" ");

        JLabel lastNameLabel = new JLabel(" ");

        JLabel salaryLabel = new JLabel(" ");

        JPanel renderer = new JPanel();

        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        Color backgroundSelectionColor;

        Color backgroundNonSelectionColor;

        public LayerNonEditCellRenderer() {

            // firstNameLabel.setForeground(Color.BLUE);
            // renderer.add(firstNameLabel);
            // lastNameLabel.setForeground(Color.BLUE);
            // renderer.add(lastNameLabel);
            // salaryLabel.setHorizontalAlignment(JLabel.RIGHT);
            // salaryLabel.setForeground(Color.RED);
            // renderer.add(salaryLabel);
            // renderer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();

            // JPanel panel = new JPanel();
            // renderer.add(panel, BorderLayout.CENTER);
            // panel.setLayout(new BorderLayout(0, 0));

            JPanel toolBar = new JPanel();
            renderer.add(toolBar, BorderLayout.CENTER);
            // toolBar.setLayout(new BorderLayout(0, 0));
            toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
            // toolBar.setBackground(Color.MAGENTA);
            toolBar.setOpaque(false);

            // JToolBar toolBar = new JToolBar();
            // toolBar.setRollover(true);
            // toolBar.setFloatable(false);
            // panel.add(toolBar, BorderLayout.CENTER);

            JCheckBox chckbxActive = new JCheckBox();
            chckbxActive.setSelected(true);
            chckbxActive.setOpaque(false);
            chckbxActive.setToolTipText("Active it");
            toolBar.add(chckbxActive);

            // toolBar.addSeparator();

            LabelCategory lblCategoryGen = new LabelCategory("F", "Function");
            toolBar.add(lblCategoryGen);

            // toolBar.addSeparator();

            JLabel lblLayerType = new JLabel("_LayerType_");
            lblLayerType.setIcon(new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png"));
            toolBar.add(lblLayerType);

            // toolBar.addSeparator();

            JLabel lblLayerName = new JLabel("_LayerName_");
            toolBar.add(lblLayerName);

            // toolBar.addSeparator();

            ColorChooserButton ccTest1 = new ColorChooserButton(Color.CYAN);
            ccTest1.setToolTipText("Yooo");
            toolBar.add(ccTest1);

            ColorChooserButton ccTest2 = new ColorChooserButton(Color.CYAN);
            ccTest2.setToolTipText("YoooPP");
            toolBar.add(ccTest2);

            // toolBar.addSeparator();

            JPanel panel_1 = new JPanel();
            toolBar.add(panel_1);
            panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

            // setMenuToBottomLeftAlignement(mnNewMenu);

            // lblLayerCategory.pack();
            // lblLayerCategory.setBorder(new EmptyBorder(0, 5, 0, 5));
            // toolBar.pack();

        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component returnValue = null;
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof Employee) {
                    Employee e = (Employee) userObject;
                    firstNameLabel.setText(e.firstName);
                    lastNameLabel.setText(e.lastName);
                    salaryLabel.setText("" + e.salary);
                    if (selected) {
                        renderer.setBackground(backgroundSelectionColor);
                    } else {
                        renderer.setBackground(backgroundNonSelectionColor);
                    }
                    renderer.setEnabled(tree.isEnabled());
                    returnValue = renderer;
                }
            }
            if (returnValue == null) {
                returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }

    }

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

    // TEST :
    private static Vector simpleTest2() {

        Employee javaBooks[] = { new Employee("A", "F", 9.99f), new Employee("B", "E", 4.99f), new Employee("C", "D", 9.95f) };
        Employee netBooks[] = { new Employee("AA", "CC", 9.99f), new Employee("BB", "DD", 9.99f) };
        Vector<Employee> javaVector = new TreeNodeVectorEmploye<Employee>("A", javaBooks);
        Vector<Employee> netVector = new TreeNodeVectorEmploye<Employee>("As", netBooks);
        Object rootNodes[] = { javaVector, netVector, new Employee("AA", "CC", 9.99f) };
        Vector<Object> rootVector = new TreeNodeVectorEmploye<Object>("Root", rootNodes);

        return rootVector;
    }

    static class TreeNodeVectorEmploye<E> extends Vector<E> {
        String name;

        TreeNodeVectorEmploye(String name) {
            this.name = name;
        }

        TreeNodeVectorEmploye(String name, E elements[]) {
            this.name = name;
            for (int i = 0, n = elements.length; i < n; i++) {
                add(elements[i]);
            }
        }

        public String toString() {
            return "[" + name + "]";
        }
    }

    static class Employee {
        public String firstName;

        public String lastName;

        public float salary;

        public Employee(String f, String l, float s) {
            this.firstName = f;
            this.lastName = l;
            this.salary = s;
        }

    }

    class EmployeeCellRenderer implements TreeCellRenderer {
        JLabel firstNameLabel = new JLabel(" ");

        JLabel lastNameLabel = new JLabel(" ");

        JLabel salaryLabel = new JLabel(" ");

        JPanel renderer = new JPanel();

        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        Color backgroundSelectionColor;

        Color backgroundNonSelectionColor;

        public EmployeeCellRenderer() {
            firstNameLabel.setForeground(Color.BLUE);
            renderer.add(firstNameLabel);

            lastNameLabel.setForeground(Color.BLUE);
            renderer.add(lastNameLabel);

            salaryLabel.setHorizontalAlignment(JLabel.RIGHT);
            salaryLabel.setForeground(Color.RED);
            renderer.add(salaryLabel);
            renderer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component returnValue = null;
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof Employee) {
                    Employee e = (Employee) userObject;
                    firstNameLabel.setText(e.firstName);
                    lastNameLabel.setText(e.lastName);
                    salaryLabel.setText("" + e.salary);
                    if (selected) {
                        renderer.setBackground(backgroundSelectionColor);
                    } else {
                        renderer.setBackground(backgroundNonSelectionColor);
                    }
                    renderer.setEnabled(tree.isEnabled());
                    returnValue = renderer;
                }
            }
            if (returnValue == null) {
                returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }
    }

    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //
    // !!!!!!!!!!!! //

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed node is the child
             * of the node we have already gotten. Otherwise, the changed node
             * and the specified node are the same.
             */
            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode) (node.getChildAt(index));
            } catch (NullPointerException exc) {
            }

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        }

        public void treeStructureChanged(TreeModelEvent e) {
        }
    }

    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            // There is no selection. Default to the root node.
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        // ...
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    public static class BookInfo {
        String name, link;

        BookInfo(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String toString() {
            return name;
        }

    }

    private static DefaultMutableTreeNode createNodes() {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("The Java Series");

        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        category = new DefaultMutableTreeNode("Books for Java Programmers");
        top.add(category);

        // original Tutorial
        book = new DefaultMutableTreeNode(new BookInfo("The Java Tutorial: A Short Course on the Basics", "tutorial.html"));
        category.add(book);

        // Tutorial Continued
        book = new DefaultMutableTreeNode(new BookInfo("The Java Tutorial Continued: The Rest of the JDK", "tutorialcont.html"));
        category.add(book);

        // Swing Tutorial
        book = new DefaultMutableTreeNode(new BookInfo("The Swing Tutorial: A Guide to Constructing GUIs", "swingtutorial.html"));
        category.add(book);

        // ...add more books for programmers...

        category = new DefaultMutableTreeNode("Books for Java Implementers");
        top.add(category);

        // VM
        book = new DefaultMutableTreeNode(new BookInfo("The Java Virtual Machine Specification", "vm.html"));
        category.add(book);

        // Language Spec
        book = new DefaultMutableTreeNode(new BookInfo("The Java Language Specification", "jls.html"));
        category.add(book);

        return top;
    }

    // TEST :
    private static Vector simpleTest() {

        Book javaBooks[] = { new Book("Core Java 2 Fundamentals", "Cornell/Horstmann", 42.99f), new Book("Taming Java Threads", "Holub", 34.95f), new Book("JavaServer  Pages", "Pekowsky", 39.95f) };
        Book htmlBooks[] = { new Book("Dynamic HTML", "Goodman", 39.95f), new Book("HTML 4 Bible", "Pfaffenberger/Gutzman", 49.99f) };
        Vector javaVector = new NamedVector("Java Books", javaBooks);
        Vector htmlVector = new NamedVector("HTML Books", htmlBooks);
        Object rootNodes[] = { javaVector, htmlVector };
        Vector rootVector = new NamedVector("Root", rootNodes);

        return rootVector;
    }

    private static class Book {
        String title;

        String authors;

        float price;

        public Book(String title, String authors, float price) {
            this.title = title;
            this.authors = authors;
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthors() {
            return authors;
        }

        public float getPrice() {
            return price;
        }
    }

    private static class LayerTreeCell implements TreeCellRenderer {

        JLabel titleLabel;

        JLabel authorsLabel;

        JLabel priceLabel;

        JPanel renderer;

        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        Color backgroundSelectionColor;

        Color backgroundNonSelectionColor;

        public LayerTreeCell() {
            renderer = new JPanel(new GridLayout(0, 1));
            titleLabel = new JLabel(" ");
            titleLabel.setForeground(Color.blue);
            renderer.add(titleLabel);
            authorsLabel = new JLabel(" ");
            authorsLabel.setForeground(Color.blue);
            renderer.add(authorsLabel);
            priceLabel = new JLabel(" ");
            priceLabel.setHorizontalAlignment(JLabel.RIGHT);
            priceLabel.setForeground(Color.red);
            renderer.add(priceLabel);
            // renderer.setBorder(BorderFactory.createLineBorder(Color.black));
            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component returnValue = null;
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof Book) {
                    Book book = (Book) userObject;
                    titleLabel.setText(book.getTitle());
                    authorsLabel.setText(book.getAuthors());
                    priceLabel.setText("" + book.getPrice());
                    if (selected) {
                        renderer.setBackground(backgroundSelectionColor);
                    } else {
                        renderer.setBackground(backgroundNonSelectionColor);
                    }
                    renderer.setEnabled(tree.isEnabled());
                    returnValue = renderer;
                }
            }
            if (returnValue == null) {
                returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }
    }

    private static class NamedVector extends Vector {
        String name;

        public NamedVector(String name) {
            this.name = name;
        }

        public NamedVector(String name, Object elements[]) {
            this.name = name;
            for (int i = 0, n = elements.length; i < n; i++) {
                add(elements[i]);
            }
        }

        public String toString() {
            return "[" + name + "]";
        }

    }

}
