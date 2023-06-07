package org.example.shop;

import org.example.shop.checkout.Checkouts;
import org.example.shop.exceptions.NoSoldItems;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.example.shop.goods.SoldGoods;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        MockitoAnnotations.openMocks(this); // Initialize the mock object

        // Set up the mock behavior for the goods object
        when(goods.getName()).thenReturn("Apple");

        shop.setCheckoutsSet(checkoutsSet);
        shop.setCashiersSet(cashiersSet);
        shop.setCashiersSet(cashiersSet);
        shop.setCashiersSet(cashiersSet);

        Shop shop1 = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);
        cashiersSet = new HashSet<>();
        shop1.setCashiersSet(cashiersSet);
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
        // Create a new set of checkouts
        Set<Checkouts> checkoutsSet = new HashSet<>();
        checkoutsSet.add(new Checkouts());
        checkoutsSet.add(new Checkouts());
        checkoutsSet.add(new Checkouts());

        // Create a new set of cashiers
        Set<Cashiers> cashiersSet = new HashSet<>();
        cashiersSet.add(new Cashiers("Cashier 4", UUID.randomUUID(), BigDecimal.valueOf(3000)));
        cashiersSet.add(new Cashiers("Cashier 5", UUID.randomUUID(), BigDecimal.valueOf(4000)));
        cashiersSet.add(new Cashiers("Cashier 6", UUID.randomUUID(), BigDecimal.valueOf(5000)));

        // Call the method under test
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
        // Add the mocked goods to the store
        shop.addGoodsToSet(goods);
        // Set up the desired goods name to find
        String goodsName = "Apple";
        // Call the method under test
        Goods foundGoods = shop.findGoodsByName(goodsName);
        // Check that the foundGoods is not null
        assertNotNull(foundGoods);
        // Check that the foundGoods has the expected name (using the mocked goods object)
        assertEquals(goodsName, foundGoods.getName());
    }

    @Test
    void chooseCheckoutWithCashier() {
        // Capture the console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Call the method under test
        Map.Entry<Checkouts, Cashiers> result = shop.chooseCheckoutWithCashier();

        // Verify that null is returned when there are no checkouts with assigned cashiers
        assertNull(result);

        // Verify that the appropriate message is printed
        String consoleOutput = outputStream.toString();
        assertTrue(consoleOutput.contains("No checkouts with assigned cashiers."));
    }


    @Test
    public void chooseCheckoutWithCashierE() {
        // Create mock objects
        Checkouts checkout1 = mock(Checkouts.class);
        Checkouts checkout2 = mock(Checkouts.class);
        Cashiers cashier1 = mock(Cashiers.class);
        Cashiers cashier2 = mock(Cashiers.class);

        // Create a Shop instance
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 2);

        // Populate the checkoutsCashiersMap
        shop.getCheckoutsCashiersMap().put(checkout1, cashier1);
        shop.getCheckoutsCashiersMap().put(checkout2, cashier2);

        // Mock getUserChoice method to return the first entry in the map
        Map.Entry<Checkouts, Cashiers> expectedEntry = Map.entry(checkout1, cashier1);
        Shop spyShop = spy(shop);
        doReturn(expectedEntry).when(spyShop).getUserChoice(anyList());

        // Invoke the method under test
        Map.Entry<Checkouts, Cashiers> result = spyShop.chooseCheckoutWithCashier();

        // Verify the expected interactions
        verify(spyShop).getUserChoice(anyList());

        // Verify the expected result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedEntry, result);
    }


    @Test
    void calculateCashierExpenses() {
        // Create mock Cashiers objects
        Cashiers cashier1 = mock(Cashiers.class);
        Cashiers cashier2 = mock(Cashiers.class);
        Cashiers cashier3 = mock(Cashiers.class);

        // Set up the monthly salaries for the mock cashiers
        BigDecimal salary1 = BigDecimal.valueOf(1000);
        BigDecimal salary2 = BigDecimal.valueOf(1500);
        BigDecimal salary3 = BigDecimal.valueOf(2000);
        when(cashier1.getMonthlySalary()).thenReturn(salary1);
        when(cashier2.getMonthlySalary()).thenReturn(salary2);
        when(cashier3.getMonthlySalary()).thenReturn(salary3);

        // Create a new cashiers set and add the mock cashiers
        Set<Cashiers> newCashiersSet = new HashSet<>();
        newCashiersSet.add(cashier1);
        newCashiersSet.add(cashier2);
        newCashiersSet.add(cashier3);

        // Assign the new cashiers set to the shop
        shop.setCashiersSet(newCashiersSet);

        // Calculate the expected total salary
        BigDecimal expectedTotalSalary = salary1.add(salary2).add(salary3);

        // Call the method under test
        BigDecimal totalSalary = shop.calculateCashierExpenses();

        // Assert the result
        assertEquals(expectedTotalSalary, totalSalary);
    }

    @Test
    void calculateIncome() {
        // Create a list of sold goods
        Set<SoldGoods> soldItems = new HashSet<>();
        soldItems.add(new SoldGoods("Apple", BigDecimal.valueOf(2.99), GoodsType.FOOD, LocalDate.now(), 5, BigDecimal.valueOf(14.95)));
        soldItems.add(new SoldGoods("Banana", BigDecimal.valueOf(1.49), GoodsType.FOOD, LocalDate.now(), 3, BigDecimal.valueOf(4.47)));
        soldItems.add(new SoldGoods("Orange", BigDecimal.valueOf(0.99), GoodsType.FOOD, LocalDate.now(), 10, BigDecimal.valueOf(9.90)));

        // Set the sold items in the shop
        shop.setSoldItems(soldItems);

        // Call the method under test
        BigDecimal totalIncome = shop.calculateIncome();

        // Calculate the expected total income
        BigDecimal expectedIncome = BigDecimal.ZERO;
        for (SoldGoods soldGoods : soldItems) {
            BigDecimal itemIncome = soldGoods.getFinalPrice().multiply(BigDecimal.valueOf(soldGoods.getQuantity()));
            expectedIncome = expectedIncome.add(itemIncome);
        }
        // Assert the expected total income and the actual total income
        assertEquals(expectedIncome, totalIncome);
    }

    @Test
    void shopInventarExpenses() {
        // Create a list of store goods
        Set<Goods> storeGoods = new HashSet<>();
        storeGoods.add(new Goods("Apple", BigDecimal.valueOf(2.99), GoodsType.FOOD, LocalDate.now(), 10));
        storeGoods.add(new Goods("Banana", BigDecimal.valueOf(1.49), GoodsType.FOOD, LocalDate.now(), 5));
        storeGoods.add(new Goods("Orange", BigDecimal.valueOf(0.99), GoodsType.FOOD, LocalDate.now(), 8));

        // Set the store goods in the shop
        shop.setStoreGoods(storeGoods);

        // Call the method under test
        BigDecimal totalExpenses = shop.shopInventarExpenses();

        // Calculate the expected total expenses
        BigDecimal expectedExpenses = BigDecimal.ZERO;
        for (Goods goods : storeGoods) {
            BigDecimal finalPrice = goods.getFinalPrice();
            if (finalPrice != null) {
                BigDecimal itemExpenses = finalPrice.multiply(BigDecimal.valueOf(goods.getQuantity()));
                expectedExpenses = expectedExpenses.add(itemExpenses);
            }
        }

        // Assert the expected total expenses and the actual total expenses
        assertEquals(expectedExpenses, totalExpenses);
    }

    @Test
    void handleNoSoldItemsInCalculateIncome() {
        // Create a new shop instance
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);

        // Assert that the initial soldItems list is empty
        assertTrue(shop.getSoldItems().isEmpty());

        // Call calculateIncome() and assert that it throws an exception
        assertThrows(RuntimeException.class, () -> {
            BigDecimal income = shop.calculateIncome();
        });
    }

    @Test
    public void testShopInventarExpenses() {
        // Create a set of goods
        Set<Goods> goodsSet = new HashSet<>();

        Goods item1 = new Goods("Item 1", BigDecimal.valueOf(5), GoodsType.FOOD, LocalDate.now(), 1);
        item1.setFinalPrice(BigDecimal.valueOf(5)); // Set the final price
        goodsSet.add(item1);

        Goods item2 = new Goods("Item 2", BigDecimal.valueOf(8), GoodsType.NONFOOD, null, 1);
        item2.setFinalPrice(BigDecimal.valueOf(8)); // Set the final price
        goodsSet.add(item2);

        Goods item3 = new Goods("Item 3", BigDecimal.valueOf(12), GoodsType.FOOD, LocalDate.now().plusDays(7), 1);
        item3.setFinalPrice(BigDecimal.valueOf(12)); // Set the final price
        goodsSet.add(item3);

        // Create a shop
        Shop shop = new Shop(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), 5, BigDecimal.valueOf(0.5), 1);
        shop.setStoreGoods(goodsSet);

        // Calculate and assert the inventory expenses
        BigDecimal inventarExpenses = shop.shopInventarExpenses();
        Assertions.assertEquals(BigDecimal.valueOf(25), inventarExpenses);
    }

    @Test
    public void testApplyMarkup() {
        BigDecimal foodMarkup = BigDecimal.valueOf(10);
        BigDecimal nonFoodMarkup = BigDecimal.valueOf(5);
        int daysUntilExpiryDiscount = 7;
        BigDecimal expiryDiscount = BigDecimal.valueOf(20);
        int numberOfCheckouts = 3;

        Shop shop = new Shop(foodMarkup, nonFoodMarkup, daysUntilExpiryDiscount, expiryDiscount, numberOfCheckouts);

        BigDecimal cost = BigDecimal.valueOf(100);
        BigDecimal markup = shop.getFoodMarkup();

        Goods goods = new Goods("Product", BigDecimal.TEN, GoodsType.FOOD, LocalDate.now().plusDays(7), 5);
        shop.applyMarkup(cost, markup, goods);

        BigDecimal expectedFinalPrice = BigDecimal.valueOf(110.0);
        BigDecimal actualFinalPrice = goods.getFinalPrice();

        Assertions.assertEquals(expectedFinalPrice, actualFinalPrice);
    }

    @Test
    public void testGetUserChoice() {
        // Create a Shop object
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);

        // Create sample data for checkoutsWithCashiers
        List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers = List.of(
                new AbstractMap.SimpleEntry<>(new Checkouts(), new Cashiers("Cashier 1", UUID.randomUUID(), BigDecimal.ZERO)),
                new AbstractMap.SimpleEntry<>(new Checkouts(), new Cashiers("Cashier 2", UUID.randomUUID(), BigDecimal.ZERO))
        );

        // Set up the input stream with test input
        String testInput = "1\n"; // Simulate user input "1"
        InputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(inputStream);

        // Call the getUserChoice method
        Map.Entry<Checkouts, Cashiers> userChoice = shop.getUserChoice(checkoutsWithCashiers);

        // Assert the expected result
        Assertions.assertNotNull(userChoice);
        Assertions.assertEquals(checkoutsWithCashiers.get(0), userChoice);

        // Clean up the input stream
        System.setIn(System.in);
    }

    @Test
    public void testGetUserChoice_InvalidChoice() {
        // Create a Shop object
        Shop shop = new Shop(BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);

        // Create sample data for checkoutsWithCashiers
        List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers = List.of(
                new AbstractMap.SimpleEntry<>(new Checkouts(), new Cashiers("Cashier 1", UUID.randomUUID(), BigDecimal.ZERO)),
                new AbstractMap.SimpleEntry<>(new Checkouts(), new Cashiers("Cashier 2", UUID.randomUUID(), BigDecimal.ZERO))
        );

        // Set up the input stream with test input
        String testInput = "3\n"; // Simulate user input "3" (invalid choice)
        InputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        System.setIn(inputStream);

        // Call the getUserChoice method
        Map.Entry<Checkouts, Cashiers> userChoice = shop.getUserChoice(checkoutsWithCashiers);

        // Assert the expected result
        Assertions.assertNull(userChoice);

        // Clean up the input stream
        System.setIn(System.in);
    }

}