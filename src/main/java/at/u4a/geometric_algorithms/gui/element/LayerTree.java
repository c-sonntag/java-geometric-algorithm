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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

/**
 * 
 * @author Christophe Sonntag
 * @see Source for work :
 *      http://www.java2s.com/Code/Java/Swing-JFC/TreeCellRenderer.htm
 *
 */

public class LayerTree extends JTree {

    public LayerTree() {
        
        super( simpleTest() );
        
        
        TreeCellRenderer renderer = new LayerTreeCell();
        this.setCellRenderer(renderer);

         
        
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
            //renderer.setBorder(BorderFactory.createLineBorder(Color.black));
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
