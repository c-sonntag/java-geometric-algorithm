package at.u4a.geometric_algorithms._others;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
/* w w w  .ja v  a 2 s . c o m*/
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public class TestJTree {
    public static JComponent makeUI() {
        JTree tree = new JTree();
        TreeModel model = tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Object o = node.getUserObject();
            if (o instanceof String) {
                node.setUserObject(new CheckBoxNode((String) o, false));
            }
        }
        tree.setEditable(true);
        tree.setCellRenderer(new CheckBoxNodeRenderer());
        tree.setCellEditor(new CheckBoxNodeEditor());
        tree.expandRow(0);
        return new JScrollPane(tree);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().add(new TestJTree().makeUI());
        f.setSize(320, 240);
        f.setVisible(true);
    }
}

class CheckBoxNode {
    public final String text;
    public final boolean selected;

    public CheckBoxNode(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return text;
    }
}

class CheckBoxNodeRenderer implements TreeCellRenderer {
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    JCheckBox check = new JCheckBox();
    JPanel p = new JPanel(new BorderLayout());

    public CheckBoxNodeRenderer() {
        p.setFocusable(false);

        p.setOpaque(false);
        p.add(check, BorderLayout.WEST);
        check.setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel l = (JLabel) renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        System.out.println("CheckBoxNodeRenderer\t Width(" + String.valueOf(l.getPreferredSize().getWidth()) + ") Height(" + String.valueOf(l.getPreferredSize().getHeight()) + ")  ");

        if (value instanceof DefaultMutableTreeNode) {
            check.setEnabled(tree.isEnabled());
            check.setFont(tree.getFont());
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof CheckBoxNode) {
                CheckBoxNode node = (CheckBoxNode) userObject;
                l.setText(node.text);
                check.setSelected(node.selected);
            }
            p.add(l);
            return p;
        }
        return l;
    }
}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    JCheckBox check = new JCheckBox();
    JPanel p = new JPanel(new BorderLayout());
    String str = null;

    public CheckBoxNodeEditor() {
        super();
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });
        p.setFocusable(false);
        p.setRequestFocusEnabled(false);
        p.setOpaque(false);
        p.add(check, BorderLayout.WEST);
        check.setOpaque(false);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        JLabel l = (JLabel) renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

        System.out.println("CheckBoxNodeEditor\t Width(" + String.valueOf(l.getPreferredSize().getWidth()) + ") Height(" + String.valueOf(l.getPreferredSize().getHeight()) + ")  ");

        if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof CheckBoxNode) {
                CheckBoxNode node = (CheckBoxNode) userObject;
                l.setText(node.text);
                check.setSelected(node.selected);
                str = node.text;
            }
            p.add(l);
            return p;
        }
        return l;
    }

    @Override
    public Object getCellEditorValue() {
        return new CheckBoxNode(str, check.isSelected());
    }


    public boolean isCellEditable___(EventObject event) {


        boolean returnValue = false;
        if (event instanceof MouseEvent) {

            MouseEvent mouseEvent = (MouseEvent) event;
            TreePath path = ((JTree) event.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());

            if (path != null) {
                Object node = path.getLastPathComponent();
                if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    Object userObject = treeNode.getUserObject();
                    // returnValue = ((treeNode.isLeaf()) && (userObject
                    // instanceof AbstractLayer));
                    returnValue = true; // (userObject instanceof
                                        // AbstractLayer);
                }
            }
        }
        return returnValue;
    }

    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
            MouseEvent me = (MouseEvent) e;
            JTree tree = (JTree) e.getSource();
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            Rectangle r = tree.getPathBounds(path);
            if (r == null) {
                return false;
            }
            Dimension d = check.getPreferredSize();
            r.setSize(new Dimension(d.width, r.height));
            if (r.contains(me.getX(), me.getY())) {
                check.setBounds(new Rectangle(0, 0, d.width, r.height));
                return true;
            }
        }
        return false;
    }

}
