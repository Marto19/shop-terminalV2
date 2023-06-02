package org.example.shop;

import org.example.overridenstructures.LimitedHashSet;
import org.example.shop.checkout.Checkouts;
import org.example.shop.checkout.Receipt;
import org.example.shop.exceptions.ExpiryDateException;
import org.example.shop.exceptions.NoSoldItems;
import org.example.shop.goods.Goods;
import org.example.shop.goods.GoodsType;
import org.example.shop.goods.SoldGoods;
import org.example.shop.services.ShopServices;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Shop implements ShopServices {
    private Map<GoodsType, BigDecimal> markup;
    private int daysUntilExpiryDiscountApplied;
    private BigDecimal expiryDiscount;
    private Map<Checkouts, Cashiers> checkoutsCashiersMap;
    private int numberOfCheckouts;
    private Set<Cashiers> cashiersSet;
    private Set<Goods> storeGoods;
    private Set<Checkouts> checkoutsSet;
    private Set<SoldGoods> soldItems;
    private Set<Receipt> receiptSet;
    private int numberOfReceipt = 0;


    public Shop(BigDecimal foodMarkup, BigDecimal nonFoodMarkup, int daysUntilExpiryDiscount, BigDecimal expiryDiscount, int numberOfCheckouts) {
        markup = new EnumMap<GoodsType, BigDecimal>(GoodsType.class);
        markup.put(GoodsType.FOOD, foodMarkup);
        markup.put(GoodsType.NONFOOD, nonFoodMarkup);
        this.daysUntilExpiryDiscountApplied = daysUntilExpiryDiscount;
        this.expiryDiscount = expiryDiscount;
        this.checkoutsCashiersMap = new HashMap<>();
        this.numberOfCheckouts = numberOfCheckouts;
        this.cashiersSet = new LimitedHashSet<>(numberOfCheckouts);
        this.storeGoods = new HashSet<>();
        this.checkoutsSet = new LinkedHashSet<>(numberOfCheckouts);
        this.soldItems = new HashSet<>();
        this.receiptSet = new HashSet<>();
    }

    public int getDaysUntilExpiryDiscountApplied() {
        return daysUntilExpiryDiscountApplied;
    }

    public BigDecimal getFoodMarkup() {
        return markup.get(GoodsType.FOOD);
    }
    public BigDecimal getNonFoodMarkup() {
        return markup.get(GoodsType.NONFOOD);
    }

    public Set<Goods> getStoreGoods() {
        return storeGoods;
    }
    public int getNumberOfCheckouts() {
        return numberOfCheckouts;
    }

    public BigDecimal getExpiryDiscount() {
        return expiryDiscount;
    }

    public Set<Cashiers> getCashiersSet() {
        return cashiersSet;
    }

    public Set<Checkouts> getCheckoutsSet() {
        return checkoutsSet;
    }

    public Set<SoldGoods> getSoldItems() {
        return soldItems;
    }

    public Map<Checkouts, Cashiers> getCheckoutsCashiersMap() {
        return checkoutsCashiersMap;
    }

    public Set<Receipt> getReceiptSet() {
        return receiptSet;
    }

    public int getNumberOfReceipt() {
        return numberOfReceipt;
    }

    public void setNumberOfReceipt(int numberOfReceipt) {
        this.numberOfReceipt = numberOfReceipt;
    }

    //-----------------------------------calculate goods selling price method--------------------------
    @Override
    public BigDecimal calculateGoodsSellingPrice(Goods goods, BigDecimal expiryDiscount) {//shop.getExpiryDiscount()
        BigDecimal markup = goods.getGoodsType() == GoodsType.FOOD ? getFoodMarkup() : getNonFoodMarkup();
        BigDecimal unitShippingCost = goods.getUnitShippingCost();
        int daysUntilExpiry = 0;

        if (goods.getGoodsType() == GoodsType.FOOD) {
            LocalDate expiryDate = goods.getExpiryDate();
            LocalDate currentDate = LocalDate.now();
            if (expiryDate != null) {
                daysUntilExpiry = (int) currentDate.until(expiryDate, ChronoUnit.DAYS);
            }
        }

        // Apply markup based on the goods type
        applyMarkup(unitShippingCost, markup, goods);

        BigDecimal sellingPrice = unitShippingCost.add(markup);
        // Apply expiry date discount if applicable
//        if (daysUntilExpiry <= getDaysUntilExpiryDiscountApplied()) {
//            sellingPrice = subtractFromPrice(sellingPrice, expiryDiscount);
//        }
//        goods.setFinalPrice(sellingPrice);//u was the problem
        return sellingPrice;
    }

    private void applyMarkup(BigDecimal cost, BigDecimal markup, Goods goods) {
        BigDecimal percent = markup.divide(BigDecimal.valueOf(100));
        goods.setFinalPrice(cost.add(cost.multiply(percent)));
    }


    public BigDecimal subtractFromPrice(BigDecimal price, BigDecimal discount) {
        BigDecimal percent = discount.divide(BigDecimal.valueOf(100));
        return price.subtract(price.multiply(percent));
    }

    private void handleExpiryProduct(){
        try {
            throw new ExpiryDateException("This product has expired.");
        } catch (ExpiryDateException e) {
            throw new RuntimeException(e);
        }
    }
    //-----------------------------------add cashiers to set-------------------------------------------
    public void addCashierToSet(Cashiers cashier) {
        cashiersSet.add(cashier);
    }

    //-----------------------------------add checkouts to set-----------------------------------------
    public void addCheckoutToSet(Checkouts checkouts) {
        checkoutsSet.add(checkouts);
    }
    //-------------------------------------add goods to set---------------------------------------------
    public void addGoodsToSet(Goods goods) {
        storeGoods.add(goods);
    }

    //--------------------------------------add to sold item set--------------------------------------------------
    public void addGoodToSoldSet(SoldGoods goods){
        soldItems.add(goods);
    }
    //-----------------------------------assign cashiers to checkouts in a hashmap---------------------

    @Override
    public void assignCashierToChekout(Set<Checkouts> checkoutsSet, Set<Cashiers> cashiersSet) {
        Iterator<Cashiers> cashiersIterator = cashiersSet.iterator();
        for (Checkouts checkouts : checkoutsSet){
            if (cashiersIterator.hasNext()){
                Cashiers cashiers = cashiersIterator.next();
                checkoutsCashiersMap.put(checkouts, cashiers);
                checkoutsCashiersMap.containsValue(cashiers);
            }else{
                break;
            }
        }
    }

    //-----------------------------generate checkouts------------------------------------------
    public List<Checkouts> generateCheckouts(int amount, Shop shop) {
        List<Checkouts> generatedCheckouts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            UID uid = new UID(); // Generate a random UID
            Checkouts checkout = new Checkouts(); // Create a new Checkout object with the generated UID
            shop.addCheckoutToSet(checkout);
            generatedCheckouts.add(checkout); // Add the generated Checkout to the list
        }
        System.out.println("Checkouts generated and saved to the store!");
        return generatedCheckouts;
    }

    //----------------------------------sell goods from store-------------------------------------------

//    public void sellGoods(Shop shop) {
//        //if we choose a checkout to go we can try to
//        //take the cashier and checkout to use the
//        //markGoods and scanGoods from the different classes
//        Random random = new Random();
//        int balance = random.nextInt(101); // Generate a random balance between 0 and 100
//        System.out.println("Your balance: " + balance);
//        //IZNASQNE W CHECKOUT, IZPOLZWANE TAM NA CASHIER I SHOP
//        Scanner scanner = new Scanner(System.in);
//        String input = "";
//        while (!input.equalsIgnoreCase("stop")) {
//            System.out.println("What do you want to buy?");
//            shop.printStoreGoods();
//            System.out.print("Enter name: ");
//            String goodsName = scanner.nextLine();
//            // Find the goods by name
//            Goods selectedGoods = shop.findGoodsByName(goodsName);
//            if (selectedGoods == null) {
//                System.out.println("Invalid goods name. Please try again.");
//                continue;
//            }
//            System.out.print("Enter quantity: ");
//            int quantity = scanner.nextInt();
//            scanner.nextLine(); // Consume newline character
//            // Check if there are enough goods in the store
//            if (quantity > selectedGoods.getQuantity()) {
//                System.out.println("Insufficient quantity. Please try again.");
//                continue;
//            }
//            // Check if the balance is sufficient -DO TUK SME
//            BigDecimal totalPrice = selectedGoods.getFinalPrice().multiply(BigDecimal.valueOf(quantity));
//            if (totalPrice.compareTo(BigDecimal.valueOf(balance)) > 0) {
//                System.out.println("Insufficient balance. Please try again.");
//                continue;
//            }
//            // Sell the goods and update the quantity-TOWA TRQBWA DA NAPRAVIM
//            selectedGoods.setQuantity(selectedGoods.getQuantity() - quantity);
//            shop.getSoldItems().add(selectedGoods);
//            System.out.println(quantity + " " + selectedGoods.getName() + " sold.");
//            shop.printStoreGoods();
//            shop.addGoodToSoldSet(selectedGoods);
//            shop.printSoldGoods();
//            balance -= totalPrice.intValue(); // Deduct the total price from the balance
//            System.out.println("Remaining balance: " + balance);
//            System.out.print("Enter 'stop' to finish or any key to continue buying: ");
//            input = scanner.nextLine();
//        }
//    }

    //    public Goods findGoodsByName(String goodsName) {
//        for (Goods goods : storeGoods) {
//            if (goods.getName().equalsIgnoreCase(goodsName)) {
//                return goods;
//            }
//        }
//        return null;
//    }
    public Goods findGoodsByName(String goodsName) {
        return getStoreGoods().stream()
                .filter(goods -> goods.getName().equalsIgnoreCase(goodsName))
                .findFirst()
                .orElse(null);
    }




    //----------------choose checkout method----------------------------

//    public Map.Entry<Checkouts, Cashiers> chooseCheckoutWithCashier() {
//        List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers = new ArrayList<>();
//        for (Map.Entry<Checkouts, Cashiers> entry : checkoutsCashiersMap.entrySet()) {
//            Checkouts checkout = entry.getKey();
//            Cashiers cashier = entry.getValue();
//            if (cashier != null) {
//                checkoutsWithCashiers.add(entry);
//            }
//        }
//        if (checkoutsWithCashiers.isEmpty()) {
//            System.out.println("No checkouts with assigned cashiers.");
//            return null;
//        }
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Checkouts with Assigned Cashiers:");
//        for (int i = 0; i < checkoutsWithCashiers.size(); i++) {
//            System.out.println((i + 1) + ": " + checkoutsWithCashiers.get(i).getKey());
//        }
//        System.out.print("Choose a checkout (1-" + checkoutsWithCashiers.size() + "): ");
//        int choice = scanner.nextInt();
//        scanner.nextLine(); // Consume newline character
//        if (choice < 1 || choice > checkoutsWithCashiers.size()) {
//            System.out.println("Invalid choice. Please try again.");
//            return null;
//        }
//        return checkoutsWithCashiers.get(choice - 1);
//    }


    private List<Map.Entry<Checkouts, Cashiers>> filterCheckoutsWithAssignedCashiers() {
        return checkoutsCashiersMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toList());
    }
    private void printCheckoutsWithAssignedCashiers(List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers) {
        System.out.println("Checkouts with Assigned Cashiers:");
        for (int i = 0; i < checkoutsWithCashiers.size(); i++) {
            System.out.println((i + 1) + ": " + checkoutsWithCashiers.get(i).getKey());
        }
    }
    private Map.Entry<Checkouts, Cashiers> getUserChoice(List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose a checkout (1-" + checkoutsWithCashiers.size() + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        if (choice < 1 || choice > checkoutsWithCashiers.size()) {
            System.out.println("Invalid choice. Please try again.");
            return null;
        }
        return checkoutsWithCashiers.get(choice - 1);
    }

    public Map.Entry<Checkouts, Cashiers> chooseCheckoutWithCashier() {
        List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers = filterCheckoutsWithAssignedCashiers();
        if (checkoutsWithCashiers.isEmpty()) {
            System.out.println("No checkouts with assigned cashiers.");
            return null;
        }
        printCheckoutsWithAssignedCashiers(checkoutsWithCashiers);
        return getUserChoice(checkoutsWithCashiers);
    }


    //---------------------method to calculate cashier expenses--------------------------------

    @Override
    public BigDecimal calculateCashierExpenses() {
        return getCashiersSet().stream()
                .map(Cashiers::getMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //---------------------------method to calculate the shops income---------------------------------
    public BigDecimal calculateIncome() {
        if (soldItems.isEmpty()) {
            handleNoSoldItems();
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        for (SoldGoods soldGoods : soldItems) {
            BigDecimal itemIncome = soldGoods.getFinalPrice().multiply(BigDecimal.valueOf(soldGoods.getQuantity()));
            totalIncome = totalIncome.add(itemIncome);
        }

        return totalIncome;
    }


    public void handleNoSoldItems(){
        try {
            throw new NoSoldItems("There are no sold items to calculate the income");
        } catch (NoSoldItems e) {
            throw new RuntimeException(e);
        }
    }
    //---------------------------------------PRINTING METHODS-----------------------------------------

    //----------------------------------------print cashiers-----------------------------
    public void printCashiers() {
        for (Cashiers cashier : cashiersSet) {
            System.out.println(cashier);
        }
    }
    //--------------------------------------------print goods set--------------------------------
    public void printStoreGoods() {
        for (Goods goods : getStoreGoods()) {
            System.out.println(goods);
        }
    }
    //--------------------------------------printing the checkout cashiers map-------------------------

    public void printCheckoutsCashiersMap() {
        if (checkoutsCashiersMap.isEmpty()) {
            System.out.println("No cashiers assigned to checkouts.");
        } else {
            System.out.println("Checkouts - Cashiers Map:");
            checkoutsCashiersMap.entrySet().stream()
                    .forEach(entry -> {
                        Checkouts checkout = entry.getKey();
                        Cashiers cashier = entry.getValue();
                        System.out.println("Checkout: " + checkout + ", Cashier: " + cashier);
                    });
        }
    }


    //-------------------------------------print sold items set------------------------

    public void printSoldGoods() {
        for (Goods goods : getSoldItems()) {
            System.out.println(goods);
        }
    }


    @Override
    public String toString() {
        return "Shop{" +
                "storeGoods=" + storeGoods +
                ", markup=" + markup +
                ", daysUntilExpiryDiscountApplied=" + daysUntilExpiryDiscountApplied +
                ", expiryDiscount=" + expiryDiscount +
                ", checkoutsCashiersMap=" + checkoutsCashiersMap +
                '}';
    }
}