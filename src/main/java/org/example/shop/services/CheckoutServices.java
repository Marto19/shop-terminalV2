package org.example.shop.services;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.checkout.Checkouts;
import org.example.shop.checkout.Receipt;
import org.example.shop.goods.Goods;

import java.math.BigDecimal;
import java.util.HashMap;

public interface CheckoutServices {
    void sellGoods(Shop shop, BigDecimal balance, HashMap<String, Integer> shoppingList, Checkouts checkouts, Cashiers cashiers);
    BigDecimal scanGoods(Shop shop, BigDecimal balance, HashMap<String, Integer> shoppingList);
    void checkGoodsAvailability(Shop shop, String goodsName, int requestedQuantity);
    BigDecimal checkGoodExpiryDate(Goods goods, Shop shop, BigDecimal totalValue); //to check expiry date and apply discount
    void checkCustomersBalance(BigDecimal balance, BigDecimal totalSum);
    void updateStoreGoods(Shop shop, HashMap<String, Integer> shoppingList);
    Receipt createReceipt(HashMap<String, Integer> shoppingList, Shop shop, Checkouts checkouts, Cashiers cashiers, BigDecimal totalValue);

    void serializeReceipt(Receipt receipt, String filePath);
    Receipt deserializeReceipt(String filePath);
}
