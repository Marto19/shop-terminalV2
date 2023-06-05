package org.example.shop;

import org.example.shop.checkout.Checkouts;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShopTest {

    private Shop shop;
    private Set<Checkouts> checkoutsSet;
    private Set<Cashiers> cashiersSet;

    @BeforeEach
    void setUp() {
        BigDecimal foodMarkup = BigDecimal.valueOf(10);
        BigDecimal nonFoodMarkup = BigDecimal.valueOf(5);
        int daysUntilExpiryDiscount = 5;
        BigDecimal expiryDiscount = BigDecimal.valueOf(2);
        int numberOfCheckouts = 3;
        shop = new Shop(foodMarkup, nonFoodMarkup, daysUntilExpiryDiscount, expiryDiscount, numberOfCheckouts);

        // Create checkouts and cashiers for the test
        checkoutsSet = new HashSet<>();
        checkoutsSet.add(new Checkouts());
        checkoutsSet.add(new Checkouts());
        checkoutsSet.add(new Checkouts());

        cashiersSet = new HashSet<>();
        cashiersSet.add(new Cashiers("Cashier 1", UUID.randomUUID(), BigDecimal.valueOf(1000)));
        cashiersSet.add(new Cashiers("Cashier 2", UUID.randomUUID(), BigDecimal.valueOf(1500)));
        cashiersSet.add(new Cashiers("Cashier 3", UUID.randomUUID(), BigDecimal.valueOf(2000)));
    }

    @Mock
    private Goods goods;

    @Test
    void testCalculateGoodsSellingPrice() {
        // Initialize the Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Create a sample expiry date and current date
        LocalDate expiryDate = LocalDate.now().plusDays(5); // Expiry date 5 days from now
        LocalDate currentDate = LocalDate.now();

        // Set up the goods object with the necessary properties
        when(goods.getGoodsType()).thenReturn(GoodsType.FOOD);
        when(goods.getUnitShippingCost()).thenReturn(BigDecimal.valueOf(10));
        when(goods.getExpiryDate()).thenReturn(expiryDate);

        // Create an instance of the Shop class
        Shop shop = new Shop(BigDecimal.valueOf(2), BigDecimal.valueOf(3), 7, BigDecimal.valueOf(5), 1);

        // Call the method under test
        BigDecimal sellingPrice = shop.calculateGoodsSellingPrice(goods, shop.getExpiryDiscount());

        // Calculate the expected selling price
        BigDecimal markup = shop.getFoodMarkup();
        BigDecimal unitShippingCost = goods.getUnitShippingCost();
        int daysUntilExpiry = (int) currentDate.until(expiryDate, ChronoUnit.DAYS);
        shop.applyMarkup(unitShippingCost, markup, goods);
        BigDecimal expectedSellingPrice = unitShippingCost.add(markup);

        // Assert the expected selling price and the actual selling price
        assertEquals(expectedSellingPrice, sellingPrice);
    }

    @Test
    void subtractFromPrice() {
        // Set up the inputs
        BigDecimal price = BigDecimal.valueOf(100);
        BigDecimal discount = BigDecimal.valueOf(20);

        // Create an instance of the Shop class
        Shop shop = new Shop(
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20),
                5,
                BigDecimal.valueOf(5),
                3
        );

        // Call the method under test
        BigDecimal result = shop.subtractFromPrice(price, discount);

        // Calculate the expected result
        BigDecimal percent = discount.divide(BigDecimal.valueOf(100));
        BigDecimal expected = price.subtract(price.multiply(percent));

        // Assert the expected result and the actual result
        assertEquals(expected, result);
    }

    @Test
    void addCashierToSet() {
        // Create a Cashiers object
        Cashiers cashier = new Cashiers("John Doe", UUID.randomUUID(), BigDecimal.valueOf(1000));

        // Create a Shop object
        Shop shop = new Shop(
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                7,
                BigDecimal.valueOf(0.05),
                3
        );

        // Add the cashier to the set
        shop.addCashierToSet(cashier);

        // Verify that the cashier is added to the set
        assertTrue(shop.getCashiersSet().contains(cashier));
    }

    @Test
    void addCheckoutToSet() {
        // Create a Checkouts object
        Checkouts checkout = new Checkouts();

        // Create a Shop object
        Shop shop = new Shop(
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                7,
                BigDecimal.valueOf(0.05),
                3
        );

        // Add the checkout to the set
        shop.addCheckoutToSet(checkout);

        // Get the checkoutsSet from the Shop object
        Set<Checkouts> checkoutsSet = shop.getCheckoutsSet();

        // Verify that the checkout is added to the set
        assertTrue(checkoutsSet.contains(checkout));
    }

    @Test
    void addGoodsToSet() {
        // Create a Goods object
        Goods goods = new Goods(
                "Apple",
                BigDecimal.valueOf(1.99),
                GoodsType.FOOD,
                LocalDate.now().plusDays(7),
                10
        );

        // Create a Shop object
        Shop shop = new Shop(
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                7,
                BigDecimal.valueOf(0.05),
                3
        );

        // Add the goods to the set
        shop.addGoodsToSet(goods);

        // Get the storeGoods set from the Shop object
        Set<Goods> storeGoods = shop.getStoreGoods();

        // Verify that the goods is added to the set
        assertTrue(storeGoods.contains(goods));
    }



    @Test
    void assignCashierToCheckoutTest() {
        shop.assignCashierToChekout(checkoutsSet, cashiersSet);

        // Check if the checkouts are assigned to cashiers
        assertEquals(3, shop.getCheckoutsCashiersMap().size());
        for (Checkouts checkout : checkoutsSet) {
            assertTrue(shop.getCheckoutsCashiersMap().containsKey(checkout));
            assertNotNull(shop.getCheckoutsCashiersMap().get(checkout));
            assertTrue(cashiersSet.contains(shop.getCheckoutsCashiersMap().get(checkout)));
        }
    }

    @Test
    void generateCheckouts() {
        // Set up the desired amount of checkouts to generate
        int amount = 3;
        // Call the method under test
        List<Checkouts> generatedCheckouts = shop.generateCheckouts(amount, shop);
        // Verify the expected behavior
        // Check that the correct number of checkouts was generated
        assertEquals(amount, generatedCheckouts.size());
        // Verify that each generated checkout has a non-null UUID
        for (Checkouts generatedCheckout : generatedCheckouts) {
            assertNotNull(generatedCheckout.getUuid());
        }
    }

    @Test
    void findGoodsByName() {
    }

    @Test
    void chooseCheckoutWithCashier() {
    }

    @Test
    void calculateCashierExpenses() {
    }

    @Test
    void calculateIncome() {
    }

    @Test
    void shopInventarExpenses() {
    }

    @Test
    void handleNoSoldItems() {
    }
}