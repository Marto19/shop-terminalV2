package org.example.shop.checkout;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.deSerialization.ReceiptSerializer;
import org.example.shop.exceptions.InsufficientBalance;
import org.example.shop.goods.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.checkout.Checkouts;
import org.example.shop.exceptions.ExpiryDateException;
import org.example.shop.exceptions.InsufficientBalance;
import org.example.shop.exceptions.NameException;
import org.example.shop.exceptions.NotEnoughQuantity;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.example.shop.services.CheckoutServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutsTest {
    private Checkouts checkouts;
    private Shop shop;
    private Cashiers cashiers;
    private BigDecimal balance;
    private HashMap<String, Integer> shoppingList;

    @Mock
    private Shop mockedShop;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        checkouts = new Checkouts();
        shop = new Shop(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.05), 5, BigDecimal.valueOf(0.2), 1);
        cashiers = new Cashiers("John Doe", UUID.randomUUID(), BigDecimal.ZERO);
        balance = BigDecimal.valueOf(100);
        shoppingList = new HashMap<>();
        shoppingList.put("Apple", 3);
        shoppingList.put("Banana", 2);
    }

    //TODO - test the sell methods



    @Test
    void testScanGoods() {
        Shop shop = new Shop(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), 7, BigDecimal.valueOf(0.5), 5);
        Goods apple = new Goods("Apple", BigDecimal.valueOf(1.0), GoodsType.FOOD, LocalDate.now().plusDays(3), 5);
        Goods banana = new Goods("Banana", BigDecimal.valueOf(1.5), GoodsType.FOOD, LocalDate.now().plusDays(5), 5);

        // Set the final price for the goods
        apple.setFinalPrice(BigDecimal.valueOf(1.0));
        banana.setFinalPrice(BigDecimal.valueOf(1.5));

        shop.getStoreGoods().add(apple);
        shop.getStoreGoods().add(banana);

        BigDecimal balance = BigDecimal.valueOf(100);
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Apple", 3);
        shoppingList.put("Banana", 2);

        // Create an instance of the class under test
        Checkouts checkouts = new Checkouts();

        // Call the method under test
        BigDecimal totalSum = checkouts.scanGoods(shop, balance, shoppingList);

        // Verify the results
        BigDecimal expectedSum = BigDecimal.valueOf(6.0); // Calculate the expected total sum manually
        assertEquals(expectedSum, totalSum, "Total sum is incorrect");
    }

    @Test
    void testCheckGoodsAvailability() {
        Shop shop = new Shop(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), 7, BigDecimal.valueOf(0.5), 5);
        Goods apple = new Goods("Apple", BigDecimal.valueOf(1.0), GoodsType.FOOD, LocalDate.now().plusDays(3), 5);
        Goods banana = new Goods("Banana", BigDecimal.valueOf(1.5), GoodsType.FOOD, LocalDate.now().plusDays(5), 5);

        // Set the final price for the goods
        apple.setFinalPrice(BigDecimal.valueOf(1.0));
        banana.setFinalPrice(BigDecimal.valueOf(1.5));

        shop.getStoreGoods().add(apple);
        shop.getStoreGoods().add(banana);

        BigDecimal balance = BigDecimal.valueOf(100);
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Apple", 3);
        shoppingList.put("Banana", 2);

        // Create an instance of the class under test
        Checkouts checkouts = new Checkouts();

        // Call the method under test
        BigDecimal totalSum = checkouts.scanGoods(shop, balance, shoppingList);

        // Verify the results
        BigDecimal expectedSum = BigDecimal.valueOf(6.0); // Calculate the expected total sum manually
        assertEquals(expectedSum, totalSum, "Total sum is incorrect");
    }

    @Test
    void testCheckGoodsAvailability_NameException() {
        // Prepare test data
        String goodsName = "Orange";
        int requestedQuantity = 2;

        // Test the method and expect RuntimeException
        assertThrows(RuntimeException.class, () -> checkouts.checkGoodsAvailability(shop, goodsName, requestedQuantity));
    }


    @Test
    void testCheckGoodsAvailability_NotEnoughQuantity() {
        // Create a mock Shop object
        Shop shop = mock(Shop.class);

        // Create a mock Goods object
        Goods goods = mock(Goods.class);
        when(goods.getName()).thenReturn("Apple");

        // Create a mock Goods object to return from findGoodsByName
        Goods foundGoods = mock(Goods.class);
        when(foundGoods.getQuantity()).thenReturn(5);
        when(shop.findGoodsByName("Apple")).thenReturn(foundGoods);

        // Create an instance of the Checkouts class
        Checkouts checkouts = new Checkouts();

        // Call the method with a requested quantity higher than the available quantity
        int requestedQuantity = 10;

        // Assert that a RuntimeException is thrown, and its cause is NotEnoughQuantity
        assertThrows(RuntimeException.class, () ->
                checkouts.handleNotEnoughtQuantity(requestedQuantity, goods, shop));
    }


    @Test
    public void testCheckGoodExpiryDate_NoExpiredGoods() {
        // Arrange
        Goods nonExpiredGoods = new Goods("Non-Expired Goods", BigDecimal.ZERO, GoodsType.FOOD, LocalDate.now().plusDays(1), 1);
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);
        BigDecimal totalValue = BigDecimal.TEN;
        Checkouts checkouts = new Checkouts();

        // Act
        BigDecimal result = checkouts.checkGoodExpiryDate(nonExpiredGoods, shop, totalValue);

        // Assert
        assertEquals(totalValue, result);
    }

    @Test
    public void testCheckGoodExpiryDate_ExpiredGoods() {
        Goods expiredGoods = new Goods("Milk", BigDecimal.valueOf(2.99), GoodsType.FOOD, LocalDate.of(2023, 5, 31), 1);
        Shop shop = new Shop(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.1), 5, BigDecimal.valueOf(0.1), 1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkouts.checkGoodExpiryDate(expiredGoods, shop, BigDecimal.valueOf(10.0));
        });

        assertEquals("org.example.shop.exceptions.ExpiryDateException: This product has expired.", exception.getMessage());
    }

    @Test
    void testCheckCustomersBalance() {
        BigDecimal balance = BigDecimal.valueOf(50.0);
        BigDecimal totalSum = BigDecimal.valueOf(100.0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkouts.checkCustomersBalance(balance, totalSum);
        });

        assertEquals("org.example.shop.exceptions.InsufficientBalance: Your balance is insufficient", exception.getMessage());
    }

    @Test
    public void testUpdateStoreGoods_InsufficientQuantity() {
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        Goods goods = new Goods("Apple", BigDecimal.valueOf(1.0), GoodsType.FOOD, null, 5);
        shop.getStoreGoods().add(goods);
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Apple", 10);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkouts.updateStoreGoods(shop, shoppingList);
        });

        assertEquals("org.example.shop.exceptions.NotEnoughQuantity: Insufficient quantity of this product. The Apple needs 5 more to meet your satisfaction.", exception.getMessage());
    }

    @Test
    public void testUpdateStoreGoods_EnoughQuantity() {
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        Goods goods = new Goods("Apple", BigDecimal.valueOf(1.0), GoodsType.FOOD, null, 10);
        goods.setFinalPrice(BigDecimal.valueOf(1.0)); // Set the final price
        shop.getStoreGoods().add(goods);
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Apple", 5);

        checkouts.updateStoreGoods(shop, shoppingList);

        assertEquals(5, goods.getQuantity());
        assertEquals(1, shop.getSoldItems().size());
    }

    @Test
    public void testUpdateStoreGoods_GoodsNotFound() {
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Apple", 5);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkouts.updateStoreGoods(shop, shoppingList);
        });

        assertEquals("org.example.shop.exceptions.NameException: Invalid goods name. Please try again.", exception.getMessage());
    }



    @Test
    public void testUpdateStoreGoods_ExpiryDateDiscount() {
        // Create a mock Shop object and other dependencies
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        Checkouts checkouts = new Checkouts();
        Cashiers cashiers = new Cashiers("Cashier Name", UUID.randomUUID(), BigDecimal.ZERO);

        // Create a mock product with an expiry date and a discount
        Goods product = new Goods("Product 1", BigDecimal.valueOf(10), GoodsType.FOOD, LocalDate.now().plusDays(5), 10);
        product.setFinalPrice(BigDecimal.valueOf(8)); // Apply discount to the final price

        // Add the product to the shop's goods inventory
        shop.getStoreGoods().add(product);
        // Create a mock shopping list with the purchased quantity of the product
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put(product.getName(), 2);

        // Call the method being tested
        checkouts.updateStoreGoods(shop, shoppingList);

        // Assert that the quantity of the product is updated in the storeGoods
        assertEquals(8, product.getQuantity()); // Initial quantity (10) - Purchased quantity (2)

        // Assert that the product is added to the soldItems set with the correct quantity
        SoldGoods soldGoods = checkouts.findSoldGoodsByName(shop.getSoldItems(), product.getName());
        assertNotNull(soldGoods);
        assertEquals(2, soldGoods.getQuantity());

        // Assert that the discount is applied correctly
        BigDecimal expectedFinalPrice = BigDecimal.valueOf(8); // Final price with discount applied
        assertEquals(expectedFinalPrice, soldGoods.getFinalPrice());
    }

    @Test
    public void testUpdateStoreGoods_GoodsWithNoExpiryDate() {
        // Create a mock Shop object and other dependencies
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        Checkouts checkouts = new Checkouts();
        Cashiers cashiers = new Cashiers("Cashier Name", UUID.randomUUID(), BigDecimal.ZERO);

        // Create a mock product without an expiry date
        Goods product = new Goods("Product 2", BigDecimal.valueOf(15), GoodsType.NONFOOD, null, 5);

        // Set the final price for the goods
        product.setFinalPrice(BigDecimal.valueOf(15));

        // Add the product to the shop's goods inventory
        shop.getStoreGoods().add(product);

        // Create a mock shopping list with the purchased quantity of the product
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put(product.getName(), 3);

        // Call the method being tested
        checkouts.updateStoreGoods(shop, shoppingList);

        // Assert that the quantity of the product is updated in the storeGoods
        assertEquals(2, product.getQuantity()); // Initial quantity (5) - Purchased quantity (3)

        // Assert that the product is added to the soldItems set with the correct quantity
        SoldGoods soldGoods = checkouts.findSoldGoodsByName(shop.getSoldItems(), product.getName());
        assertNotNull(soldGoods);
        assertEquals(3, soldGoods.getQuantity());

        // Assert that the final price is the same as the unit shipping cost (no discount applied)
        assertEquals(product.getUnitShippingCost(), soldGoods.getFinalPrice());

    }
    //        shop.getStoreGoods().add(product);

    @Test
    void testCreateReceipt() {
        // Create a mock Shop object and other dependencies
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 1);
        Checkouts checkouts = new Checkouts();
        Cashiers cashiers = new Cashiers("Cashier Name", UUID.randomUUID(), BigDecimal.ZERO);
        BigDecimal totalValue = BigDecimal.valueOf(100);

        // Create a mock shopping list
        HashMap<String, Integer> shoppingList = new HashMap<>();
        shoppingList.put("Item 1", 2);
        shoppingList.put("Item 2", 3);

        // Get the initial number of receipts
        int initialNumberOfReceipts = shop.getNumberOfReceipt();

        // Call the method being tested
        Receipt createdReceipt = checkouts.createReceipt(shoppingList, shop, checkouts, cashiers, totalValue);

        // Assert that a new receipt is created
        assertNotNull(createdReceipt);

        // Assert that the receipt is added to the shop's receipt set
        assertTrue(shop.getReceiptSet().contains(createdReceipt));

        // Assert that the number of receipts is incremented
        assertEquals(initialNumberOfReceipts + 1, shop.getNumberOfReceipt());
    }
}


