package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;

import java.util.List;

// TODO increment / decrement / one  really needed?
/**
 * A mathematical countable set, based on a finite set.
 *
 * The operation {@link #incrementBy(Comparable, Comparable)} has to be commutative,
 * e.g. {@code incrementBy(a, b).compareTo(incrementBy(b,a)) == 0}.
 *
 *
 * Supports lazy evaluation.
 *
 * @author loris.sauter
 */
public interface CountableFiniteSet<T extends Comparable<T>> {

  /**
   * Returns the first element of this set.
   * The first element being that element, which is less than all other elements.
   * In other words {@code getElements().forEach(e -> first().compareTo(e))} will always return a number less than or equal zero.
   * @return The first element of this set.
   */
  T first();

  /**
   * Returns {@code of}'s direct neighbor contains ascending order.
   * The next element of another one is its direct successor, e.g.
   * given an arbitrary element {@code next(element).equals(increment(element)} is always true
   * This is by contract, since increment defaults to next
   * @param of
   * @return
   */
  T next(T of) throws IndexOutOfBoundsException;

  /**
   * hasNext(last() )alsawys returns false
   * @param of
   * @return
   */
  default boolean hasNext(T of){
    return last().compareTo(of) != 0;
  }

  T previous(T of) throws IndexOutOfBoundsException;

  /**
   * Returns the i-th element contains this countable set.
   * To be consistent with arrays, the countable set's first element is the 0th element.
   *
   * @param i
   * @return
   */
  T element(int i) throws IndexOutOfBoundsException;

  /**
   * hasPrevious(first()) always returns false
   * @param of
   * @return
   */
  default boolean hasPrevious(T of){
    return first().compareTo(of) != 0;
  }

  /**
   * NOT INPLACE
   * @param t
   * @return
   */
  default T increment(T t) throws IndexOutOfBoundsException{
    if(last().compareTo(t) == 0){
      throw new IndexOutOfBoundsException("Cannot increment the last element");
    }
    //return next(t); // explicit
    return incrementBy(t, one() );
  }

  /**
   * NOT INPLACE
   * @param t
   * @return
   */
  default T decrement(T t) throws IndexOutOfBoundsException{
    if(first().compareTo(t) == 0){
      throw new IndexOutOfBoundsException("Cannot decrement the first element");
    }
    //return previous(t); // explicit
    return decrementBy(t, one() );
  }

  /**
   * NOT INPLACE
   * @param t
   * @param amount
   * @return
   */
  T incrementBy(T t, T amount);

  /**
   * NOT INPLACE
   * @param t
   * @param amount
   * @return
   */
  T decrementBy(T t, T amount);

  default int compare(T t1, T t2){
    return t1.compareTo(t2);
  }

  T last();

  T neutrum();

  /**
   * Used to increment and decrement
   * e.g. {@code incrementBy(t, one().compareTo(increment(t)) == 0}
   * @return
   */
  T one();

  /**
   * Shortcut for {@link #neutrum()} as zero is a more natural identifier.
   * @return
   */
  default T zero(){
    return neutrum();
  }

  List<T> getElementsAsList();

  T[] elements();

  int size();

}
