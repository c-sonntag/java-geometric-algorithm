package at.u4a.geometric_algorithms.gui.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import at.u4a.geometric_algorithms.gui.layer.LayerMannager;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DrawerScene {

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
            
            scene.getRoot().setCursor(Cursor.WAIT);
            root.setCursor(Cursor.WAIT);
            
            //
            fxPanel.setScene(scene);
        }
    }

    // FX to Swing
    private final InterfaceDrawerAction da;
    private final JFXPanel fxPanel = new JFXPanel();
    private FX fx = null;

    // LayerManager
    private final LayerMannager layers = new LayerMannager();

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
            }
        });
        setTool(Tool.Selection);
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
        this.da.activeDrawerAction(currentState.needValidOperation());
        

        //
        repaint();
    }

    public void refresh() {
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

    public LayerMannager getLayerMannager() {
        return layers;
    }

    public ToolState getCurrentState() {
        return currentState;
    }
    
    public void addInActiveLayer() {
        
        
    }

}
