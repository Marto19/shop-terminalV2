package org.example.shop.checkout;

import org.example.shop.Cashiers;
import org.example.shop.goods.Goods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Receipt implements Serializable {
    private int serialNumber = 1;
    private Cashiers cashier;
    private Checkouts checkouts;
    private LocalDateTime issueDateTime;
    private Set<Goods> shoppedGoods;
    private BigDecimal totalAmount;

    public Receipt(Cashiers cashier, Checkouts checkouts, Set<Goods> shoppedGoods, BigDecimal totalAmount){
        this.serialNumber = serialNumber++;
        this.cashier = cashier;
        this.checkouts = checkouts;
        this.issueDateTime = LocalDateTime.now();
        this.shoppedGoods = shoppedGoods;
        this.totalAmount = totalAmount;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Cashiers getCashier() {
        return cashier;
    }

    public Checkouts getCheckouts() {
        return checkouts;
    }

    public LocalDateTime getIssueDateTime() {
        return issueDateTime;
    }

    public Set<Goods> getShoppedGoods() {
        return shoppedGoods;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "serialNumber=" + serialNumber +
                ", cashier=" + cashier +
                ", checkout=" + checkouts +
                ", issueDateTime=" + issueDateTime +
                ", shoppedGoods=" + shoppedGoods +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
