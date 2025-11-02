package ch.unibas.dmi.dbis.fds.p2p.ui.simulation;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.FingerTable;
import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.VisualNode;
import ch.unibas.dmi.dbis.fds.p2p.utilities.Triple;
import ch.unibas.dmi.dbis.fds.p2p.utilities.Tuple;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Node inspector can be used to display properties of a {@link VisualNode} in the {@link ChordPanel}.
 * Properties include the finger table as well as data that is currently stored in the node.
 *
 * @author Ralph Gasser
 */
public class NodeInspector extends AnchorPane {

    /** Label that describes the focused {@link VisualNode}. */
    private final Label label;

    /** List of data entries stored in the focused {@link VisualNode}. */
    private final ObservableList<Triple<String,String, String>> data = FXCollections.observableArrayList();

    /** List of finger's in the {@link FingerTable} of the focused {@link VisualNode}. */
    private final ObservableList<Triple<String,String,String>> fingers = FXCollections.observableArrayList();

    /**
     * Constructor for {@link NodeInspector}.
     *
     * @param panel Reference to the {@link ChordPanel} this {@link NodeInspector} belongs.
     */
    public NodeInspector(ChordPanel panel) {
        if (panel == null) throw new IllegalArgumentException("The ChordPanel cannot be null!");

        /* Prepare label. */
        this.label = new Label("Inspector: <none selected>");
        this.label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize()));
        this.label.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

        /* Setup TableView for FingerTable. */
        final TableView<Triple<String,String,String>> fingerTable = new TableView<>();

        final TableColumn<Triple<String, String, String>,String> startColumn = new TableColumn<>("Start");
        startColumn.setMinWidth(100);
        startColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFirst()));

        final TableColumn<Triple<String, String, String>,String> intervalColumn = new TableColumn<>("Interval");
        intervalColumn.setMinWidth(100);
        intervalColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getSecond()));

        final TableColumn<Triple<String, String, String>,String> successorColumn = new TableColumn<>("Successor");
        successorColumn.setMinWidth(100);
        successorColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getThird()));

        fingerTable.getColumns().addAll(startColumn, intervalColumn, successorColumn);
        fingerTable.setItems(this.fingers);

        /* Setup TableView for data. */
        final TableView<Triple<String, String, String>> dataTable = new TableView<>();

        final TableColumn<Triple<String, String, String>,String> keyColumn = new TableColumn<>("Key");
        keyColumn.setMinWidth(100);
        keyColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFirst()));

        final TableColumn<Triple<String, String, String>,String> hashColumn = new TableColumn<>("Hash");
        hashColumn.setMinWidth(100);
        hashColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getSecond()));

        final TableColumn<Triple<String, String, String>,String> valueColumn = new TableColumn<>("Value");
        valueColumn.setMinWidth(100);
        valueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getThird()));

        dataTable.getColumns().addAll(keyColumn, hashColumn, valueColumn);
        dataTable.setItems(this.data);

        /* Setup TabPane and its tabs. */
        final TabPane tabPane = new TabPane();
        final Tab dataTab = new Tab();
        dataTab.setText("Data");
        dataTab.setContent(dataTable);
        dataTab.setClosable(false);

        final Tab fingerTab = new Tab();
        fingerTab.setText("Fingers");
        fingerTab.setContent(fingerTable);
        fingerTab.setClosable(false);
        tabPane.getTabs().addAll(dataTab, fingerTab);

        /* Add TabPane. */
        this.getChildren().addAll(this.label, tabPane);

        /* Anchor the label to the top. */
        AnchorPane.setTopAnchor(this.label, 0.0);
        AnchorPane.setLeftAnchor(this.label, 0.0);
        AnchorPane.setRightAnchor(this.label, 0.0);

        /* Anchor the label to the bottom. */
        AnchorPane.setTopAnchor(tabPane, 20.0);
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);

        /* Register event handler for when selection of node changes in SimulationEngine. */
        panel.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.data.clear();
            this.fingers.clear();
            if (newValue.hasPeer()) {
                handleActiveNode(newValue);
            } else {
                handleInactiveNode(newValue);
            }
        });
    }

    /**
     * Handles the display of an focus {@link VisualNode} that was selected in the {@link ChordPanel} associated with this {@link NodeInspector}.
     *
     * @param node The {@link VisualNode} that should be handled.
     */
    private void handleActiveNode(final VisualNode node) {
        node.getPeer().ifPresent(p -> {
            if (p.predecessor() == null) {
                this.label.setText(String.format("Inspector: Node %d (Predecessor: null)", p.getIdentifier().getIndex()));
            } else {
                this.label.setText(String.format("Inspector: Node %d (Predecessor: %d)", p.getIdentifier().getIndex(), p.predecessor().getIdentifier().getIndex()));
            }
            p.dump().forEach((k,v) -> this.data.add(new Triple<>(k, String.valueOf(p.getNetwork().getHashFunction().hash(k)), v)));
            for(int k=1; k<=p.finger().size(); k++){
                final Triple<String,String,String> triple = new Triple<>();
                triple.setFirst(String.valueOf(p.finger().start(k)));
                triple.setSecond(p.finger().interval(k).toString());
                p.finger().node(k).ifPresent(n -> triple.setThird(String.valueOf(n.getIdentifier().getIndex())));
                this.fingers.add(triple);
            }
        });
    }

    /**
     * Handles the display of an inactive {@link VisualNode} that was selected in the {@link ChordPanel} associated with this {@link NodeInspector}.
     *
     * @param node The {@link VisualNode} that should be handled.
     */
    private void handleInactiveNode(VisualNode node) {
        this.label.setText(String.format("Inspector: Node %d <Inactive>", node.getNumber()));
    }
}

