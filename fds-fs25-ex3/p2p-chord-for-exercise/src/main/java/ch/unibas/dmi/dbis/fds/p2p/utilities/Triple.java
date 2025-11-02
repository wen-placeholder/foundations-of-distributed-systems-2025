package ch.unibas.dmi.dbis.fds.p2p.utilities;

import java.util.Objects;

public final class Triple<A,B,C> {
    private A first;
    private B second;
    private C third;

    public Triple(){}

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
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

    public C getThird() {
        return this.third;
    }

    public void setThird(C third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return String.format("Triple{first=%s, second=%s, third=%s}", first, second, third);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) &&
                Objects.equals(second, triple.second) &&
                Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
