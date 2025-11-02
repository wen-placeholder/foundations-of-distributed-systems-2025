package ch.unibas.dmi.dbis.fds.p2p.chord.api;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;
import ch.unibas.dmi.dbis.fds.p2p.chord.api.math.HashFunction;

/**
 * This interface defines the behaviour exposed by a {@link ChordNetwork}. The implementations in this project come in two flavors as described in [1]:
 *
 * - Static networks: No stabilization, no fix fingers, joining  {@link ChordNode}s are responsible for all the setup.
 * - Dynamic networks: Stabilization & fix fingers, joining {@link ChordNode}s only do a minimal setup.
 *
 * [1] Ion Stoica, Robert Morris, David Karger, M. Frans Kaashoek, and Hari Balakrishnan. 2001.
 *     Chord: A scalable peer-to-peer lookup service for internet applications.
 *     In Proceedings of the 2001 conference on Applications, technologies, architectures, and protocols for computer communications (SIGCOMM '01). ACM, New York, NY, USA, 149-160
 *
 * @author Loris Sauter & Ralph Gasser
 */
public interface ChordNetwork {
  /**
   * Accessor for the {@link IdentifierCircle} that is underpinning the {@link ChordNetwork}.
   *
   * @return {@link IdentifierCircle<Identifier>}
   */
  IdentifierCircle<Identifier> getIdentifierCircle();

  /**
   * Getter for the {@link HashFunction} that is underpinning the {@link ChordNetwork}.
   *
   * @return {@link HashFunction} reference.
   */
  HashFunction getHashFunction();

  /**
   * Number of bits used to construct the chord ring.
   *
   * @return Number of bits.
   */
  int getNbits();

  /**
   * Size of the {@link ChordNetwork} in terms of the maximum number of {@link ChordNode}s supported.
   *
   * @return Size of the {@link ChordNetwork}.
   */
  default int size() {
    return (int)Math.pow(2, getNbits());
  }

  /**
   * Indicates whether or not this {@link ChordNetwork} uses dynamic mode:
   *
   * - static mode: NodeJoins call full setup, not stabilization.
   * - dynamic mode: Joins
   *
   * @return True if dynamic, false otherwise.
   */
  boolean isDynamic();
}
