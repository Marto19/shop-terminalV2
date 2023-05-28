package org.example;

import org.example.shop.Cashiers;
import org.example.shop.Shop;
import org.example.shop.checkout.Checkouts;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.server.UID;
import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, welcome to the Shop Simulator!");

        BigDecimal foodMarkup;
        BigDecimal nonFoodMarkup = null;
        int daysUntilExpiryDiscount = 0;
        BigDecimal expiryDateDiscount = null;
        Shop shop = null;

        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equalsIgnoreCase("exit")) {
            System.out.println("Choose an option:");
            System.out.println("1. Make a shop");
            System.out.println("2. Add store goods");
            System.out.println("3. Create cashiers");
            System.out.println("4. Assign cashiers to checkouts");
            System.out.println("5. Print shop goods");
            System.out.println("6. Print cashiers");
            System.out.println("7. Buy stuff");
            System.out.println("8. Calculate cashier expenses");
            System.out.println("9. Calculate shop's income");
            System.out.println("Enter 'exit' to quit");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("Let's make a shop");
                    System.out.println("Enter four values for the shop");
                    System.out.print("Enter the food markup: ");
                    String markup = scanner.nextLine();
                    foodMarkup = new BigDecimal(markup);

                    System.out.print("Enter value for non-food markup: ");
                    markup = scanner.nextLine();
                    nonFoodMarkup = new BigDecimal(markup);

                    System.out.print("Enter a value for days after a discount will be applied: ");
                    String days = scanner.nextLine();
                    daysUntilExpiryDiscount = Integer.parseInt(days);

                    System.out.print("Enter a value for the discount itself: ");
                    String discount = scanner.nextLine();
                    expiryDateDiscount = new BigDecimal(discount);

                    System.out.print("Enter the number of checkouts: ");
                    int numberOfCheckouts = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character

                    // Create the Shop object with the obtained values
                    shop = new Shop(foodMarkup, nonFoodMarkup, daysUntilExpiryDiscount, expiryDateDiscount, numberOfCheckouts);

                    System.out.println("Shop created!");
                    break;
                case "2":
                    if (shop == null) {
                        System.out.println("No shop exists. Please create a shop first.");
                    } else {
                        System.out.println("Add store goods");
                        System.out.println("Enter the goods to add (one per line, leave blank to finish):");
                        Set<String> storeGoods = new HashSet<>();
                        String goods = scanner.nextLine();
                        while (!goods.isEmpty()) {
                            System.out.print("Enter the unit shipping cost: ");
                            BigDecimal unitShippingCost = scanner.nextBigDecimal();
                            scanner.nextLine(); // Consume newline character

                            System.out.print("Enter the goods type (FOOD/NONFOOD): ");
                            String goodsTypeString = scanner.nextLine();
                            GoodsType goodsType = GoodsType.valueOf(goodsTypeString.toUpperCase());

                            LocalDate expiryDate = null;
                            if (goodsType == GoodsType.FOOD) {
                                System.out.print("Enter the expiry date (yyyy-MM-dd): ");
                                String expiryDateString = scanner.nextLine();
                                expiryDate = LocalDate.parse(expiryDateString);
                            }

                            if (goodsType == GoodsType.FOOD) {

                                System.out.print("Enter the quantity: ");
                                int quantity = scanner.nextInt();
                                scanner.nextLine(); // Consume newline character

                                org.example.shop.goods.Goods newGoods = new org.example.shop.goods.Goods(goods, unitShippingCost, goodsType, expiryDate, quantity);
                                BigDecimal expiryDiscount = shop.getExpiryDiscount();
                                BigDecimal sellingPrice = shop.calculateGoodsSellingPrice(newGoods, expiryDiscount);
                                newGoods.setUnitShippingCost(sellingPrice);
                                shop.addGoodsToSet(newGoods);

                                System.out.println("Goods added!");
                                shop.printStoreGoods();

                                goods = scanner.nextLine();
                            } else {
                                System.out.print("Enter the final price: ");
                                BigDecimal finalPrice = scanner.nextBigDecimal();
                                scanner.nextLine(); // Consume newline character

                                System.out.print("Enter the quantity: ");
                                int quantity = scanner.nextInt();
                                scanner.nextLine(); // Consume newline character

                                org.example.shop.goods.Goods newGoods = new org.example.shop.goods.Goods(goods, unitShippingCost, goodsType, expiryDate, quantity);
                                BigDecimal sellingPrice = shop.calculateGoodsSellingPrice(newGoods, BigDecimal.valueOf(0));
                                newGoods.setUnitShippingCost(sellingPrice);
                                shop.addGoodsToSet(newGoods);

                                System.out.println("Goods added!");
                                shop.printStoreGoods();

                                goods = scanner.nextLine();
                            }
                        }
                    }
                    break;
                case "3": // Creating cashiers
                    if (shop == null) {
                        System.out.println("No shop exists. Please create a shop first.");
                    } else {
                        System.out.println("Creating cashiers");

                        numberOfCheckouts = shop.getNumberOfCheckouts();
                        int currentCashierCount = shop.getCashiersSet().size();

                        if (currentCashierCount >= numberOfCheckouts) {
                            System.out.println("The maximum number of cashiers has been reached.");
                            break;
                        }

                        int cashierCount = currentCashierCount + 1;

                        while (cashierCount <= numberOfCheckouts) {
                            System.out.print("Enter the name of cashier " + cashierCount + ": ");
                            String cashierName = scanner.nextLine();
                            UUID uuid = UUID.randomUUID();

                            // Assign a default salary for the cashier
                            BigDecimal monthlySalary = new BigDecimal("2000.00"); // Example salary value

                            System.out.println("Cashier created with UUID: " + uuid.toString());

                            Cashiers cashier = new Cashiers(cashierName, uuid, monthlySalary); // Create a new Cashier object

                            shop.addCashierToSet(cashier); // Add the cashier to the shop's cashier set

                            cashierCount++;

                            if (cashierCount > numberOfCheckouts) {
                                System.out.println("You reached the maximum checkouts number.");
                                break;
                            }

                            System.out.print("Do you want to create another cashier? (yes/no): ");
                            String createAnother = scanner.nextLine();
                            if (!createAnother.equalsIgnoreCase("yes")) {
                                break;
                            }
                        }
                        shop.printCashiers();
                    }
                    break;
                case "4":
                    if (shop == null) {
                        System.out.println("No shop exists. Please create a shop first.");
                    } else if (shop.getCashiersSet().isEmpty()) {
                        System.out.println("No cashiers available. Please create cashiers first.");
                    } else {
                        System.out.println("Assigning cashiers to checkouts");
                        shop.generateCheckouts(shop.getNumberOfCheckouts(), shop);
                        shop.assignCashierToChekout(shop.getCheckoutsSet(), shop.getCashiersSet());
                        System.out.println("Cashiers assigned to checkouts!");
                        shop.printCheckoutsCashiersMap();
                        System.out.println();
                    }
                    break;
                case "exit":
                    System.out.println("Exiting the Shop Simulator. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
                case "5":
                    System.out.println("Shop goods:");
                    shop.printStoreGoods();
                    break;
                case "6":
                    System.out.println("Cashiers: ");
                    shop.printCashiers();
                    break;
                case "7":
                    if (shop.getStoreGoods() == null){
                        System.out.println("First add goods to the store");
                    }
                    else {
                        System.out.println("Buy stuff");
                        System.out.println("Shop's inventory");
                        shop.printStoreGoods();
                        // Choose a checkout with a cashier
                        Map.Entry<Checkouts, Cashiers> chosenCheckoutWithCashier = shop.chooseCheckoutWithCashier();
                        if (chosenCheckoutWithCashier != null) {
                            Checkouts chosenCheckout = chosenCheckoutWithCashier.getKey();
                            Cashiers chosenCashier = chosenCheckoutWithCashier.getValue();
                            System.out.println("Chosen checkout: " + chosenCheckout);
                            System.out.println("Chosen cashier: " + chosenCashier);
                            // Create a new customer
                            Customer customer = new Customer();
                            // Mark the goods and get the shopping list
                            HashMap<String, Integer> shoppingList = chosenCashier.markGoods(shop, customer);

                            // Pass the shopping list to the chosen checkout
                            chosenCheckout.sellGoods(shop, customer.getBalance(), shoppingList, chosenCheckout, chosenCashier);
                        }
                        break;
                    }
                case "8":
                    if (shop.getCashiersSet() == null){
                        System.out.println("First add cashiers to the store");
                    }
                    else {
                        System.out.println("Cashier's expenses: " + shop.calculateCashierExpenses());
                    }
                case "9":
                    System.out.println("Shop's income is: " + shop.calculateIncome());
            }
        }
    }

}
//ADD INTERFACE IMPLEMENTED BY CASHIERS TO MARK THE PRODUCTS
/*
- THE METHOD IS GOING TO TAKE AS A PARAMETER GOODS LIST, ONE GOOD OR GOODS WITH SPACES AND THE QUANTITY

TODO: check why the shopping list is empty and the total value 0
TODO: fix the calculation of the markup properly
* */