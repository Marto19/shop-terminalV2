package org.example.shop.goods;

import org.example.shop.exceptions.FinalPriceNotReady;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.time.LocalDate;
import java.util.UUID;

public class Goods implements Serializable {
    private UUID uuid;
    private String name;
    private BigDecimal unitShippingCost;
    private GoodsType goodsType;
    private LocalDate expiryDate;
    private BigDecimal finalPrice;
    private int quantity;

    public Goods(String name, BigDecimal unitShippingCost, GoodsType goodsType, LocalDate expiryDate, int quantity) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.unitShippingCost = unitShippingCost;
        this.goodsType = goodsType;
        if (goodsType == GoodsType.NONFOOD){
            this.expiryDate = null;
        }else {
            this.expiryDate = expiryDate;
        }
        this.quantity = quantity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GoodsType getGoodsType() {
        return goodsType;
    }

    public BigDecimal getUnitShippingCost() {
        return unitShippingCost;
    }

    public void setUnitShippingCost(BigDecimal unitShippingCost) {
        this.unitShippingCost = unitShippingCost;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public BigDecimal getFinalPrice() {
        // Check if final price is null and handle it
        handleFinalPriceNotReady();
        return finalPrice;
    }

    public void handleFinalPriceNotReady() {
        if (finalPrice == null) {
            try {
                throw new FinalPriceNotReady("Final price is not available for this goods");
            } catch (FinalPriceNotReady e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public void setGoodsType(GoodsType goodsType) {
        this.goodsType = goodsType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", unitShippingCost=" + unitShippingCost +
                ", goodsType=" + goodsType +
                ", expiryDate=" + expiryDate +
                ", finalPrice=" + finalPrice +
                ", quantity=" + quantity +
                '}';
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
