package org.example.shop.checkout;

import static org.junit.jupiter.api.Assertions.*;
import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.exceptions.ExpiryDateException;
import org.example.shop.exceptions.InsufficientBalance;
import org.example.shop.exceptions.NameException;
import org.example.shop.exceptions.NotEnoughQuantity;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.example.shop.goods.SoldGoods;
import org.example.shop.services.CheckoutServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import static org.mockito.Mockito.*;

class CheckoutsTest {
    private Checkouts checkouts;
    private Shop shop;

    @BeforeEach
    public void setUp() {
        checkouts = new Checkouts();
        shop = mock(Shop.class);
    }

    @Test
    public void testCheckGoodExpiryDate_WhenDiscountNotApplied() {
        Goods goods = new Goods("Cheese", BigDecimal.valueOf(3.0), GoodsType.FOOD, LocalDate.now().plusDays(5), 5);
        BigDecimal totalValue = BigDecimal.valueOf(15.0);
        goods.setExpiryDate(LocalDate.of(2024, 11, 21));
        when(shop.getDaysUntilExpiryDiscountApplied()).thenReturn(7);

        BigDecimal result = checkouts.checkGoodExpiryDate(goods, shop, totalValue);

        assertEquals(BigDecimal.valueOf(15.0), result);
        verify(shop).getDaysUntilExpiryDiscountApplied();
        verifyNoMoreInteractions(shop);
    }

    @Test
    public void testCheckGoodExpiryDate_WhenExpired() {
        Goods goods = new Goods("Milk", BigDecimal.valueOf(2.0), GoodsType.FOOD, LocalDate.now().minusDays(2), 10);
        BigDecimal totalValue = BigDecimal.valueOf(20.0);

        assertThrows(ExpiryDateException.class, () -> checkouts.checkGoodExpiryDate(goods, shop, totalValue));
    }

    @Test
    public void testCheckCustomersBalance_WhenSufficientBalance() {
        BigDecimal balance = BigDecimal.valueOf(100.0);
        BigDecimal totalSum = BigDecimal.valueOf(50.0);

        assertDoesNotThrow(() -> checkouts.checkCustomersBalance(balance, totalSum));
    }

    @Test
    public void testCheckCustomersBalance_WhenInsufficientBalance() {
        BigDecimal balance = BigDecimal.valueOf(50.0);
        BigDecimal totalSum = BigDecimal.valueOf(100.0);

        assertThrows(InsufficientBalance.class, () -> checkouts.checkCustomersBalance(balance, totalSum));
    }



}