package at.u4a.geometric_algorithms.gui.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import at.u4a.geometric_algorithms.gui.layer.LayerMannager;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DrawerScene {

    // Initialize Final Variable
    private final Group root = new Group();
    private final Scene scene;
    private final Drawer drawer = new Drawer(this);
    private final JFXPanel fxPanel = new JFXPanel();

    private final Map<Tool, ToolButton> toolsItems = new HashMap<Tool, ToolButton>();

    // LayerManager
    private final LayerMannager layers = new LayerMannager();

    // DrawerContext
    private Tool currentTool;
    private ToolState currentState;

    public DrawerScene() {
        scene = new Scene(root, Color.WHITE);
        //
        fxPanel.setScene(scene);
        root.getChildren().add(drawer);
        //
        setTool(Tool.Selection);
    }

    public JFXPanel getPanel() {
        return fxPanel;
    }

    public void addToolButton(ToolButton btnTool) {

        toolsItems.put(btnTool.getTool(), btnTool);

        btnTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setTool(btnTool.getTool());
            }
        });

        // TODO Auto-generated method stub

    }

    /**
     * Initialize Canvas
     */
    private void initializeCanvas() {

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

        // Drawer canvas = new Drawer();

        return (scene);
    }

    /**
     * @todo gerer l'outil pour terminer son travail (valid, cancel)
     * @param tool
     */
    public void setTool(Tool tool) {

        for (Entry<Tool, ToolButton> toolControlEntry : toolsItems.entrySet()) {
            toolControlEntry.getValue().setActive(tool.equals(toolControlEntry.getKey()));
        }

        if (tool == currentTool)
            return;

        //
        /*
         * for (Entry<Tool, ToolControl> toolControlEntry :
         * toolsControl.entrySet()) {
         * toolControlEntry.getValue().setActive(tool.equals(toolControlEntry.
         * getKey()));
         * 
         * if (tool.equals(toolControlEntry.getKey())) {
         * toolControlEntry.getValue().data.btn.setSelected(true); } else {
         * toolControlEntry.getValue().data.btn.setSelected(false); } }
         */

        this.currentTool = tool;
        this.currentState = tool.supplier.get();

        //
        drawer.repaint();
    }

    public void refresh() {
        setTool(currentTool);
    }

    /* */

    public Drawer drawer() {
        return drawer;
    }

    public LayerMannager getLayers() {
        return layers;
    }

    public ToolState getCurrentState() {
        return currentState;
    }

}
