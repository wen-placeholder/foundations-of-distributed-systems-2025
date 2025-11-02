package ch.unibas.dmi.dbis.fds.p2p.chord.api.data;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.math.HashFunction;

/**
 * Implementation of an identifier circle outlined in the chord paper.
 *
 * The identifier circle is an ordered, circular topology for identifiers.
 *
 * Basically, this is a circle with indexed elements, where
 * each element has a predecessor and successor.
 *
 * This particular implementation's items are {@link Identifier}s,
 * a tuple of index and n-bit hash of the index (e.g. the tuple (hash, index)
 * where hash is the n-bit hash of the index.
 *
 * <p>
 *   <h4>Chord Context</h4>
 *   The {@link IdentifierCircle} is used to retrieve successor and predecessor
 *   identifiers of the nodes.
 * </p>
 *
 * @author loris.sauter
 */
public class IdentifierCircle implements
    ch.unibas.dmi.dbis.fds.p2p.chord.api.IdentifierCircle<Identifier> {

  /** The number of bits. The circle's size is 2 to the power of this number. (Math.pow(2, nbits)) */
  private final int nbits;

  /** The array of identifiers */
  private final Identifier[] identifiers;

  /**
   * Creates a new identifier circle for the given number of bytes.
   * Ultimately, the circle's size is 2^number of bytes
   * @param nbits The number of bytes used for hashing, thus determining the size of the circle
   */
  public IdentifierCircle(int nbits){
    this.nbits = nbits;
    identifiers = new Identifier[size()];
    preComputeIdentifiers();
  }

  /**
   * Precomputes the identifiers.
   * Identifiers are immutable and can be pre-computed therefore
   */
  private void preComputeIdentifiers(){
    final HashFunction f = new HashFunction(nbits);
    for(int i=0; i<size(); i++){
      identifiers[i] = new Identifier(f.hash(i),i);
    }
  }

  /**
   * Returns the identifier at the given index on the circle.
   *
   * Since this is a circular topology, positive numbers lead to clock wise travel through the circle, negative numbers to counter clockwise.
   *
   * The index can be any integer, as the result will always be the identifier at this position, modulo the circle's size.
   * @param i The index of the identifier to retrieve, a negative number for counter clockwise travel direction
   *
   * @return The identifier at the given position.
   */
  @Override
  public Identifier getIdentifierAt(int i) {
    return identifiers[Math.floorMod(i, size())]; // Makes it possible to get the -1st identifier, which is the last one (in array form): e.g. with size=8: -1 + 8 = 7, 7 % 8 = 7
  }

  /**
   * Returns the size of the identifier circle, e.g the amount of items on the circle.
   * @return The size of the identifier circle, e.g the amount of items on the circle.
   */
  @Override
  public int size() {
    return (int)Math.pow(2, nbits);
  }

  /**
   * Returns the next identifier, starting from the given identifier.
   * The next item on the circle in clockwise travel direction, starting from the given identifier.
   * @param id
   * @return
   */
  @Override
  public Identifier next(Identifier id) {
    return identifiers[id.getIndex() + 1 % size()];
  }

  /**
   * Returns the last identifier, startin from the given identifier.
   * The next item on the circle in counter clockwise travel direction, starting from the given identifier.
   * @param id
   * @return
   */
  @Override
  public Identifier last(Identifier id) {
    return identifiers[(id.getIndex() + size() - id.getIndex()) % size()];
  }
}
