package cse213.ecoresort.model;

public interface PaymentMethod {
    PaymentResult processPayment(Order order, PaymentInput input);
    String getDisplayName();
}
