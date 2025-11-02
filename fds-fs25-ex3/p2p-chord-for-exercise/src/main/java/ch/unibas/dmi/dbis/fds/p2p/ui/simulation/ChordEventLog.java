package ch.unibas.dmi.dbis.fds.p2p.ui.simulation;

import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEngine;
import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEvent;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

/**
 * This view is used to display a log of {@link SimulationEvent}s.
 *
 * @author Loris Sauter & Ralph Gasser
 */
public class ChordEventLog extends AnchorPane {
  /** This {@link ListView} to display events. */
  private final ListView<SimulationEvent> list = new ListView<>();

  /**
   * Constructor for {@link ChordEventLog}.
   *
   * @param engine The {@link SimulationEngine} for which to display the events.
   */
  public ChordEventLog(SimulationEngine engine){
    /* Link items and activate auto scroll. */
    this.list.setItems(engine.allEvents());
    this.list.getItems().addListener((ListChangeListener<SimulationEvent>) c -> ChordEventLog.this.list.scrollTo(c.getList().size()-1));

    /* Set cell factory to create list items. */
    this.list.setCellFactory(param -> new SimulationEventCell());

    /* Add list and anchor it to the ChordEventLog. */
    this.getChildren().add(this.list);
    AnchorPane.setTopAnchor(this.list, 0.0);
    AnchorPane.setBottomAnchor(this.list, 0.0);
    AnchorPane.setLeftAnchor(this.list, 0.0);
    AnchorPane.setRightAnchor(this.list, 0.0);
  }

  /**
   * A custom {@link ListCell} used to display {@link SimulationEvent}.
   */
  static class SimulationEventCell extends ListCell<SimulationEvent> {
    @Override
    public void updateItem(SimulationEvent item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        this.setText(String.format("[%d]: %s", item.getWhen(), item.getMessage()));
        this.setTextFill(item.getType().getColor());
      }
    }
  }
}
