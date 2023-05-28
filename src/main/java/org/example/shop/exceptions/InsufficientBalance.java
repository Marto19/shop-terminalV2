package org.example.shop.exceptions;

public class InsufficientBalance extends Throwable {
    public InsufficientBalance(String yourBalanceIsInsufficient) {
        super(yourBalanceIsInsufficient);
    }
}
