package org.example.shop.services;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.checkout.Checkouts;
import org.example.shop.goods.Goods;

import java.math.BigDecimal;
import java.util.Set;

public interface ShopServices {
    BigDecimal calculateGoodsSellingPrice(Goods goods, BigDecimal expiryDiscount);

    //void sellGoods(Shop shop);
    void assignCashierToChekout(Set<Checkouts> checkoutsSet, Set<Cashiers> cashiersSet);
    BigDecimal calculateCashierExpenses();
    BigDecimal shopInventarExpenses();
}
