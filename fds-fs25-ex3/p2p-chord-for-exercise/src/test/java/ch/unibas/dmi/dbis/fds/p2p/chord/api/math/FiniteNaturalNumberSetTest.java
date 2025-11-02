package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests whether the {@link FiniteNaturalNumberSet} behaves as expected. This is primarily to have a
 * certified implementation of {@link CountableFiniteSet} for testing {@link CircularInterval}.
 *
 * @author loris.sauter
 */
public class FiniteNaturalNumberSetTest {

  private CountableFiniteSet<Integer> countableFiniteSet;

  @BeforeEach
  public void initCountableFiniteSet() {
    countableFiniteSet = new FiniteNaturalNumberSet(8);
    // This  is the following: {0,1,2,3,4,5,6,7} set
  }

  @Test
  public void testFirst() {
    Assertions.assertEquals(0, (int) countableFiniteSet.first());
  }

  @Test
  public void testLast() {
    Assertions.assertEquals((int) Math.pow(2, 3) - 1, (int) countableFiniteSet.last());
  }

  @Test
  public void testNeutrum() {
    Assertions.assertEquals(0, (int) countableFiniteSet.neutrum());
  }

  @Test
  public void testOne() {
    Assertions.assertEquals(1, (int) countableFiniteSet.one());
  }

  @Test
  public void testIncrementNormal() {
    Assertions.assertEquals(3, (int) countableFiniteSet.increment(2));
  }

  @Test
  public void testIncrementLast() {
    try {
      countableFiniteSet.increment(countableFiniteSet.last());
      Assertions.fail("Last element was incremented");
    } catch (IndexOutOfBoundsException ignored) {

    }
  }

  @Test
  public void testDecrementNormal() {
    Assertions.assertEquals(2, (int) countableFiniteSet.decrement(3));
  }

  @Test
  public void testDecrementFirst() {
    try {
      countableFiniteSet.decrement(countableFiniteSet.first());
      Assertions.fail("First element was decremented");
    } catch (IndexOutOfBoundsException ignored) {

    }
  }

  @Test
  public void testIncrementBy() {
    Assertions.assertEquals(5, (int) countableFiniteSet.incrementBy(2, 3));
  }

  @Test
  public void testIncrementByCommutative() {
    Assertions.assertEquals(0,
            countableFiniteSet.incrementBy(2, 3).compareTo(countableFiniteSet.incrementBy(3, 2)));
  }

  @Test
  public void testIncrementByNeutrum() {
    Assertions.assertEquals(1, (int) countableFiniteSet.incrementBy(1, countableFiniteSet.neutrum()));
  }

  @Test
  public void testDecrementBy() {
    Assertions.assertEquals(5, (int) countableFiniteSet.decrementBy(8, 3));
  }

  @Test
  public void testDecrementByNeutrum() {
    Assertions.assertEquals(1, (int) countableFiniteSet.decrementBy(1, countableFiniteSet.neutrum()));
  }

  @Test
  public void testArbitraryElement() {
    Assertions.assertEquals(4, (int) countableFiniteSet.element(4));
  }

  @Test
  public void testArbitraryNegativeIndexElement() {
    try {
      countableFiniteSet.element(-4);
      Assertions.fail("Set's indices must be zerobased, postivie integer");
    } catch (IndexOutOfBoundsException ignored) {

    }
  }

  @Test
  public void testArbitraryTooLargeIndexElement() {
    try {
      countableFiniteSet.element(100);
      Assertions.fail("Set's indices must be zerobased, postivie integer, less than " + (
          countableFiniteSet.size() - 1));
    } catch (IndexOutOfBoundsException ignored) {

    }
  }


}
