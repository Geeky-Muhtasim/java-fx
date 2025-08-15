package cse213.ecoresort;

import cse213.ecoresort.model.*;
import cse213.ecoresort.service.MenuService;
import cse213.ecoresort.service.OrderService;
import cse213.ecoresort.service.PaymentService;
import cse213.ecoresort.service.PricingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to demonstrate the four OOP principles implemented in Eco-Resort
 */
public class OOPPrinciplesTest {
    
    private PricingService pricingService;
    
    @BeforeEach
    public void setUp() {
        pricingService = new PricingService();
    }
    
    @Test
    public void testPricingServiceCalculations() {
        // Test pricing service calculations
        double subtotal = 100.0;
        double tax = pricingService.calculateTax(subtotal);
        double expectedTax = 10.0; // 10% of 100
        
        assertEquals(expectedTax, tax, 0.01, "Tax calculation should be correct");
        
        // Test discount validation
        assertTrue(pricingService.isValidDiscountPercentage(15.0), "15% discount should be valid");
        assertFalse(pricingService.isValidDiscountPercentage(150.0), "150% discount should be invalid");
        assertFalse(pricingService.isValidDiscountPercentage(-5.0), "Negative discount should be invalid");
    }
    
    @Test
    public void testDiscountStrategies() {
        // Test discount strategies (polymorphism)
        DiscountStrategy noDiscount = new NoDiscount();
        DiscountStrategy tenPercentDiscount = new PercentageDiscount(10.0);
        
        double subtotal = 100.0;
        
        assertEquals(0.0, noDiscount.apply(subtotal), 0.01, "No discount should return 0");
        assertEquals(10.0, tenPercentDiscount.apply(subtotal), 0.01, "10% discount should return 10");
    }
    
    public static void main(String[] args) {
        System.out.println("=== Eco-Resort OOP Principles Demonstration ===\n");
        
        // 1. ENCAPSULATION - Private fields with controlled access
        demonstrateEncapsulation();
        
        // 2. INHERITANCE - MenuItem as base class, FoodItem/DrinkItem as subclasses
        demonstrateInheritance();
        
        // 3. ABSTRACTION - Interfaces hiding implementation details
        demonstrateAbstraction();
        
        // 4. POLYMORPHISM - Same interface, different implementations
        demonstratePolymorphism();
        
        System.out.println("\n=== All OOP Principles Successfully Demonstrated! ===");
    }
    
    private static void demonstrateEncapsulation() {
        System.out.println("1. ENCAPSULATION:");
        System.out.println("   - Private fields with controlled access through getters/setters");
        System.out.println("   - Business methods enforce invariants");
        
        FoodItem burger = new FoodItem("Classic Burger", 12.99, 50, "American", false);
        
        // Can't access private fields directly
        // burger.price = -5.0; // This would cause compilation error
        
        // Must use controlled setter
        burger.setPrice(15.99); // Valid price
        burger.setPrice(-5.0);  // Invalid price - will be ignored
        System.out.println("   - Burger price after valid change: $" + burger.getPrice());
        System.out.println("   - Burger price after invalid change: $" + burger.getPrice());
        
        // Business method enforces stock rules
        System.out.println("   - Initial stock: " + burger.getStockQty());
        burger.decreaseStock(10);
        System.out.println("   - Stock after decreasing 10: " + burger.getStockQty());
        System.out.println("   - Available: " + burger.isAvailable());
        burger.decreaseStock(50); // Try to decrease more than available
        System.out.println("   - Stock after invalid decrease: " + burger.getStockQty());
        System.out.println();
    }
    
    private static void demonstrateInheritance() {
        System.out.println("2. INHERITANCE:");
        System.out.println("   - MenuItem as abstract base class");
        System.out.println("   - FoodItem and DrinkItem extend MenuItem");
        
        MenuItem[] items = {
            new FoodItem("Pasta", 14.99, 30, "Italian", true),
            new DrinkItem("Coffee", 3.99, 100, false, "Hot")
        };
        
        for (MenuItem item : items) {
            System.out.println("   - " + item.getName() + " (" + item.getType() + ")");
            System.out.println("     Description: " + item.getDescription());
            System.out.println("     Price: $" + item.getPrice());
        }
        System.out.println();
    }
    
    private static void demonstrateAbstraction() {
        System.out.println("3. ABSTRACTION:");
        System.out.println("   - PaymentMethod interface hides implementation details");
        System.out.println("   - DiscountStrategy interface provides discount calculation");
        
        PaymentMethod cashPayment = new CashPayment();
        PaymentMethod cardPayment = new CardPayment();
        
        System.out.println("   - Cash Payment: " + cashPayment.getDisplayName());
        System.out.println("   - Card Payment: " + cardPayment.getDisplayName());
        
        DiscountStrategy noDiscount = new NoDiscount();
        DiscountStrategy tenPercentDiscount = new PercentageDiscount(10.0);
        
        System.out.println("   - No Discount: " + noDiscount.getDescription());
        System.out.println("   - 10% Discount: " + tenPercentDiscount.getDescription());
        System.out.println();
    }
    
    private static void demonstratePolymorphism() {
        System.out.println("4. POLYMORPHISM:");
        System.out.println("   - Same interface, different implementations");
        System.out.println("   - Runtime method dispatch based on actual object type");
        
        // Polymorphic payment methods
        PaymentMethod[] paymentMethods = {
            new CashPayment(),
            new CardPayment()
        };
        
        for (PaymentMethod method : paymentMethods) {
            System.out.println("   - Payment Method: " + method.getDisplayName());
        }
        
        // Polymorphic discount strategies
        DiscountStrategy[] discountStrategies = {
            new NoDiscount(),
            new PercentageDiscount(15.0),
            new PercentageDiscount(20.0)
        };
        
        double subtotal = 100.0;
        for (DiscountStrategy strategy : discountStrategies) {
            double discount = strategy.apply(subtotal);
            System.out.println("   - " + strategy.getDescription() + 
                             " on $" + subtotal + " = $" + String.format("%.2f", discount));
        }
        System.out.println();
    }
}
