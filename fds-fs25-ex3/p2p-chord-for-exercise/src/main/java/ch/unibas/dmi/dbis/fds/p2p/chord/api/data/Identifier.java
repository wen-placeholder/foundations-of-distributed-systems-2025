package ch.unibas.dmi.dbis.fds.p2p.chord.api.data;

/**
 * A hash-index tuple.
 *
 * The identifier consists of a hash and its index on the {@link IdentifierCircle}.
 *
 * This particular implementation is immutable
 *
 * <p>
 *   <h4>Chord Context</h4>
 *   This is the class used to make it easy to switch the node's numbers to
 *   actual IPs, if desired.
 *   In particular, everywhere in the chord paper, where the <i>identifier</i> is mentioned,
 *   an object of this class is used in the java code
 * </p>
 *
 * @author loris.sauter
 */
public class Identifier implements Comparable<Identifier> {

  private final int hash;
  private final int index;

  /**
   * Creates a new identifier based on the given arguments
   * @param hash The hash. In this implementation this must be the hash of the index.
   * @param index The index, must be a valid index of the {@link IdentifierCircle}
   */
  Identifier(int hash, int index) {
    this.hash = hash;
    this.index = index;
  }

  /**
   * Returns the hash, e.g. the actual <i>identifier</i>
   * @return The hash
   */
  public long getHash() {
    return hash;
  }

  /**
   * Returns the index of this identifier on the {@link IdentifierCircle}
   * @return The index of this identifier on the {@link IdentifierCircle}
   */
  public int getIndex() {
    return index;
  }

  /**
   * Comparison of identifiers.
   * Two identifiers are compared by their indices.
   * Complies with the {@link Comparable} API.
   * @param o The other identifier to compare this with
   * @return A negative number if this identifier is considered less than the other identifier, 0 if they are considered equal and a positive number, if this one is considered greater than the other one.
   * @see Integer#compare(int, int)
   */
  @Override
  public int compareTo(Identifier o) {
    return Integer.compare(index,o.index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Identifier that = (Identifier) o;

    if (getHash() != that.getHash()) {
      return false;
    }
    return getIndex() == that.getIndex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = (int) (getHash() ^ (getHash() >>> 32));
    result = 31 * result + getIndex();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Identifier{");
    sb.append("hash=").append(hash);
    sb.append(", index=").append(index);
    sb.append('}');
    return sb.toString();
  }
}
