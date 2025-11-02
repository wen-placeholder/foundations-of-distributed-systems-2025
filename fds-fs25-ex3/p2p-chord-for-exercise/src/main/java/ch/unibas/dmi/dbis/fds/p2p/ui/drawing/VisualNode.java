package ch.unibas.dmi.dbis.fds.p2p.ui.drawing;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.NodeStatus;
import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationPeer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Optional;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public final class VisualNode extends AbstractVisualNode {

  /** The {@link SimulationPeer} associated with this {@link VisualNode}. */
  private SimulationPeer peer;

  /**
   *
   * @param number
   */
  public VisualNode(int number){
    super(number);
  }

  /**
   * Getter for the {@link SimulationPeer} underpinning this {@link VisualNode}. This method is supposed to be used
   * only in the context of simulation and UI display.
   *
   * @return {@link SimulationPeer} associated with this {@link VisualNode}.
   */
  public Optional<SimulationPeer> getPeer() {
    return Optional.ofNullable(this.peer);
  }

  /**
   * Setter for the {@link SimulationPeer} underpinning this {@link VisualNode}. This method is supposed to be used
   * only in the context of simulation and UI display.
   *
   * @param peer New {@link SimulationPeer}.
   */
  public void setPeer(SimulationPeer peer){
    this.peer = peer;
  }

  /**
   * Returns true if this {@link VisualNode} currently has a peer and false otherwise.
   *
   * @return True if a {@link SimulationPeer} is associated with this {@link VisualNode}.
   */
  public final boolean hasPeer() {
    return peer != null;
  }

  /**
   * Returns true if the peer associated with this {@link VisualNode} is currently online, and false otherwise.
   *
   * @return Online state of the {@link SimulationPeer} associated with this {@link VisualNode}.
   */
  public final boolean isOnline() {
    return this.peer != null && this.peer.status() == NodeStatus.ONLINE;
  }

  /**
   * Returns true if the peer associated with this {@link VisualNode} is currently joining, and false otherwise.
   *
   * @return Joining state of the {@link SimulationPeer} associated with this {@link VisualNode}.
   */
  @Override
  public final boolean isJoining() {
    return this.peer != null && this.peer.status() == NodeStatus.JOINING;
  }
}
