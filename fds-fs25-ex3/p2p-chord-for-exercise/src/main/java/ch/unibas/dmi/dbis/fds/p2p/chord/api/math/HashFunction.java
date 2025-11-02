package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class HashFunction {
  /** The number if bits to keep for the hash. */
  private final int numberOfBits;

  /** The bit-mask to apply to extract the hash. */
  private final int mask;

  /**
   * Constructor for {@link HashFunction},
   *
   * @param numberOfBits  The number if bits to keep for the hash.
   */
  public HashFunction(int numberOfBits) {
    this.numberOfBits = numberOfBits;
    this.mask = (int) Math.pow(2, numberOfBits) - 1;
  }

  /**
   * Hashes the given input string using SHA-1 as hash algorithm and returns the first {@link HashFunction#numberOfBits} bits as int value.
   *
   * @param value The value to hash
   * @return The first {@link HashFunction#numberOfBits} bits of the hash, represented as integer.
   */
  public final int hash(String value){
    try {
      final MessageDigest ms = MessageDigest.getInstance("SHA");
      final ByteBuffer bytes = ByteBuffer.wrap(ms.digest(value.getBytes()));
      final int extractedLong = Math.abs(bytes.getInt());
      return (extractedLong & this.mask);
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Couldn't find Hash Algorithm *SHA*.");
      System.exit(-1);
      return 0;
    }
  }

  public final int hash(int i){
    return hash(String.valueOf(i));
  }

  public final int hash(long l){
    return hash(String.valueOf(l));
  }

  public final int hash(double d){
    return hash(String.valueOf(d));
  }

  /**
   * Getter for {@link HashFunction#numberOfBits}.
   *
   * @return
   */
  public final int getNumberOfBits() {
    return this.numberOfBits;
  }
}
