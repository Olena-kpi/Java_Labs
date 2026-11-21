package hard;

public interface MySet extends Iterable<Object> {

    boolean add(Object e);

    boolean remove(Object o);

    boolean contains(Object o);

    int size();

    boolean isEmpty();

    void clear();

    Object[] toArray();
}
