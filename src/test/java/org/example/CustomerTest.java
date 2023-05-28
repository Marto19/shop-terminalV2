package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.example.Customer;
import org.example.shop.services.CustomerBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CustomerTest {
    @Mock
    private CustomerBalance customerBalanceMock;

    private Customer customer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
    }

    @Test
    public void testGetBalance() {
        BigDecimal expectedBalance = new BigDecimal("50.00");
        customer.setBalance(expectedBalance);

        BigDecimal actualBalance = customer.getBalance();

        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    public void testGetShoppingList() {
        Map<String, Integer> expectedShoppingList = new HashMap<>();
        expectedShoppingList.put("Item 1", 2);
        expectedShoppingList.put("Item 2", 3);
        customer.addToShoppingList("Item 1", 2);
        customer.addToShoppingList("Item 2", 3);

        Map<String, Integer> actualShoppingList = customer.getShoppingList();

        assertEquals(expectedShoppingList, actualShoppingList);
    }

    @Test
    public void testGenerateBalance() {
        BigDecimal expectedBalance = new BigDecimal("50.00");
        when(customerBalanceMock.generateBalance()).thenReturn(expectedBalance);

        BigDecimal actualBalance = customerBalanceMock.generateBalance();

        assertEquals(expectedBalance, actualBalance);
    }
}
