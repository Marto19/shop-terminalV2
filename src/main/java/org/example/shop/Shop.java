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
    private UUID shopUuid;
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
        this.shopUuid = UUID.randomUUID();
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

    public void setSoldItems(Set<SoldGoods> soldItems) {
        this.soldItems = soldItems;
    }

    public void setStoreGoods(Set<Goods> storeGoods) {
        this.storeGoods = storeGoods;
    }

    public UUID getShopUuid() {
        return shopUuid;
    }

    public void setCashiersSet(Set<Cashiers> cashiersSet) {
        this.cashiersSet = cashiersSet;
    }

    public void setCheckoutsSet(Set<Checkouts> checkoutsSet) {
        this.checkoutsSet = checkoutsSet;
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
        return sellingPrice;
    }

    protected void applyMarkup(BigDecimal cost, BigDecimal markup, Goods goods) {
        BigDecimal percent = markup.divide(BigDecimal.valueOf(100));
        goods.setFinalPrice(cost.add(cost.multiply(percent)));
    }


    public BigDecimal subtractFromPrice(BigDecimal price, BigDecimal discount) {
        if (price == null || discount == null) {
            return BigDecimal.ZERO; // Return zero if either price or discount is null
        }
        BigDecimal percent = discount.divide(BigDecimal.valueOf(100));
        return price.subtract(price.multiply(percent));
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
            Checkouts checkout = new Checkouts(); // Create a new Checkout object with the generated UID
            shop.addCheckoutToSet(checkout);
            generatedCheckouts.add(checkout); // Add the generated Checkout to the list
        }
        System.out.println("Checkouts generated and saved to the store!");
        return generatedCheckouts;
    }

    public Goods findGoodsByName(String goodsName) {
        return getStoreGoods().stream()
                .filter(goods -> goods.getName().equalsIgnoreCase(goodsName))
                .findFirst()
                .orElse(null);
    }

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
    protected Map.Entry<Checkouts, Cashiers> getUserChoice(List<Map.Entry<Checkouts, Cashiers>> checkoutsWithCashiers) {
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

    //------------------------------method to calculate shops inventar expenses----------------------------


    @Override
    public BigDecimal shopInventarExpenses() {
        return getStoreGoods().stream()
                .map(goods -> {
                    BigDecimal finalPrice = goods.getFinalPrice();
                    if (finalPrice != BigDecimal.ZERO) { //its was null
                        return finalPrice.multiply(BigDecimal.valueOf(goods.getQuantity()));
                    } else {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                "shopUuid=" + shopUuid +
                "storeGoods=" + storeGoods +
                ", markup=" + markup +
                ", daysUntilExpiryDiscountApplied=" + daysUntilExpiryDiscountApplied +
                ", expiryDiscount=" + expiryDiscount +
                ", checkoutsCashiersMap=" + checkoutsCashiersMap +
                '}';
    }
}