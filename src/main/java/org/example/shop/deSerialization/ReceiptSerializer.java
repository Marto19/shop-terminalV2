package org.example.shop.deSerialization;

import org.example.shop.checkout.Receipt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ReceiptSerializer {
    public static void serializeReceipt(Receipt receipt, String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(receipt);
            objectOut.close();
            fileOut.close();
            System.out.println("Receipt serialized and saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
