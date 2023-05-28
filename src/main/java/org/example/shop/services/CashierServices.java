package org.example.shop.services;

import org.example.Customer;
import org.example.shop.Shop;
import org.example.shop.goods.Goods;

import java.util.HashMap;

public interface CashierServices {
    HashMap<String, Integer> markGoods(Shop shop, Customer customer);
}
