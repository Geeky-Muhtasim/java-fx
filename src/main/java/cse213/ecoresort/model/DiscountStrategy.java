package cse213.ecoresort.model;

public interface DiscountStrategy {
    double apply(double subtotal);
    String getDescription();
}
