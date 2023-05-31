package org.example.shop.checkout;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.exceptions.InsufficientBalance;
import org.example.shop.exceptions.NameException;
import org.example.shop.exceptions.NotEnoughQuantity;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.example.shop.goods.SoldGoods;
import org.example.shop.services.CheckoutServices;

import java.io.*;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Checkouts implements CheckoutServices, Serializable{
    private UUID uuid;
    public Checkouts(UID uid) {
        this.uuid = UUID.randomUUID();
    }
    public UUID getUid() {
        return uuid;
    }


    @Override
    public String toString() {
        return "Checkouts{" +
                "uuid=" + uuid +
                '}';
    }

    @Override
    public void sellGoods(Shop shop, BigDecimal balance, HashMap<String, Integer> shoppingList, Checkouts checkouts, Cashiers cashiers) {        //CHECK THE AVAILABILITY - NAME, QUANTITY
        //checkGoodsAvailability(shop,  name, quantity);
        //CHECK CUSTOMERS BALANCE AVAILABILITY
        //checkCustomersBalance(balance);
        BigDecimal totalValue = scanGoods(shop, balance, shoppingList);
        //checkCustomersBalance(balance, totalValue);
        checkCustomersBalance(balance, totalValue);
        balance = balance.subtract(totalValue);
        //VNIMANIE MOJE DA MINE NATATUK, nqma ako e newalidno shte hwurli exception
        //ako drugoqche, shte produlji nadolu sus prodavaneto
        System.out.println("Your balance: " + balance);
        //update the store
        updateStoreGoods(shop, shoppingList);
        System.out.println("Store updated!");
        shop.printStoreGoods();
        System.out.println("Sold inventar");
        shop.printSoldGoods();

        //createRecept;
        System.out.println("Your receipt cool dude:");
        System.out.println(createReceipt(shoppingList, shop, checkouts, cashiers, totalValue));

        shop.getReceiptSet().add(createReceipt(shoppingList, shop, checkouts, cashiers, totalValue));
        shop.setNumberOfReceipt(shop.getNumberOfReceipt() + 1);

        System.out.println();
        System.out.println("Receipt set: " + shop.getReceiptSet());
        System.out.println("Number of receipts: " + shop.getNumberOfReceipt());
    }

    @Override
    public BigDecimal scanGoods(Shop shop, BigDecimal balance, HashMap<String, Integer> shoppingList) {
        BigDecimal totalSum = BigDecimal.ZERO; // Initialize the total value as zero

        for (Map.Entry<String, Integer> entry : shoppingList.entrySet()) {
            String goodsName = entry.getKey();
            int requestedQuantity = entry.getValue();
            checkGoodsAvailability(shop, goodsName, requestedQuantity);

            // Get the selected goods item from the shop
            Goods selectedGoods = shop.findGoodsByName(goodsName);

            // Create a new SoldGoods object with the necessary parameters
            SoldGoods soldGoods = new SoldGoods(
                    selectedGoods.getUuid(),
                    selectedGoods.getName(),
                    selectedGoods.getUnitShippingCost(),
                    selectedGoods.getGoodsType(),
                    selectedGoods.getExpiryDate(),
                    requestedQuantity,
                    selectedGoods.getFinalPrice()
            );

            BigDecimal goodsValue = soldGoods.getFinalPrice().multiply(BigDecimal.valueOf(requestedQuantity));
            totalSum = totalSum.add(goodsValue);
        }

        return totalSum;
    }


    @Override
    public void checkGoodsAvailability(Shop shop, String goodsName, int requestedQuantity) {
        //Find the goods by name
        Goods selectedGoods = shop.findGoodsByName(goodsName);
        if (selectedGoods == null) {
            handleNameException();
        }
       if (selectedGoods.getQuantity() < requestedQuantity ){
           handleNotEnoughtQuantity(requestedQuantity, selectedGoods, shop);
       }
    }

    @Override
    public void checkGoodExpiryDate(Goods goods, Shop shop) {

    }

    public void handleNameException(){
        try {
            throw new NameException("Invalid goods name. Please try again.");
        } catch (NameException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleNotEnoughtQuantity(int requestedQuantity, Goods goods, Shop shop) {
        int availableQuantity = shop.findGoodsByName(goods.getName()).getQuantity();
        int remainingQuantity = requestedQuantity - availableQuantity;
        if (remainingQuantity > 0) {
            try {
                throw new NotEnoughQuantity("Insufficient quantity of this product. The " + goods.getName() + " needs " +
                        remainingQuantity + " more to meet your satisfaction.");
            } catch (NotEnoughQuantity e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void checkCustomersBalance(BigDecimal balance, BigDecimal totalSum) {
        if (balance.compareTo(totalSum) < 0){
            handleInsufficientBalance();
        }
    }

    public void handleInsufficientBalance(){
        try {
            throw new InsufficientBalance("Your balance is insufficient");
        } catch (InsufficientBalance e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStoreGoods(Shop shop, HashMap<String, Integer> shoppingList) {
        for (Map.Entry<String, Integer> entry : shoppingList.entrySet()) {
            String goodsName = entry.getKey();
            int purchasedQuantity = entry.getValue();
            Goods selectedGoods = shop.findGoodsByName(goodsName);

            if (selectedGoods != null) {
                int currentQuantity = selectedGoods.getQuantity();
                int updatedQuantity = currentQuantity - purchasedQuantity;

                if (updatedQuantity >= 0) {
                    // Update the quantity in storeGoods
                    selectedGoods.setQuantity(updatedQuantity);
                    // Add the sold items to the soldItems set
                    SoldGoods soldGoods = new SoldGoods(
                            selectedGoods.getUuid(),
                            selectedGoods.getName(),
                            selectedGoods.getUnitShippingCost(),
                            selectedGoods.getGoodsType(),
                            selectedGoods.getExpiryDate(),
                            purchasedQuantity,
                            selectedGoods.getFinalPrice()
                    );
                    shop.getSoldItems().add(soldGoods);
                    System.out.println(purchasedQuantity + " " + goodsName + " updated in the store.");
                } else {
                    handleNotEnoughtQuantity(purchasedQuantity, selectedGoods, shop);
                }
            } else {
                handleNameException();
            }
        }
    }

    @Override
    public Receipt createReceipt(HashMap<String, Integer> shoppingList, Shop shop, Checkouts checkouts, Cashiers cashiers, BigDecimal totalValue) {
        Set<Goods> shoppedGoodSet = new HashSet<>();
        for (Map.Entry<String, Integer> entry : shoppingList.entrySet()) {
            String goodsName = entry.getKey();
            int requestedQuantity = entry.getValue();
            Goods selectedGoods = shop.findGoodsByName(goodsName);
            if (selectedGoods != null) {
                shoppedGoodSet.add(selectedGoods);
            }
        }
        Receipt receipt = new Receipt(cashiers, checkouts, shoppedGoodSet, totalValue);
        shop.getReceiptSet().add(receipt);

        String filePath = "receipt" + shop.getNumberOfReceipt() + ".ser"; // Generate the filename with the receiptCounter value
        serializeReceipt(receipt, filePath);

        shop.setNumberOfReceipt(shop.getNumberOfReceipt() + 1); // Increment the receiptCounter

        return receipt;
    }

    //fix this:Receipt{...., shoppedGoods=[], totalAmount=0}


    @Override
    public void serializeReceipt(Receipt receipt, String filePath) {
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

    @Override
    public Receipt deserializeReceipt(String filePath) {
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
