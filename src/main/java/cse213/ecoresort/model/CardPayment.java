package cse213.ecoresort.model;

public class CardPayment implements PaymentMethod {
    @Override
    public PaymentResult processPayment(Order order, PaymentInput input) {
        if (input.getType() != PaymentInput.PaymentType.CARD) {
            return PaymentResult.failure("Invalid payment type for card payment");
        }

        String cardNumber = input.getCardNumber();
        if (!isValidCardNumber(cardNumber)) {
            return PaymentResult.failure("Invalid card number format. Expected: ####-####-####-####");
        }

        return PaymentResult.success("Card payment successful", 0.0);
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }

        // Simple validation: ####-####-####-#### format
        String pattern = "\\d{4}-\\d{4}-\\d{4}-\\d{4}";
        return cardNumber.matches(pattern);
    }

    @Override
    public String getDisplayName() {
        return "Card";
    }
}
