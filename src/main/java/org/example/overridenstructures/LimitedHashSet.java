package org.example.overridenstructures;

import java.util.LinkedHashSet;

public class LimitedHashSet<E> extends LinkedHashSet<E> {
    private int capacity;

    public LimitedHashSet(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public boolean add(E element) {
        if (size() >= capacity) {
            return false; // Limit reached, element not added
        }
        return super.add(element);
    }
}
