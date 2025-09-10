package hansanhha;

import java.util.*;

public class SimpleLinkedList<E> extends AbstractSequentialList<E>
        implements List<E>, Deque<E>, Cloneable {

    private int size;

    private Node<E> first;

    private Node<E> last;

    private static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        public Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;

        if (f == null) {
            last = newNode;
        } else {
            f.prev = newNode;
        }

        size++;
    }

    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(last, e, null);
        last = newNode;

        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }

        size++;
    }

    void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);

        if (pred == null) {
            first = newNode;
        } else {
            pred.next = newNode;
        }

        size++;
    }

    private E unlinkFirst(Node<E> f) {
        final Node<E> next = f.next;
        final E item = f.item;
        f.item = null;
        f.next = null;

        first = next;
        if (next == null) {
            last = null;
        } else {
            next.prev = null;
        }

        size--;
        return item;
    }

    E unlinkLast(Node<E> l) {
        final Node<E> prev = l.prev;
        final E item = l.item;
        l.prev = null;
        l.item = null;

        last = prev;
        if (prev == null) {
            first = null;
        } else {
            prev.next = null;
        }

        size--;
        return item;
    }

    E unlink(Node<E> x) {
        final Node<E> prev = x.prev;
        final Node<E> next = x.next;
        final E item = x.item;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        return item;
    }

    Node<E> node(int index) {

        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

    @Override
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    @Override
    public void add(int index, E e) {
        checkPositionIndex(index);

        if (index == size) {
            linkLast(e);
        } else {
            linkBefore(e, node(index));
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0) {
            return false;
        }

        Node<E> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }

        for (Object o : a) {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);

            if (pred == null) {
                first = newNode;
            } else {
                pred.next = newNode;
            }
            pred = newNode;
        }

        if (succ == null) {
            pred = last;
        } else {
            succ.prev = pred;
            pred.next = succ;
        }

        size += numNew;
        return true;
    }

    @Override
    public void addFirst(E e) {
        linkFirst(e);
    }

    @Override
    public void addLast(E e) {
        linkLast(e);
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        }

        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item.equals(o)) {
                unlink(x);
                return true;
            }
        }

        return false;
    }

    @Override
    public E remove(int index) {
        checkPositionIndex(index);
        return unlink(node(index));
    }


    @Override
    public E removeFirst() {
        Node<E> f = first;

        if (f == null) {
            throw new NoSuchElementException();
        }

        return unlinkFirst(f);
    }

    @Override
    public E removeLast() {
        Node<E> l = last;

        if (l == null) {
            throw new NoSuchElementException();
        }

        return unlinkLast(l);
    }

    @Override
    public void clear() {
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.prev = null;
            x.next = null;

            x = next;
        }

        first = last = null;
        size = 0;
    }

    @Override
    public E get(int index) {
        return node(index).item;
    }

    @Override
    public E getFirst() {
        Node<E> f = first;

        if (f == null) {
            throw new NoSuchElementException();
        }

        return f.item;
    }

    @Override
    public E getLast() {
        Node<E> l = last;

        if (l == null) {
            throw new NoSuchElementException();
        }

        return l.item;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    return index;
                }
                index++;
            }
        }

        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item.equals(o)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    return index;
                }
                index--;
            }
        }

        for (Node<E> x = last; x != null; x = x.prev) {
            if (x.item.equals(o)) {
                return index;
            }
            index--;
        }

        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public E set(int index, E element) {
        checkPositionIndex(index);

        Node<E> node = node(index);
        E oldVal = node.item;
        node.item = element;

        return oldVal;
    }


    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    @Override
    public E pollFirst() {
        Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    @Override
    public E pollLast() {
        Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == o) {
                    unlink(x);
                    return true;
                }
            }
        }

        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item.equals(o)) {
                unlink(x);
                return true;
            }
        }

        return false;
    }

    @Override
    public E peek() {
        Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    @Override
    public E peekFirst() {
        Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    @Override
    public E peekLast() {
        Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public LinkedList<E> reversed() {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return List.of();
    }

    private void checkPositionIndex(int index) {
        if (!(index >= 0 && index <= size))
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
}
