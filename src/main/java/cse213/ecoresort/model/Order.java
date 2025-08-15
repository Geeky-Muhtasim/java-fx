package cse213.ecoresort.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String id;
    private int tableNo;
    private OrderStatus status;
    private final List<OrderLine> orderLines;
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;

    public Order(int tableNo) {
        this.id = UUID.randomUUID().toString();
        this.tableNo = tableNo;
        this.status = OrderStatus.DRAFT;
        this.orderLines = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.subtotal = 0.0;
        this.tax = 0.0;
        this.discount = 0.0;
        this.total = 0.0;
    }

    // Getters
    public String getId() { return id; }
    public int getTableNo() { return tableNo; }
    public void setTableNo(int tableNo) { 
        if (tableNo > 0) this.tableNo = tableNo; 
    }
    public OrderStatus getStatus() { return status; }
    public List<OrderLine> getOrderLines() { return new ArrayList<>(orderLines); } // Defensive copy
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }

    // Setters for pricing service
    public void setSubtotal(double subtotal) { 
        if (subtotal >= 0) this.subtotal = subtotal; 
    }
    
    public void setTax(double tax) { 
        if (tax >= 0) this.tax = tax; 
    }
    
    public void setTotal(double total) { 
        this.total = total; 
    }

    // Business methods
    public void addLine(MenuItem item, int quantity) {
        if (item != null && quantity > 0 && item.hasStock(quantity)) {
            OrderLine line = new OrderLine(item, quantity);
            orderLines.add(line);
            recalculateTotals();
        }
    }

    public boolean removeLine(String itemId) {
        return orderLines.removeIf(line -> line.getItemId().equals(itemId));
    }

    public void updateLineQuantity(String itemId, int newQuantity) {
        orderLines.stream()
                .filter(line -> line.getItemId().equals(itemId))
                .findFirst()
                .ifPresent(line -> {
                    line.setQuantity(newQuantity);
                    recalculateTotals();
                });
    }

    public void recalculateTotals() {
        this.subtotal = orderLines.stream()
                .mapToDouble(OrderLine::getLineTotal)
                .sum();
        
        this.tax = subtotal * 0.10; // 10% tax
        this.total = subtotal + tax - discount;
    }

    public void setDiscount(double discountAmount) {
        if (discountAmount >= 0 && discountAmount <= subtotal) {
            this.discount = discountAmount;
            this.total = subtotal + tax - discount;
        }
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public boolean isDraft() {
        return status == OrderStatus.DRAFT;
    }

    public boolean isPaid() {
        return status == OrderStatus.PAID;
    }

    public enum OrderStatus {
        DRAFT("Draft"),
        PAID("Paid");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
