package cse213.ecoresort.service;

import cse213.ecoresort.model.DiscountStrategy;
import cse213.ecoresort.model.MenuItem;
import cse213.ecoresort.model.Order;
import cse213.ecoresort.model.OrderLine;

import java.util.List;

/**
 * Service for handling pricing calculations including subtotal, tax, and discounts
 * Demonstrates abstraction through the DiscountStrategy interface
 */
public class PricingService {
    
    private static final double TAX_RATE = 0.10; // 10% tax rate
    
    /**
     * Calculate subtotal from order lines
     */
    public double calculateSubtotal(List<OrderLine> orderLines) {
        return orderLines.stream()
                .mapToDouble(OrderLine::getLineTotal)
                .sum();
    }
    
    /**
     * Calculate tax amount based on subtotal
     */
    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }
    
    /**
     * Apply discount strategy to subtotal
     */
    public double applyDiscount(double subtotal, DiscountStrategy discountStrategy) {
        return discountStrategy.apply(subtotal);
    }
    
    /**
     * Calculate total amount (subtotal + tax - discount)
     */
    public double calculateTotal(double subtotal, double tax, double discount) {
        return subtotal + tax - discount;
    }
    
    /**
     * Recalculate all totals for an order using discount strategy
     */
    public void recalculateOrderTotals(Order order, DiscountStrategy discountStrategy) {
        double subtotal = calculateSubtotal(order.getOrderLines());
        double tax = calculateTax(subtotal);
        double discount = applyDiscount(subtotal, discountStrategy);
        double total = calculateTotal(subtotal, tax, discount);
        
        // Update order totals
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setDiscount(discount);
        order.setTotal(total);
    }
    
    /**
     * Get the tax rate as a percentage
     */
    public double getTaxRate() {
        return TAX_RATE * 100; // Return as percentage
    }
    
    /**
     * Validate discount percentage
     */
    public boolean isValidDiscountPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }
    
    /**
     * Calculate discount amount from percentage
     */
    public double calculateDiscountAmount(double subtotal, double percentage) {
        if (!isValidDiscountPercentage(percentage)) {
            return 0.0;
        }
        return subtotal * (percentage / 100.0);
    }
}
