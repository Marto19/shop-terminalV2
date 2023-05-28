package org.example;

import org.example.shop.services.CustomerBalance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Customer implements CustomerBalance {
    private BigDecimal balance;
    private Map<String, Integer> shoppingList;

    public Customer() {
        this.balance = generateBalance();
        this.shoppingList = new HashMap<>();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Map<String, Integer> getShoppingList() {
        return shoppingList;
    }

    public void addToShoppingList(String item, int quantity) {
        shoppingList.put(item, quantity);
    }

    @Override
    public BigDecimal generateBalance() {
        Random random = new Random();
        BigDecimal balance = new BigDecimal(random.nextInt(101)); // Generate a random balance between 0 and 100
        return balance;
    }
}
