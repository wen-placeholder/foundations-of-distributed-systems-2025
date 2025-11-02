package ch.unibas.dmi.dbis.fds.p2p.chord.api;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;

/**
 * Abstraction of an identifier circle as defined in [1].
 *
 * [1] Ion Stoica, Robert Morris, David Karger, M. Frans Kaashoek, and Hari Balakrishnan. 2001.
 *    Chord: A scalable peer-to-peer lookup service for internet applications.
 *    In Proceedings of the 2001 conference on Applications, technologies, architectures, and protocols for computer communications (SIGCOMM '01). ACM, New York, NY, USA, 149-160
 *
 * @author Loris Sauter
 */
public interface IdentifierCircle<T> {

  T getIdentifierAt(int i);
  int size();
  T next(Identifier id);
  T last(Identifier id);

}
