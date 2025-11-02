package ch.unibas.dmi.dbis.fds.p2p.chord.api;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.IdentifierCircularInterval;

import java.util.Optional;

/**
 * This interface defines the functionality of a {@link FingerTable} as used in {@link ChordNode}s.
 *
 * @author Loris Sauter & Ralph Gasser
 */
public interface FingerTable {
  /**
   * Returns the start value of the interval of the k-th entry (one-based) in the {@link FingerTable}.
   *
   * @param k The index in the {@link FingerTable} that should be accessed (one-based).
   * @return Start value of the k-th entry in the {@link FingerTable}.
   * @throws IndexOutOfBoundsException If k is > size()
   */
  int start(int k);

  /**
   * Returns the {@link IdentifierCircularInterval} of the k-th entry (one-based) in the {@link FingerTable}.
   *
   * @param k The index in the {@link FingerTable} that should be accessed (one-based).
   * @return Start value of the k-th entry in the {@link FingerTable}.
   * @throws IndexOutOfBoundsException If k is > size()
   */
  IdentifierCircularInterval interval(int k);

  /**
   * Size of the {@link FingerTable}, i.e. the number of entries it may hold.
   *
   * @return Size of the {@link FingerTable}.
   */
  int size();

  /**
   * Returns the first entry from the {@link FingerTable}, i.e. the successor of the enclosing {@link ChordNode}. In an operating
   * {@link ChordNode}, this entry should always be set as soon as the {@link ChordNode} has completed its join operation.
   *
   * @return The first entry in the {@link FingerTable}, i.e. the successor {@link ChordNode} of the enclosing {@link ChordNode}.
   */
  ChordNode successor();

  /**
   * Returns the k-th entry (one-based) from the {@link FingerTable}. This method may return empty references, since after joining of a {@link ChordNode},
   * its {@link FingerTable} will be built up gradually by the stabilization mechanism (in the dynamic mode). For the non-dynamic mode, all
   * values for k > 1 will be empty!
   *
   * @param k The index in the {@link FingerTable} that should be accessed (one-based).
   * @return Optional, k-th entry in the {@link FingerTable}.
   * @throws IndexOutOfBoundsException If k is > size()
   */
  Optional<ChordNode> node(int k);
}
