package cse213.ecoresort.model;

public class PaymentResult {
    private final boolean success;
    private final String message;
    private final double change;

    private PaymentResult(boolean success, String message, double change) {
        this.success = success;
        this.message = message;
        this.change = change;
    }

    public static PaymentResult success(String message, double change) {
        return new PaymentResult(true, message, change);
    }

    public static PaymentResult failure(String message) {
        return new PaymentResult(false, message, 0.0);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public double getChange() { return change; }
}
