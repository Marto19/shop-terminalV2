package org.example.shop.goods;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SoldGoods extends Goods {
    public SoldGoods(UUID uuid, String name, BigDecimal unitShippingCost, GoodsType goodsType, LocalDate expiryDate, int quantity, BigDecimal finalPrice) {
        super(name, unitShippingCost, goodsType, expiryDate, quantity);
        setUuid(super.getUuid());
        setQuantity(super.getQuantity());
    }

}





