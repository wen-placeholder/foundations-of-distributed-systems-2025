package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;

import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TODO: write JavaDoc
 *
 * Possibilities to test: a < b x contains (a,b) x contains (b,a) x contains [a,b) x contains [b,a)
 * x contains (a,b] x contains (b,a] x contains [a,b] x contains [b,a]
 *
 * x contains (a,a) // impossible x contains [a,a) x contains (a,a] x contains [a,a]
 *
 * and all with notin
 *
 * @author loris.sauter
 */
public class CircularIntervalTest {

  private CountableFiniteSet<Integer> set = new FiniteNaturalNumberSet(3);
  private Random r = new Random();

  private Integer x;
  private CircularInterval<Integer> interval;

// ============== SPECIAL CASES

  @Test
  public void testOpenCircularSingleElementContains(){
    x = 7;
    Integer a = 6, b = 0;
    interval = new CircularInterval<>(a,b,false,false);
    Assertions.assertTrue(interval.contains(7));
    Assertions.assertFalse(interval.contains(a));
    Assertions.assertFalse(interval.contains(b));
  }

  @Test
  public void testLeftFirstOpenContains() {
    x = 2;
    Integer a = 0, b = 4;
    interval = new CircularInterval<>(a, b, false, false);
    Assertions.assertTrue(interval.contains(x));
  }


  @Test
  public void testLeftFirstLeftClosedContains() {
    x = 0;
    Integer a = 0, b = 4;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertTrue(interval.contains(x));
  }


  @Test
  public void testElementFirstLeftClosedCircularContains() {
    x = 0;
    Integer a = 6, b = 4;
    interval = new CircularInterval<>(a, b, true, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testRightLastRightClosedContains() {
    x = 7;
    Integer a = 1, b = 7;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertTrue(interval.contains(x));
  }

// ===== CONTAINS

  @Test
  public void testOpenContains() {
    x = 2;
    Integer a = 1, b = 4;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testOpenCircularContains() {
    x = 4;
    Integer a = 3, b = 1;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testLeftClosedContains() {
    x = 1;
    Integer a = 1, b = 4;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testLeftClosedCircularContains() {
    x = 6;
    Integer a = 6, b = 4;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testRightClosedContains() {
    x = 5;
    Integer a = 3, b = 5;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testRightClosedCircularContains() {
    x = 1;
    Integer a = 3, b = 1;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testClosedContains() {
    x = 4;
    Integer a = 2, b = 5;
    interval = new CircularInterval<>( a, b, true, true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testClosedCircularContains() {
    x = 0;
    Integer a = 5, b = 2;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testSingleElementOpenContains() {
    // a single element, open interval contains only the empty set, thus this test has to assert false
    x = 3;
    Integer a = 3, b = 3;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testSingleElementLeftClosedContains() {
    x = 2;
    Integer a = 2, b = 2;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testSingleElementRightClosedContains() {
    x = 4;
    Integer a = 4, b = 4;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testSingleElementClosedContains() {
    x = 7;
    Integer a = 7, b = 7;
    interval = new CircularInterval<>( a, b, true, true);
    Assertions.assertTrue(interval.contains(x));
  }

// ========= CONTAINS NOT
  @Test
  public void testOpenContainsNot() {
    x = 0;
    Integer a = 1, b = 4;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testOpenCircularContainsNot() {
    x = 2;
    Integer a = 3, b = 1;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testLeftClosedContainsNot() {
    x = 4;
    Integer a = 1, b = 4;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testLeftClosedCircularContainsNot() {
    /*
    [6,4) contains 6,7,0,1,2,3 but not 4,5
    -> ! x in [4,6)
     */
    x = 4;
    Integer a = 6, b = 4;
    interval = new CircularInterval<>( a, b, true, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testRightClosedContainsNot() {
    x = 3;
    Integer a = 3, b = 5;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testRightClosedCircularContainsNot() {
    x = 3;
    Integer a = 3, b = 1;
    interval = new CircularInterval<>( a, b, false, true);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testClosedContainsNot() {
    x = 1;
    Integer a = 2, b = 5;
    interval = new CircularInterval<>( a, b, true, true);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testClosedCircularContainsNot() {
    x = 4;
    Integer a = 5, b = 2;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testFullClosedContains() {
    x = 6;
    Integer a = 7, b = 7;
    interval = new CircularInterval<>( a, b, true, true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testFullOpenContainsNot() {
    x = 7;
    Integer a = 7, b = 7;
    interval = new CircularInterval<>( a, b, false, false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testCircleRightOpenContains(){
    x = 0;
    int a = 0, b=0;
    interval = new CircularInterval<>(a,b,true,false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testCircleLeftOpenContains(){
    x = 3;
    int a=3, b=3;
    interval = new CircularInterval<>(a,b,false,true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testCircleOpenContainsNot2(){
    x = 0;
    int a = 0, b=0;
    interval = new CircularInterval<>(a,b,false,false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testCircleClosedContains(){
    x = 3;
    int a=3, b=3;
    interval = new CircularInterval<>(a,b,true,true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testCircleRightOpenContainsNot(){
    // there is no element that is not in this interval
    x = 2;
    int a = 2, b=2;
    interval = new CircularInterval<>(a,b,true,false);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testCircleLeftOpenContainsNot(){
    // there is no element that is not in this interval
    x = 1;
    int a=1, b=1;
    interval = new CircularInterval<>(a,b,false,true);
    Assertions.assertTrue(interval.contains(x));
  }

  @Test
  public void testCircleOpenContainsNot(){
    x = 0;
    int a = 0, b=0;
    interval = new CircularInterval<>(a,b,false,false);
    Assertions.assertFalse(interval.contains(x));
  }

  @Test
  public void testCircleClosedContainsNot(){
    // There is no element that is not in this interval
    x = 7;
    int a=7, b=7;
    interval = new CircularInterval<>(a,b,true,true);
    Assertions.assertTrue(interval.contains(x));
  }
}
