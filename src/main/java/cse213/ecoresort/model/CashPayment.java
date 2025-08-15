package cse213.ecoresort.model;

public class CashPayment implements PaymentMethod {
    @Override
    public PaymentResult processPayment(Order order, PaymentInput input) {
        if (input.getType() != PaymentInput.PaymentType.CASH) {
            return PaymentResult.failure("Invalid payment type for cash payment");
        }

        double cashGiven = input.getCashGiven();
        double total = order.getTotal();

        if (cashGiven < total) {
            return PaymentResult.failure("Insufficient cash. Total: $" + String.format("%.2f", total) + 
                                       ", Given: $" + String.format("%.2f", cashGiven));
        }

        double change = cashGiven - total;
        return PaymentResult.success("Cash payment successful", change);
    }

    @Override
    public String getDisplayName() {
        return "Cash";
    }
}
