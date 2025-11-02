package ch.unibas.dmi.dbis.fds.p2p.simulation;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A single network event in the {@link ch.unibas.dmi.dbis.fds.p2p.chord.api.ChordNetwork} simulation.
 *
 * @author Loris Sauter
 */
public class SimulationEvent {


  private String message;
  private long when;
  private final EventType type;
  private final Identifier source;
  private final Identifier destination;

  public SimulationEvent(EventType type, Identifier source, Identifier destination) {
    this.type = type;
    this.source = source;
    this.destination = destination;
    this.message = String.format("%d.%s(%d)", source.getIndex(), type, destination.getIndex());
  }

  public SimulationEvent(EventType type, Identifier destination) {
    this.type = type;
    this.source = null;
    this.destination = destination;
    this.message = String.format("<user>.%s(%d)", type, destination.getIndex());
  }

  public String getMessage() {
    return message;
  }

  public EventType getType() {
    return type;
  }

  public Identifier getSource() {
    return source;
  }

  public Identifier getDestination() {
    return destination;
  }

  void setWhen(long when){
    this.when = when;
  }

  public long getWhen(){
    return  when;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SimulationEvent{");
    sb.append("when=").append(when);
    sb.append(", message='").append(message).append('\'');
    sb.append(", type=").append(type);
    if (this.source != null) {
      sb.append(", source=").append(source.getIndex());
    } else {
      sb.append(", source=<user>");
    }
    sb.append(", destination=").append(destination.getIndex());
    sb.append('}');
    return sb.toString();
  }

  public enum EventType{
    FIND_SUCCESSOR, FIND_PREDECESSOR, CLOSEST_PRECEDING_FINGER, NOTIFY, JOIN, GENERIC, LOOKUP_DATA, STORE_DATA;

    /**
     *
     * @return
     */
    public Paint getColor() {
      switch (this) {
        case FIND_SUCCESSOR:
          return Color.CRIMSON;
        case FIND_PREDECESSOR:
          return Color.NAVY;
        case CLOSEST_PRECEDING_FINGER:
          return Color.GOLD;
        case NOTIFY:
          return Color.MAGENTA;
        case JOIN:
          return Color.FORESTGREEN;
        case GENERIC:
          return Color.LIGHTGRAY;
        default:
          return Color.BLACK;
      }
    }
  }
}
