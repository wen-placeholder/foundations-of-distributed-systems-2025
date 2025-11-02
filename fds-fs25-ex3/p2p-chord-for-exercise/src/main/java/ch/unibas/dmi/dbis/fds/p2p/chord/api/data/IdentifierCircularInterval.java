package ch.unibas.dmi.dbis.fds.p2p.chord.api.data;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.math.CircularInterval;

/**
 * A non-generic {@link CircularInterval} for {@link Identifier}s.
 *
 * @see CircularInterval
 *
 * @author loris.sauter
 */
public class IdentifierCircularInterval extends CircularInterval<Identifier> {


  protected IdentifierCircularInterval(Identifier leftBound,
      Identifier rightBound, boolean leftClosed, boolean rightClosed) {
    super(leftBound, rightBound, leftClosed, rightClosed);
  }

  public static IdentifierCircularInterval createOpen(Identifier leftBound, Identifier rightBound) {
    return new IdentifierCircularInterval(leftBound, rightBound, false, false);
  }

  public static IdentifierCircularInterval createClosed(
      Identifier leftBound, Identifier rightBound) {
    return new IdentifierCircularInterval(leftBound, rightBound, true, true);
  }

  public static IdentifierCircularInterval createLeftOpen(
      Identifier leftBound, Identifier rightBound) {
    return new IdentifierCircularInterval(leftBound, rightBound, false, true);
  }

  public static IdentifierCircularInterval createRightOpen(
      Identifier leftBound, Identifier rightBound) {
    return new IdentifierCircularInterval(leftBound, rightBound, true, false);
  }

  @Override
  public String toString() {
    return ""+(isLeftClosed() ? "[" : "(" )+ getLeftBound().getIndex()+","+getRightBound().getIndex()+ (isRightClosed() ? "]" : ")");
  }

}
