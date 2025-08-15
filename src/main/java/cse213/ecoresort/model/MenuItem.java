package cse213.ecoresort.model;

import java.util.UUID;

public abstract class MenuItem {
    private final String id;
    private String name;
    private double price;
    private int stockQty;
    private boolean available;
    private final ItemType type;

    public MenuItem(String name, double price, int stockQty, ItemType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.stockQty = stockQty;
        this.type = type;
        this.available = stockQty > 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { 
        if (price >= 0) {
            this.price = price; 
        }
    }
    
    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { 
        if (stockQty >= 0) {
            this.stockQty = stockQty;
            this.available = stockQty > 0;
        }
    }
    
    public boolean isAvailable() { return available; }
    public ItemType getType() { return type; }

    // Business methods demonstrating encapsulation
    public boolean decreaseStock(int quantity) {
        if (quantity > 0 && stockQty >= quantity) {
            stockQty -= quantity;
            available = stockQty > 0;
            return true;
        }
        return false;
    }

    public void increaseStock(int quantity) {
        if (quantity > 0) {
            stockQty += quantity;
            available = true;
        }
    }

    public boolean hasStock(int quantity) {
        return stockQty >= quantity && available;
    }

    // Abstract method for subclasses to implement
    public abstract String getDescription();

    public enum ItemType {
        FOOD("Food"),
        DRINK("Drink");

        private final String displayName;

        ItemType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
