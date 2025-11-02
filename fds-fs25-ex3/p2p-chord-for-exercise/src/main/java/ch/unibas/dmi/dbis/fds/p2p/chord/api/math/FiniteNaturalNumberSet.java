package ch.unibas.dmi.dbis.fds.p2p.chord.api.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Zero based finite set of natural numbers.
 * The set is built for any size, however as it is implemented using an integer array,
 * it is limited to the bounds of {@link Integer}.
 *
 * @author loris.sauter
 */
public final class FiniteNaturalNumberSet implements CountableFiniteSet<Integer> {

  private final int[] elements;

  /**
   * Creates an FiniteNaturalNumberSet of the size 2^size
   * @param size
   */
  public FiniteNaturalNumberSet(int size) {
    elements = new int[size];
    initElements();
  }

  private void initElements() {
    for(int i=0; i<elements.length;i++){
      elements[i] = i;
    }
  }


  @Override
  public Integer first() {
    return elements[0];
  }

  @Override
  public Integer next(Integer of) {
    if(of >= last()){
      throw new IndexOutOfBoundsException(""+of+" is no element of ["+first()+","+last()+"] and "+of+" is no element");
    }
    return elements[of+1];
  }


  @Override
  public Integer previous(Integer of) {
    if(of <= last()){
      throw new IndexOutOfBoundsException(""+of+" is no element of ["+first()+","+last()+"] and "+of+" is no element");
    }
    return elements[of-1];
  }

  @Override
  public Integer element(int i) throws IndexOutOfBoundsException {
    if(i < 0 || i>= elements.length){
      throw new IndexOutOfBoundsException("The given index is not contains the set's indices: "+i+" not contains ["+0+","+elements.length+"].");
    }
    return elements[i];
  }


  @Override
  public Integer incrementBy(Integer integer, Integer amount) {
    return integer + amount;
  }

  @Override
  public Integer decrementBy(Integer integer, Integer amount) {
    return integer - amount;
  }

  @Override
  public Integer last() {
    return elements[elements.length-1];
  }

  @Override
  public Integer neutrum() {
    return 0;
  }

  @Override
  public Integer one() {
    return 1;
  }

  @Override
  public List<Integer> getElementsAsList() {
    ArrayList<Integer> elems = new ArrayList<>();
    for(int i=0; i<elements.length; i++){
      elems.add(elements[i]);
    }
    return elems;
  }

  @Override
  public Integer[] elements() {
    Integer[] out = new Integer[elements.length];
    for(int i=0; i<elements.length; i++){
      out[i] = elements[i];
    }
    return out;
  }

  public int[] getElementsAsArray(){
    return Arrays.copyOf(elements, elements.length);
  }

  @Override
  public int size() {
    return elements.length;
  }
}
