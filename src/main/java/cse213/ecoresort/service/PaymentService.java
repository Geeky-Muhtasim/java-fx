package cse213.ecoresort.service;

import cse213.ecoresort.model.*;
import cse213.ecoresort.repository.OrderRepository;
import cse213.ecoresort.repository.InMemoryOrderRepository;

import java.util.HashMap;
import java.util.Map;

public class PaymentService {
    private final OrderRepository orderRepository;
    private final Map<PaymentInput.PaymentType, PaymentMethod> paymentMethods;

    public PaymentService() {
        this.orderRepository = InMemoryOrderRepository.getInstance();
        this.paymentMethods = new HashMap<>();
        
        // Initialize payment methods - demonstrating polymorphism
        paymentMethods.put(PaymentInput.PaymentType.CASH, new CashPayment());
        paymentMethods.put(PaymentInput.PaymentType.CARD, new CardPayment());
    }

    public PaymentResult processPayment(String orderId, PaymentInput paymentInput) {
        // Get the appropriate payment method based on type
        PaymentMethod method = paymentMethods.get(paymentInput.getType());
        if (method == null) {
            return PaymentResult.failure("Unsupported payment method: " + paymentInput.getType());
        }

        // Get the order
        var orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return PaymentResult.failure("Order not found");
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return PaymentResult.failure("Order is already paid");
        }

        // Process the payment using the polymorphic method
        PaymentResult result = method.processPayment(order, paymentInput);
        
        if (result.isSuccess()) {
            // Mark order as paid and decrease stock
            order.markAsPaid();
            orderRepository.save(order);
        }

        return result;
    }

    public PaymentMethod getPaymentMethod(PaymentInput.PaymentType type) {
        return paymentMethods.get(type);
    }

    public boolean isPaymentMethodSupported(PaymentInput.PaymentType type) {
        return paymentMethods.containsKey(type);
    }
}
