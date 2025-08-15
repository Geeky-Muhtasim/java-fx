package cse213.ecoresort.model;

public class NoDiscount implements DiscountStrategy {
    @Override
    public double apply(double subtotal) {
        return 0.0;
    }

    @Override
    public String getDescription() {
        return "No Discount";
    }
}
