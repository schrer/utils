package at.schrer.utils.structures;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.UnaryOperator;

public class SomeList<T> implements List<T> {

    private Node<T> first = null;
    private Node<T> last = null;
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (size == 0) {
            return false;
        }

        Node<T> node = first;
        do {
            if (node.contains(o)){
                return true;
            }
            node = node.getNext();
        } while (node != null);

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return this.listIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        if (size == 0) {
            return arr;
        }

        int index = 0;
        Node<T> node = first;
        do {
            arr[index] = node.getValue();
            node = node.getNext();
            index++;
        } while (node != null);

        return arr;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a == null) {
            throw new NullPointerException();
        }

        Class<?> arrayType = a.getClass().getComponentType();
        if (a.length < this.size()) {
            a = (T1[]) Array.newInstance(arrayType, this.size());
        }

        for (int i=0; i < this.size(); i++) {
            T value = this.get(i);
            if (value != null) {
                Class<?> valueType = value.getClass();
                if (!arrayType.isAssignableFrom(valueType)){
                    throw new ArrayStoreException("Cannot cast element of type " + valueType
                            + " to target class of array which is " + arrayType);
                }
            }
            a[i] = (T1) value;
        }

        return a;
    }

    @Override
    public boolean add(T t) {
        Node<T> newNode = new Node<>(t);
        if (first == null) {
            first = newNode;
        } else {
            newNode.setPrevious(last);
            last.setNext(newNode);
        }
        last = newNode;
        size++;

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (size == 0) {
            return false;
        }

        if (first.contains(o)) {
            Node<T> second = first.getNext();
            if (second != null) {
                second.setPrevious(null);
            }
            first = second;
            size--;
            return true;
        }


        Node<T> node = first.getNext();
        do {
            if (node.contains(o)) {
                Node<T> prev = node.getPrevious();
                Node<T> next = node.getNext();

                prev.setNext(next);
                next.setPrevious(prev);

                return true;
            }
            node = node.getNext();
        } while (node.hasNext());

        // Last node contains object
        if (node.contains(o)) {
            Node<T> secondToLast = last.getPrevious();
            secondToLast.setNext(null);
            last = secondToLast;
            size--;
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!this.contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }

        for (T element : c) {
            this.add(element);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkIndexBounds(index, true);
        if (c == null || c.isEmpty()) {
            return false;
        }

        if (this.isEmpty() || index == size) {
            // Append at end
            return this.addAll(c);
        }

        if (c.size() == 1) {
            // Single element list
            this.add(index, c.iterator().next());
            return true;
        }

        Iterator<? extends T> elements = c.iterator();
        int sizeIncrease = c.size();

        Node<T> appenderNode;
        if (index == 0) {
            addFirst(elements.next());
            appenderNode = first;
            sizeIncrease--;
        } else {
            appenderNode = getNode(index).getPrevious();
        }
        Node<T> afterElementsNode = appenderNode.getNext();

        Node<T> lastAddedNode;
        do {
            Node<T> newNode = new Node<>(elements.next());
            newNode.setPrevious(appenderNode);
            appenderNode.setNext(newNode);
            lastAddedNode = appenderNode = newNode;
        } while (elements.hasNext());

        lastAddedNode.setNext(afterElementsNode);
        afterElementsNode.setPrevious(lastAddedNode);
        size = size + sizeIncrease;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean listHasChanged = false;
        for (Object element : c) {
            listHasChanged = listHasChanged || this.remove(element);
        }

        return listHasChanged;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.isEmpty()) {
            if (this.isEmpty()) {
                return false;
            } else {
                this.clear();
                return true;
            }
        }

        int initialSize = this.size;
        Node<T> iteratorNode = first;
        while (iteratorNode != null) {
            if (c.contains(iteratorNode.getValue())) {
                iteratorNode = iteratorNode.getNext();
                continue;
            }
            Node<T> forRemoval = iteratorNode;
            iteratorNode = iteratorNode.getNext();
            removeNode(forRemoval);
        }
        return initialSize != this.size;
    }

    @Override
    public void clear() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    @Override
    public T get(int index) {
        return getNode(index).getValue();
    }

    private Node<T> getNode(int index) throws IndexOutOfBoundsException {
        checkIndexBounds(index, false);

        if (index == 0) {
            return first;
        }

        if (index == size-1) {
            return last;
        }

        boolean isInFirstHalf = index <= (size/2);
        Node<T> node = isInFirstHalf ? first : last;
        UnaryOperator<Node<T>> travFunc = isInFirstHalf ? Node::getNext : Node::getPrevious;
        int iterations = isInFirstHalf ? index : (size - 1 - index);

        for(int i=0; i<iterations; i++) {
            node = travFunc.apply(node);
        }

        return node;
    }

    @Override
    public T getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return first.getValue();
    }

    @Override
    public T getLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return last.getValue();
    }

    @Override
    public T set(int index, T element) {
        checkIndexBounds(index, false);
        Node<T> node = getNode(index);
        return node.setValue(element);
    }

    @Override
    public void add(int index, T element) {
        checkIndexBounds(index, true);
        if (index == size) {
            this.add(element);
            return;
        }

        Node<T> nodeAtIndex = getNode(index);
        Node<T> newNode = new Node<>(element);
        Node<T> previous = nodeAtIndex.getPrevious();

        newNode.setNext(nodeAtIndex);
        newNode.setPrevious(previous);
        if (previous != null) {
            previous.setNext(newNode);
        } else {
            this.first = newNode;
        }
        size++;
    }

    @Override
    public T remove(int index) {
        checkIndexBounds(index, false);

        Node<T> target = getNode(index);
        Node<T> previous = target.getPrevious();
        Node<T> next = target.getNext();

        if (previous == null) {
            this.first = next;
        } else {
            previous.setNext(next);
        }

        if (next == null) {
            this.last = previous;
        } else {
            next.setPrevious(previous);
        }

        size--;
        return target.getValue();
    }

    @Override
    public int indexOf(Object o) {
        return findWithFunction(this.first, o, Node::getNext);
    }

    @Override
    public int lastIndexOf(Object o) {
        int steps = findWithFunction(this.last, o, Node::getPrevious);
        if (steps == -1) {
            return -1;
        }
        return this.size - 1 - steps;
    }

    /**
     *
     * @param node the node to start with
     * @param o the object to look for
     * @param travOperator the operator that returns the following node
     * @return the number of times the operator has to be applied for the search object to be found, or -1 if it is not found
     */
    private int findWithFunction(Node<T> node, Object o, UnaryOperator<Node<T>> travOperator) {
        int stepCounter = 0;
        do {
            if (node.contains(o)) {
                return stepCounter;
            }
            node = travOperator.apply(node);
            stepCounter++;
        } while (node != null);
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new SomeListIterator<>(this.first);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkIndexBounds(index, false);
        Node<T> startNode = getNode(index);
        return new SomeListIterator<>(startNode, index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> node = getNode(fromIndex);
        List<T> copiedList = new SomeList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            copiedList.add(node.getValue());
        }
        return copiedList;
    }

    /**
     * Method to check whether an index is out of the lists bounds.
     * The parameter is meant to decide between allowing an index equal to the size or not.
     * Set to true it is meant for add operations, where a new element can be placed at the index equal to the size.
     * Set to false it is meant for read operations, where no element at index==size exists.
     *
     * @param index index to check
     * @param allowEqualsSize allow index to be equal to size
     * @throws IndexOutOfBoundsException if the index is out of permitted bounds
     */
    private void checkIndexBounds(int index, boolean allowEqualsSize) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index is below 0");
        }

        if (!allowEqualsSize && index >= size){
            throw new IndexOutOfBoundsException("Index goes beyond last element");
        }

        if (allowEqualsSize && index > size) {
            throw new IndexOutOfBoundsException("Index is above list size");
        }

    }

    private void removeNode(Node<T> node){
        if (size==1 && node==first) {
            clear();
            return;
        }

        size--;
        if (first == node) {
            Node<T> second = first.getNext();
            if (second != null) {
                second.setPrevious(null);
            }
            node.setNext(null);
            first = second;
            return;
        }

        if (last == node) {
            Node<T> secondToLast = last.getPrevious();
            secondToLast.setNext(null);
            last = secondToLast;
            return;
        }


        Node<T> prev = node.getPrevious();
        Node<T> next = node.getNext();

        node.setNext(null);
        node.setPrevious(null);

        prev.setNext(next);
        next.setPrevious(prev);
    }

    private static class Node<T> {
        T value;
        Node<T> previous;
        Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next = null;
            this.previous = null;
        }

        public Node<T> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<T> previous) {
            this.previous = previous;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getValue() {
            return value;
        }

        public T setValue(T value) {
            T oldVal = this.value;
            this.value = value;
            return oldVal;
        }

        public boolean contains(Object o){
            return Objects.equals(o, this.value);
        }

        public boolean hasNext(){
            return this.next != null;
        }

        public boolean hasPrevious(){
            return this.previous != null;
        }
    }

    private static class SomeListIterator<T> implements ListIterator<T> {
        Node<T> nextNode;
        int nextIndex;


        public SomeListIterator(Node<T> startNode){
            this.nextNode = startNode;
            this.nextIndex = 0;
        }

        public SomeListIterator(Node<T> startNode, int startIndex){
            this.nextNode = startNode;
            this.nextIndex = startIndex;
        }

        @Override
        public boolean hasNext() {
            return nextNode.hasNext();
        }

        @Override
        public T next() {
            T value = nextNode.getValue();
            nextNode = nextNode.getNext();
            nextIndex++;
            return value;
        }

        @Override
        public boolean hasPrevious() {
            return nextNode.hasPrevious();
        }

        @Override
        public T previous() {
            Node<T> previous = nextNode.getPrevious();
            nextNode = previous;
            nextIndex--;
            return previous.getValue();
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {

        }

        @Override
        public void set(T t) {

        }

        @Override
        public void add(T t) {

        }
    }
}
