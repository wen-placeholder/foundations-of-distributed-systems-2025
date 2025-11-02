package ch.unibas.dmi.dbis.fds.p2p.utilities;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public final class Tuple<A,B> {
  
  private A first;
  private B second;
  
  public Tuple(){
  
  }
  
  public Tuple(A first, B second) {
    this.first = first;
    this.second = second;
  }
  
  public A getFirst() {
    return first;
  }
  
  public void setFirst(A first) {
    this.first = first;
  }
  
  public B getSecond() {
    return second;
  }
  
  public void setSecond(B second) {
    this.second = second;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Tuple<?, ?> tuple = (Tuple<?, ?>) o;
    
    if (getFirst() != null ? !getFirst().equals(tuple.getFirst()) : tuple.getFirst() != null) return false;
    return getSecond() != null ? getSecond().equals(tuple.getSecond()) : tuple.getSecond() == null;
  }
  
  @Override
  public int hashCode() {
    int result = getFirst() != null ? getFirst().hashCode() : 0;
    result = 31 * result + (getSecond() != null ? getSecond().hashCode() : 0);
    return result;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Tuple{");
    sb.append("first=").append(first);
    sb.append(", second=").append(second);
    sb.append('}');
    return sb.toString();
  }
}
