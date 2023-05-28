package org.example.shop.exceptions;

public class NameException extends Throwable {
    public NameException(String nameNotCorrect) {
        super(nameNotCorrect);
    }
}
