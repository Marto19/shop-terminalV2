package org.example.shop;
import org.example.Customer;
import org.example.shop.Shop;
import org.example.shop.goods.Goods;
import org.example.shop.services.CashierServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CashiersTest {

    private CashierServices cashier;
    private Shop shop;
    private Customer customer;

    @BeforeEach
    public void setup() {
        shop = Mockito.mock(Shop.class);
        customer = Mockito.mock(Customer.class);
        cashier = new Cashiers("John", UUID.randomUUID(), BigDecimal.valueOf(1000));
    }

    @Test
    void markGoodsTest() {
        // Create mock objects for Shop and Customer
        Shop shopMock = Mockito.mock(Shop.class);
        Customer customerMock = Mockito.mock(Customer.class);

        // Set up the mock behavior
        Mockito.when(customerMock.getBalance()).thenReturn(new BigDecimal("100.0"));

        // Create an instance of Cashiers
        Cashiers cashiers = new Cashiers("John", UUID.randomUUID(), new BigDecimal("2000.0"));

        // Call the markGoods() method
        HashMap<String, Integer> shoppingList = cashiers.markGoods(shopMock, customerMock);

        // Assertions
        assertEquals(0, shoppingList.size()); // Verify that the initial shopping list is empty
        // You can add more assertions based on your test scenario

        // Verify the interaction with mocked objects if needed
        Mockito.verify(shopMock, Mockito.times(1)).printStoreGoods();
        // You can add more verification based on your test scenario
    }
}
