package at.u4a.geometric_algorithms.gui.element;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.print.Book;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.u4a.geometric_algorithms.gui.element.LayerTree.BookInfo;
import at.u4a.geometric_algorithms.gui.layer.LayerMannager;

/**
 * 
 * @author Christophe Sonntag
 * @see Source for work :
 *      http://www.java2s.com/Code/Java/Swing-JFC/TreeCellRenderer.htm
 *      https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
 *
 */

public class LayerTree extends JTree {


    protected final LayerMannager lm;

    public LayerTree(LayerMannager lm) {

        //super(simpleTest());

        //super(createNodes());

        this.lm = lm;

        TreeCellRenderer renderer = new LayerTreeCell();
        this.setCellRenderer(renderer);

    }
    
    
    
    
    
    
    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)
                     (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we have already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */
            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)
                       (node.getChildAt(index));
            } catch (NullPointerException exc) {}

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
            //There is no selection. Default to the root node.
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);
        //...
        treeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
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
