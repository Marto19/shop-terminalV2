package org.example.shop;

import org.example.Customer;
import org.example.shop.goods.Goods;
import org.example.shop.services.CashierServices;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class Cashiers implements CashierServices, Serializable {
    private String name;
    private UUID uuid;
    private BigDecimal monthlySalary;

    public String getName() {
        return name;
    }

    public Cashiers(String name, UUID uuid, BigDecimal monthlySalary) {
        this.name = name;
        this.uuid = uuid;
        this.monthlySalary = monthlySalary;
    }

    //--------------------------------------generating cashier------------------------------------------

    //-----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Cashiers{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                ", monthlySalary=" + monthlySalary +
                '}';
    }


    @Override
    public HashMap<String, Integer> markGoods(Shop shop, Customer customer) {
        HashMap<String, Integer> shoppingList = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        System.out.println("Your balance is: " + customer.getBalance());
        while (!input.equalsIgnoreCase("stop")) {
            System.out.println("What do you want to buy?");
            shop.printStoreGoods();
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Add the goods to the customer's shopping list
            shoppingList.put(name, quantity);

            System.out.print("Enter 'stop' to finish or any other key to continue: ");
            input = scanner.nextLine();
        }
        return shoppingList;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }
}
