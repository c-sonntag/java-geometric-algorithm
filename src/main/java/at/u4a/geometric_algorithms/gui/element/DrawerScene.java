package at.u4a.geometric_algorithms.gui.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import at.u4a.geometric_algorithms.gui.layer.LayerManager;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class DrawerScene {

    private static final Cursor CURSOR_NO_TOOLS = Cursor.WAIT;

    private class FX {
        final Group root = new Group();
        final Scene scene;
        final Drawer drawer;

        FX() {
            drawer = new Drawer(DrawerScene.this);
            drawer.setVisible(true);
            //
            scene = new Scene(root);
            //
            fxPanel.setScene(scene);
            //
            drawer.getGraphicsContext2D().setFill(Color.AQUA);
            root.getChildren().add(drawer);
            //
            // https://blog.idrsolutions.com/2012/11/adding-a-window-resize-listener-to-javafx-scene/
            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    drawer.setWidth(newSceneWidth.doubleValue());
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    drawer.setHeight(newSceneHeight.doubleValue());
                }
            });

            scene.getRoot().setCursor(CURSOR_NO_TOOLS);
            root.setCursor(CURSOR_NO_TOOLS);

            //
            fxPanel.setScene(scene);
        }
    }

    // FX to Swing
    private final InterfaceDrawerAction da;
    private final JFXPanel fxPanel = new JFXPanel();
    private FX fx = null;

    // LayerManager
    private final LayerManager layers = new LayerManager(this);

    // Tools
    private final Map<Tool, ToolButton> toolsItems = new HashMap<Tool, ToolButton>();

    // DrawerContext
    private Tool currentTool;
    private ToolState currentState;

    public DrawerScene(InterfaceDrawerAction da) {
        this.da = da;
        //
        Platform.runLater(new Runnable() {
            public void run() {
                fx = new DrawerScene.FX();
                setTool(null);
                // setTool(Tool.Selection);
            }
        });

    }

    public JFXPanel getPanel() {
        return fxPanel;
    }

    public InterfaceDrawerAction getDrawerAction() {
        return da;
    }

    public void addToolButton(ToolButton btnTool) {

        toolsItems.put(btnTool.getTool(), btnTool);

        btnTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setTool(btnTool.getTool());
            }
        });
    }

    /**
     * TODO gerer l'outil pour terminer son travail (valid, cancel)
     * 
     * @param tool
     */
    public void setTool(Tool tool) {

        //
        if (fx == null)
            throw new RuntimeException("Need FX is initialized");

        //
        for (Entry<Tool, ToolButton> toolControlEntry : toolsItems.entrySet()) {
            toolControlEntry.getValue().setActive((tool == null) ? false : tool.equals(toolControlEntry.getKey()));
        }

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

        //
        if (tool == null) {
            this.currentState = null;
            this.da.activeDrawerAction(false);

        } else if (tool != currentTool) {

            this.currentTool = tool;

            this.currentState = tool.supplier.get();
            this.currentState.init(fx.drawer); // Need fx !
            this.da.activeDrawerAction(currentState.needValidOperation());

        }

        //
        repaint();
    }

    public void refresh() {
        if (fx != null)
            setTool(currentTool);
    }

    public void repaint() {
        if (fx != null)
            fx.drawer.repaint();
    }
    

    /* */

    // public Drawer drawer() {
    // return drawer;
    // }

    public LayerManager getLayerMannager() {
        return layers;
    }

    public ToolState getCurrentState() {
        return currentState;
    }

    public void addInActiveLayer() {

    }

}
