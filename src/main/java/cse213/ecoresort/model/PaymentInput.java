package cse213.ecoresort.model;

public class PaymentInput {
    private final PaymentType type;
    private final double cashGiven;
    private final String cardNumber;

    public PaymentInput(PaymentType type, double cashGiven, String cardNumber) {
        this.type = type;
        this.cashGiven = cashGiven;
        this.cardNumber = cardNumber;
    }

    public static PaymentInput forCash(double cashGiven) {
        return new PaymentInput(PaymentType.CASH, cashGiven, null);
    }

    public static PaymentInput forCard(String cardNumber) {
        return new PaymentInput(PaymentType.CARD, 0.0, cardNumber);
    }

    public PaymentType getType() { return type; }
    public double getCashGiven() { return cashGiven; }
    public String getCardNumber() { return cardNumber; }

    public enum PaymentType {
        CASH("Cash"),
        CARD("Card");

        private final String displayName;

        PaymentType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
