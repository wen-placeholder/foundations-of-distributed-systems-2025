package ch.unibas.dmi.dbis.fds.p2p.ui.simulation;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.ChordNode;
import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEngine;
import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEvent;
import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationPeer;
import ch.unibas.dmi.dbis.fds.p2p.ui.components.ChordLegend;
import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.ChordGraphicsContext;
import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.VisualNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.unibas.dmi.dbis.fds.p2p.utilities.Tuple;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * The {@link ChordPanel} is used to visualize the simulation of a {@link ch.unibas.dmi.dbis.fds.p2p.chord.api.ChordNetwork}.
 *
 * @author Loris Sauter & Ralph Gasser
 */
public class ChordPanel extends VBox {

    /** Reference to the {@link Canvas} onto which the simulation is drawn. */
    private Canvas canvas;

    /** A {@link ContextMenu} reference. */
    private final ContextMenu contextMenu = new ContextMenu();

    /** List of nodes that should be visualized. Visualisation takes place regardless of whether node is actually active. */
    private List<VisualNode> visualNodes;

    /** Reference to the {@link SimulationEngine} underpinning this {@link ChordPanel}. */
    private final SimulationEngine engine;

    /** Reference to the currently selected {@link VisualNode}. */
    private final ObjectProperty<VisualNode> selected = new SimpleObjectProperty<>();

    /** The {@link AnimationTimer} that updates the Canvas. */
    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
        ChordPanel.this.update();
        ChordPanel.this.redraw();
        }
    };

    /**
     * Constructor for {@link ChordPanel}
     *
     * @param size Size of the {@link ChordPanel} (width and height)
     */
    public ChordPanel(double size, SimulationEngine engine) {
        this.canvas = new Canvas(size, size);
        this.canvas.minHeight(size);
        getChildren().addAll(canvas);

        /* Setup binding of width and height and make resizing trigger the redraw() method. */
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty().subtract(100));
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> redraw());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redraw());

        /* Setup the ContextMenu. */
        this.canvas.setOnContextMenuRequested(e -> {
            if (engine == null || engine.isPaused()) {
                return;
            }

            final Point2D evtPos = new Point2D(e.getSceneX() - canvas.getLayoutX(), e.getSceneY() - canvas.getLayoutY());
            final VisualNode node = getClickedNode(evtPos);
            if (node != null) {
                final VBox container = new VBox();
                container.setPadding(new Insets(5));
                container.getChildren().addAll(new Label("Node " + node.getNumber()));
                final CustomMenuItem title = new CustomMenuItem(container);
                title.setHideOnClick(false);
                title.getStyleClass().clear();

                this.contextMenu.getItems().clear();

                if (node.hasPeer()) {
                    final MenuItem item1 = new MenuItem("Remove Peer");
                    item1.setOnAction(evt -> this.handleNodeLeaveMenuItem(node));

                    final MenuItem item2 = new MenuItem("Store data");
                    item2.setOnAction(evt -> this.handleDataStoreMenuItem(node));

                    final MenuItem item3 = new MenuItem("Lookup data");
                    item3.setOnAction(evt -> this.handleDataLookupMenuItem(node));

                    this.contextMenu.getItems().addAll(title, new SeparatorMenuItem(), item1, new SeparatorMenuItem(), item2, item3);

                } else {
                    final MenuItem item = new MenuItem("Add Peer");
                    item.setOnAction(evt -> this.handleNodeJoinMenuItem(node));
                    this.contextMenu.getItems().addAll(title, new SeparatorMenuItem(), item);
                }

                this.contextMenu.show(canvas, e.getScreenX(), e.getScreenY());
            }
        });

        /* Setup MouseClick Event Handler. */
        this.canvas.setOnMouseClicked(this::handleMouseClicked);

        /* Setup the simulation engine. */
        this.engine = engine;
        setupVisualNodes(engine.getNetwork().size());

        /* Starts the animation timer. */
        this.timer.start();
    }

    /**
     * Handler for the "Add Peer" context menu item.
     *
     * @param node The {@link VisualNode} that triggered the action.
     */
    private void handleNodeJoinMenuItem(VisualNode node) {
        this.engine.nodeJoining(node.getNumber());
    }

    /**
     * Handler for the "Remove Peer" context menu item.
     *
     * @param node The {@link VisualNode} that triggered the action.
     */
    private void handleNodeLeaveMenuItem(VisualNode node) {
        this.engine.nodeLeaving(node.getNumber());
    }

    /**
     * Handler for the "Store data" context menu item.
     *
     * @param node The {@link VisualNode} that triggered the action.
     */
    private void handleDataStoreMenuItem(VisualNode node) {
        final TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Input your data item.");
        dialog.setHeaderText("Please enter the data you would like to store. The format must be <key>:<value>. Existing keys will be overwritten!");
        dialog.setContentText("Data item (<key>:<value>):");
        dialog.showAndWait().map(s -> s.split(":")).filter(s -> s.length == 2).ifPresent(s -> {
            this.engine.storeData(node.getNumber(), s[0], s[1]);
        });
    }

    /**
     * Handler for the "Store data" context menu item.
     *
     * @param node The {@link VisualNode} that triggered the action.
     */
    private void handleDataLookupMenuItem(VisualNode node) {
        final TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Input your data item.");
        dialog.setHeaderText("Please enter the key of the data item you would like to retrieve.");
        dialog.setContentText("Key");
        final Optional<Tuple<String,String>> result = dialog.showAndWait().map(s -> this.engine.lookupData(node.getNumber(), s).map(r -> new Tuple<>(s,r)).orElse(new Tuple<>(s,(String)null)));
        result.ifPresent(r -> {
            if (r.getSecond() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Lookup failed");
                alert.setHeaderText(String.format("Could not find value for key '%s'", r.getFirst()));
                alert.setContentText(String.format("%s --> null", r.getFirst()));
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lookup successful");
                alert.setHeaderText(String.format("Successfully retrieved value '%s' for key '%s'", r.getSecond(), r.getFirst()));
                alert.setContentText(String.format("%s --> %s", r.getFirst(), r.getSecond()));
                alert.showAndWait();
            }
        });
    }

    /**
     * Handler for simple mouse clicks on the Canvas.
     *
     * @param evt Reference to the {@link MouseEvent}
     */
    private void handleMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseButton.PRIMARY) {
            /* Hide context menu on click in canvas. */
            if (this.contextMenu.isShowing()) {
                this.contextMenu.hide();
            }

            /* Handle general Left-Click. */
            final Point2D evtPos = new Point2D(evt.getSceneX() - canvas.getLayoutX(), evt.getSceneY() - canvas.getLayoutY());
            final VisualNode node = getClickedNode(evtPos);
            if (node != null) {
                this.selected.setValue(node);
            }
        }
    }

    /**
     *
     * @param size
     */
    private void setupVisualNodes(int size) {
        this.visualNodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.visualNodes.add(new VisualNode(i));
        }
    }



    private VisualNode getClickedNode(double x, double y) {
        if (visualNodes == null || visualNodes.isEmpty()) {
            return null;
        }
        for (VisualNode n : visualNodes) {
            if (n.isInArea(x, y)) {
                return n;
            }
        }
        return null;
    }

    private VisualNode getClickedNode(Point2D pos) {
        return getClickedNode(pos.getX(), pos.getY());
    }

    /**
     * Aligns the local data model (for visualisation) with the current state of the {@link SimulationEngine},
     */
    private void update() {
        for (int i = 0; i < this.engine.getNetwork().size(); i++) {
            final Optional<SimulationPeer> node = this.engine.getNetwork().getSimulationPeer(i);
            if (node.isPresent()) {
                visualNodes.get(i).setPeer(node.get());
            } else {
                visualNodes.get(i).setPeer(null);
            }
        }
    }

    /**
     * Redraws the ChordCanvas: Ring + Nodes.
     */
    private void redraw() {
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        final Paint fill = gc.getFill();
        final Paint stroke = gc.getStroke();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(fill);

        /* Draw the ChordRing. */
        final int radius = (int) Math.min(canvas.getWidth(), canvas.getHeight()) / 2 - 50;
        final ChordGraphicsContext cgc = new ChordGraphicsContext(gc);
        cgc.strokeCircle(0, 0, radius);
        gc.setFill(Color.BLACK);

        /* Draw nodes. */
        cgc.renderRegularlyOnCircle(0, 0, radius, this.visualNodes);

        /* Draw simulation counter. */
        gc.setStroke(Color.BLACK);
        gc.strokeText(String.format("t=%d, paused=%b", this.engine.getSimulationStep(), this.engine.isPaused()), 20, 20);
        gc.setFill(fill);
        gc.setStroke(stroke);

        /* Draw the actual events that are currently happening. */
        for (SimulationEvent event : this.engine.eventBuffer()) {
            this.drawEvent(event);
        }
    }

    /**
     * Draws a single {@link SimulationEvent} on the ChordCanvas.
     *
     * @param event The {@link SimulationEvent} that should be drawn.
     */
    private void drawEvent(SimulationEvent event) {
        VisualNode src = null;
        final VisualNode dest = visualNodes.get(event.getDestination().getIndex());
        if (event.getSource() != null) {
            src = visualNodes.get(event.getSource().getIndex());
        }

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        final Paint stroke = gc.getStroke();
        final Paint fill = gc.getFill();
        gc.setStroke(event.getType().getColor());
        gc.setFill(event.getType().getColor());
        if (src == null) {
            double dx = (dest.getPosition().getX() - canvas.getWidth() / 2.0) / 5.0;
            double dy = (dest.getPosition().getY() - canvas.getHeight() / 2.0) / 5.0;
            ChordGraphicsContext.strokeArrow(gc, dest.getPosition().getX() + dx, dest.getPosition().getY() + dy, dest.getPosition().getX(), dest.getPosition().getY());
        } else if (src == dest) {
            double dx = (dest.getPosition().getX() - canvas.getWidth() / 2.0) / 10.0;
            double dy = (dest.getPosition().getY() - canvas.getHeight() / 2.0) / 10.0;
            ChordGraphicsContext.strokeArrow(gc, dest.getPosition().getX() - dx, dest.getPosition().getY() - dy, dest.getPosition().getX(), dest.getPosition().getY());

        } else {
            ChordGraphicsContext.strokeArrow(gc, src.getPosition().getX(), src.getPosition().getY(), dest.getPosition().getX(), dest.getPosition().getY());
        }

        gc.setStroke(Color.GRAY);
        gc.setStroke(stroke);
        gc.setFill(fill);
    }

    /**
     * Getter for the {@link ChordPanel#selected} property. Can be used to observe selection changes.
     *
     * @return {@link ReadOnlyObjectProperty <SimulationPeer>}
     */
    public ReadOnlyObjectProperty<VisualNode> selectedProperty() {
        return this.selected;
    }

    /**
     * Displays the{@link ChordLegend} in an alert.
     */
    public void showHelp() {
        final ChordLegend info = new ChordLegend();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help: Chord Simulation");
        alert.setHeaderText("This application simulates and visualizes a chord ring. See the official paper for further information about Chord.");
        alert.setContentText(
            "Usage: Start the simulation via the start button and stop the simulation via the stop button. Adjust the simulation speed with the slider.\n" +
            "Click a node to display its properties in the inspector. Right click a node to join / remove a node or store some data."
        );
        alert.getDialogPane().setExpandableContent(info);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
}
