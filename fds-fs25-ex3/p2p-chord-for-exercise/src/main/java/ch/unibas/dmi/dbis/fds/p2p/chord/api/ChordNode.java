package ch.unibas.dmi.dbis.fds.p2p.chord.api;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;

/**
 * This interface defines the behaviour exposed by a {@link ChordNode} in a {@link ChordNetwork}
 *
 * [1] Ion Stoica, Robert Morris, David Karger, M. Frans Kaashoek, and Hari Balakrishnan. 2001.
 *     Chord: A scalable peer-to-peer lookup service for internet applications.
 *     In Proceedings of the 2001 conference on Applications, technologies, architectures, and protocols for computer communications (SIGCOMM '01). ACM, New York, NY, USA, 149-160
 *
 * @author Loris Sauter & Ralph Gasser
 */
public interface ChordNode extends Node {

  /**
   * Returns this {@link ChordNode}'s identifier
   *
   * @return The identifier of this {@link ChordNode}
   */
  Identifier getIdentifier();

  /**
   * Returns a reference to this {@link ChordNode}'s {@link FingerTable}
   *
   * @return This {@link ChordNode}'s {@link FingerTable}
   */
  FingerTable getFingerTable();

  /**
   * Convenience method (shortcut) for {@link #getIdentifier()}
   *
   * @return The identifier of this {@link ChordNode}
   */
  default Identifier id() {
    return getIdentifier();
  }

  /**
   * Convenience method (shortcut) for {@link #getFingerTable()}
   *
   * @return This {@link ChordNode}'s {@link FingerTable}
   * @see FingerTable
   */
  default FingerTable finger() {
    return getFingerTable();
  }

  /**
   * Asks this {@link ChordNode} to find {@code of}'s successor {@link ChordNode}.
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param of The {@link ChordNode} for which to lookup the successor
   * @return The successor of the node {@code of} from this {@link ChordNode}'s point of view
   */
  default ChordNode findSuccessor(ChordNode caller, ChordNode of){
    return findSuccessor(caller, of.getIdentifier());
  }

  /**
   * Asks this {@link ChordNode} to find {@code id}'s successor {@link ChordNode}.
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param id The {@link Identifier} for which to lookup the successor. Does not need to be the ID of an actual {@link ChordNode}!
   * @return The successor of the node {@code id} from this {@link ChordNode}'s point of view
   */
  ChordNode findSuccessor(ChordNode caller, Identifier id);

  /**
   *  Asks this {@link ChordNode} to find {@code of}'s predecessor {@link ChordNode}
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param of The {@link ChordNode} for which to lookup the predecessor.
   * @return The predecessor of or the node {@code of} from this {@link ChordNode}'s point of view
   */
  default ChordNode findPredecessor(ChordNode caller, ChordNode of){
    return findPredecessor(caller, of.getIdentifier());
  }

  /**
   * Asks this {@link ChordNode} to find {@code id}'s predecessor {@link ChordNode}
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param id The {@link Identifier} for which to lookup the predecessor. Does not need to be the ID of an actual {@link ChordNode}!
   * @return The predecessor of or the node {@code of} from this {@link ChordNode}'s point of view
   */
  ChordNode findPredecessor(ChordNode caller, Identifier id);

  /**
   * Return the closest finger preceding the {@link ChordNode} {@code of}
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param of The {@link ChordNode} of which the closest preceding finger is searched for
   * @return The closest preceding finger of the node {@code of} from this node's point of view
   */
  default ChordNode closestPrecedingFinger(ChordNode caller, ChordNode of){
    return closestPrecedingFinger(caller, of.getIdentifier());
  }

  /**
   * Return the closest finger preceding the  {@code id}
   *
   * Defined in [1], Figure 4
   *
   * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
   * @param id The {@link Identifier} for which the closest preceding finger is looked up.
   * @return The closest preceding finger of the node {@code of} from this node's point of view
   */
  ChordNode closestPrecedingFinger(ChordNode caller, Identifier id);

  /**
   * Convenience method (shortcut) for the first finger of this {@link ChordNode} - its successor.
   *
   * Defined in [1], Figure 6
   *
   * @return The first entry in the finger table of this node, thus this {@link ChordNode}'s successor.
   */
  default ChordNode successor(){
    return finger().successor();
  }

  /**
   * Returns the predecessor of this {@link ChordNode}.
   *
   * @return Preceding {@link ChordNode} of this {@link ChordNode}
   */
  ChordNode predecessor();

  /**
   * Sets the predecessor of this {@link ChordNode}.
   *
   * @param node New value of the {@link ChordNode}.
   */
  void setPredecessor(ChordNode node);

  /**
   * Called on this {@link ChordNode} if it wishes to join the {@link ChordNetwork}. {@code nprime} references another {@link ChordNode}
   * that is already member of the {@link ChordNetwork}.
   *
   * Required for static {@link ChordNetwork} mode. Since no stabilization takes place in this mode, the joining node must make all
   * the necessary setup.
   *
   * Defined in [1], Figure 6
   *
   * @param nprime Arbitrary {@link ChordNode} that is part of the {@link ChordNetwork} this {@link ChordNode} wishes to join.
   */
  void joinAndUpdate(ChordNode nprime);

  /**
   * If node {@code s} is the i-th finger of this node,
   * update this node's finger table with {@code s}
   *
   * Defined in [1], Figure 6
   *
   * @param s The should-be i-th finger of this node
   * @param i The index of {@code s} in this node's finger table
   */
  void updateFingerTable(ChordNode s, int i);

  /**
   * Called on this {@link ChordNode} if it wishes to join the {@link ChordNetwork}. {@code nprime} references
   * another {@link ChordNode} that is already member of the {@link ChordNetwork}.
   *
   * Required for dynamic {@link ChordNetwork} mode. Since in that mode {@link ChordNode}s stabilize the network
   * periodically, this method simply sets its successor and waits for stabilization to do the rest.
   *
   * Defined in [1], Figure 7
   *
   * @param nprime Arbitrary {@link ChordNode} that is part of the {@link ChordNetwork} this {@link ChordNode} wishes to join.
   */
  void joinOnly(ChordNode nprime);

  /**
   * Called periodically in order to verify this node's immediate successor and inform it about this
   * {@link ChordNode}'s presence,
   *
   * Defined in [1], Figure 7
   */
  void stabilize();

  /**
   * Called by {@code nprime} if it thinks it might be this {@link ChordNode}'s predecessor. Updates predecessor
   * pointers accordingly, if required.
   *
   * Defined in [1], Figure 7
   *
   * @param nprime The alleged predecessor of this {@link ChordNode}
   */
  void notify(ChordNode nprime);

  /**
   * Called periodically in order to refresh entries in this {@link ChordNode}'s {@link FingerTable}.
   *
   * Defined in [1], Figure 7
   */
  void fixFingers();

  /**
   * Called periodically in order to check activity of this {@link ChordNode}'s predecessor.
   *
   * Not part of [1]. Required for dynamic network to handle node failure.
   */
  void checkPredecessor();

  /**
   * Called periodically in order to check activity of this {@link ChordNode}'s successor.
   *
   * Not part of [1]. Required for dynamic network to handle node failure.
   */
  void checkSuccessor();
}
