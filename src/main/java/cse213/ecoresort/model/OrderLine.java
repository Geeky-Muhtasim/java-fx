package cse213.ecoresort.model;

public class OrderLine {
    private final String itemId;
    private final String itemName;
    private final double unitPrice;
    private int quantity;
    private double lineTotal;

    public OrderLine(MenuItem item, int quantity) {
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.unitPrice = item.getPrice();
        this.quantity = quantity;
        this.lineTotal = unitPrice * quantity;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return lineTotal; }

    // Business methods
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
            this.lineTotal = unitPrice * quantity;
        }
    }

    public void updateQuantity(int newQuantity) {
        setQuantity(newQuantity);
    }
}
