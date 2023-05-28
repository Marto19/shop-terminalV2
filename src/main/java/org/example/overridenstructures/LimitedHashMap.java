package org.example.overridenstructures;
import org.example.shop.exceptions.EmployeesExceedShopLimit;

import java.util.HashMap;

public class LimitedHashMap<K, V> extends HashMap<K, V> {
    private int maxSize;
    public LimitedHashMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {
        if (size() == maxSize){
            try {
                throw new EmployeesExceedShopLimit("The amount of employees exceeds the maximum checkouts");
            } catch (EmployeesExceedShopLimit e) {
                throw new RuntimeException(e);
            }
        }
        return super.put(key, value);
    }
}