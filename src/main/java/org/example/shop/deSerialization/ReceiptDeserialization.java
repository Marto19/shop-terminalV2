package org.example.shop.deSerialization;

import org.example.shop.checkout.Receipt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiptDeserialization {
    public static Receipt deserializeReceipt(String filePath) {
        Receipt receipt = null;
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            receipt = (Receipt) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            System.out.println("Receipt deserialized from: " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return receipt;
    }
}
