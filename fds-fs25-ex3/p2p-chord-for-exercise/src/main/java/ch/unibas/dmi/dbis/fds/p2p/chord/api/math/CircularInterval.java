package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;
//TODO Check documentation

/**
 * Represents a mathematical interval on the identifier countableFiniteSet.
 *
 * Circular means that if the upper bound is less than the lower bound, the interval continues at
 * the beginning again. Given the set
 *
 * Immutable.
 *
 * @author loris.sauter
 */
public class CircularInterval<T extends Comparable<T>> {


  private final T leftBound;
  private final T rightBound;

  private final boolean leftClosed;
  private final boolean rightClosed;

  protected CircularInterval(T leftBound, T rightBound, boolean leftClosed, boolean rightClosed) {
    this.leftBound = leftBound;
    this.rightBound = rightBound;
    this.leftClosed = leftClosed;
    this.rightClosed = rightClosed;
  }

  public static <T extends Comparable<T>> CircularInterval<T> createOpen(T leftBound, T rightBound) {
    return new CircularInterval<>(leftBound, rightBound, false, false);
  }

  public static <T extends Comparable<T>> CircularInterval<T> createClosed(
      T leftBound, T rightBound) {
    return new CircularInterval<>(leftBound, rightBound, true, true);
  }

  public static <T extends Comparable<T>> CircularInterval<T> createLeftOpen(
      T leftBound, T rightBound) {
    return new CircularInterval<>(leftBound, rightBound, false, true);
  }

  public static <T extends Comparable<T>> CircularInterval<T> createRightOpen(T leftBound, T rightBound) {
    return new CircularInterval<>(leftBound, rightBound, true, false);
  }

  public T getLeftBound() {
    return leftBound;
  }

  public T getRightBound() {
    return rightBound;
  }

  public boolean isLeftClosed() {
    return leftClosed;
  }

  public boolean isRightClosed() {
    return rightClosed;
  }

  public boolean contains(T t) {
    /*
    In the following, a circle size of 8 is assumed.

    # Closed

    [1,4] contains elements 1,2,3,4 but not 5,6,7,0

    -> x <= a && x <= b

    [3,3] contains element 3 but not 0,1,2,4,5,6,7

    -> x == a && x == b

    [6,3] contains elements 6,7,0,1,2,3 but not 4,5

    -> ! x in (3,6)

    [7,1] contains elements 7,0,1 but not 2,3,4,5,6

    -> ! x in (1,7)

    [1,1] contains all elements 1,2,3,4,5,6,7,0 (cause it's a circular interval)

    # Open

    (1,5) contains elements 2,3,4 but not 5,6,7,0,1

    -> 1 < x && x < 5

    (1,1) contains ALL elements but 1, hence 2,3,4,5,6,7,0 (cause it's circular)

    -> none

    (6,1) contains elements 7,0 but not 1,2,3,4,5,6

    -> ! x in [1,6]

    # Left Closed

    [1,4) contains elements 1,2,3 but not 4,5,6,7
    [5,2) contains elements 5,6,7,0,1 but not 2,3,4
    [4,4) contains element 4, but not 0,1,2,3,5,6,7

    # Right closed

    (1,4] contains elements 2,3,4 but not 0,1,5,6,7
    (5,2] contains elements 6,7,0,1,2 but not 3,4,5
    (4,4] contains elements 4, but not 0,1,2,3,5,6,7
    */

    if(leftBound.compareTo(rightBound) == 0) {
      // x in a,b -> a==b
      if(t.compareTo(leftBound) == 0){
        // x in a,b --> x == a == b
          return leftClosed || rightClosed;
      }else{
        // x in a,b --> x != a == b
        return true; /* Important: [a,a] contains all elements between a and a when following the circle clockwise. */
      }
    }else if(leftBound.compareTo(rightBound) > 0){
      // Circular
      boolean temp = inverse().contains(t);
      return !temp; // will jump to 'normal' interval comparison
    }else{
      // Normal
      if(leftClosed && rightClosed){
        return containsClosed(t);
      }else if(leftClosed){
        return containsLeftClosed(t);
      }else if(rightClosed){
        return containsRightClosed(t);
      }else { // open
        return containsOpen(t);
      }
    }
  }

  /**
   * Test a < t < b (with [a,b])
   *
   * @param t
   * @return
   */
  private boolean containsOpen(T t){
    return leftBound.compareTo(t) < 0 && t.compareTo(rightBound) < 0;
  }

  /**
   * Test a <= t < b (with [a,b])
   *
   * @param t
   * @return
   */
  private boolean containsLeftClosed(T t){
    return leftBound.compareTo(t) <= 0 && t.compareTo(rightBound) < 0;
  }

  /**
   * Test a < t <= b (with [a,b])
   *
   * @param t
   * @return
   */
  private boolean containsRightClosed(T t){
    return leftBound.compareTo(t) < 0 && t.compareTo(rightBound) <= 0;
  }

  /**
   * Test a <= t <= b (with [a,b])
   *
   * @param t
   * @return
   */
  private boolean containsClosed(T t){
    return leftBound.compareTo(t) <= 0 && t.compareTo(rightBound) <= 0;
  }


  @Override
  public String toString() {
    return (leftClosed ? "[" : "(" )+ leftBound + "," + rightBound + (rightClosed ? "]" : ")");
  }

  public CircularInterval<T> inverse(){
    if(rightClosed == leftClosed){
      return new CircularInterval<>(rightBound, leftBound, !leftClosed, !rightClosed);
    }else{
      return new CircularInterval<>(rightBound, leftBound, leftClosed, rightClosed);
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CircularInterval<?> that = (CircularInterval<?>) o;

    if (isLeftClosed() != that.isLeftClosed()) {
      return false;
    }
    if (isRightClosed() != that.isRightClosed()) {
      return false;
    }
    if (getLeftBound() != null ? !getLeftBound().equals(that.getLeftBound())
        : that.getLeftBound() != null) {
      return false;
    }
    return getRightBound() != null ? getRightBound().equals(that.getRightBound())
        : that.getRightBound() == null;
  }

  @Override
  public int hashCode() {
    int result = getLeftBound() != null ? getLeftBound().hashCode() : 0;
    result = 31 * result + (getRightBound() != null ? getRightBound().hashCode() : 0);
    result = 31 * result + (isLeftClosed() ? 1 : 0);
    result = 31 * result + (isRightClosed() ? 1 : 0);
    return result;
  }
}
