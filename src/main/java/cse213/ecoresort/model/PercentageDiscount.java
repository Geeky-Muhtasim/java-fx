package cse213.ecoresort.model;

public class PercentageDiscount implements DiscountStrategy {
    private final double percentage;

    public PercentageDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public double apply(double subtotal) {
        return subtotal * (percentage / 100.0);
    }

    @Override
    public String getDescription() {
        return percentage + "% Discount";
    }

    public double getPercentage() {
        return percentage;
    }
}
