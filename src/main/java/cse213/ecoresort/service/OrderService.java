package cse213.ecoresort.service;

import cse213.ecoresort.model.*;
import cse213.ecoresort.repository.OrderRepository;
import cse213.ecoresort.repository.InMemoryOrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuService menuService;
    private final PricingService pricingService;

    public OrderService() {
        this.orderRepository = InMemoryOrderRepository.getInstance();
        this.menuService = new MenuService();
        this.pricingService = new PricingService();
    }

    public Order createOrder(int tableNo) {
        if (tableNo <= 0) {
            throw new IllegalArgumentException("Table number must be positive");
        }
        
        Order order = new Order(tableNo);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByTable(int tableNo) {
        return orderRepository.findByTable(tableNo);
    }

    public List<Order> getDraftOrders() {
        return orderRepository.findByStatus(Order.OrderStatus.DRAFT);
    }

    public List<Order> getPaidOrders() {
        return orderRepository.findByStatus(Order.OrderStatus.PAID);
    }

    public boolean addItemToOrder(String orderId, String itemId, int quantity) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Optional<MenuItem> itemOpt = menuService.getItemById(itemId);
        
        if (orderOpt.isEmpty() || itemOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        MenuItem item = itemOpt.get();

        if (!order.isDraft()) {
            return false; // Can't modify paid orders
        }

        if (!item.hasStock(quantity)) {
            return false; // Insufficient stock
        }

        order.addLine(item, quantity);
        orderRepository.save(order);
        return true;
    }

    public boolean removeItemFromOrder(String orderId, String itemId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return false; // Can't modify paid orders
        }

        boolean removed = order.removeLine(itemId);
        if (removed) {
            order.recalculateTotals();
            orderRepository.save(order);
        }
        return removed;
    }

    public boolean updateItemQuantity(String orderId, String itemId, int newQuantity) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return false; // Can't modify paid orders
        }

        // Check if we have enough stock for the new quantity
        Optional<MenuItem> itemOpt = menuService.getItemById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().hasStock(newQuantity)) {
            return false;
        }

        order.updateLineQuantity(itemId, newQuantity);
        orderRepository.save(order);
        return true;
    }

    public boolean applyDiscount(String orderId, double discountPercentage) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return false; // Can't modify paid orders
        }

        if (!pricingService.isValidDiscountPercentage(discountPercentage)) {
            return false; // Invalid discount percentage
        }

        double discountAmount = pricingService.calculateDiscountAmount(order.getSubtotal(), discountPercentage);
        order.setDiscount(discountAmount);
        orderRepository.save(order);
        return true;
    }

    public boolean finalizeOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return false; // Already finalized
        }

        if (order.getOrderLines().isEmpty()) {
            return false; // Can't finalize empty order
        }

        // Decrease stock for all items
        for (var line : order.getOrderLines()) {
            menuService.decreaseStock(line.getItemId(), line.getQuantity());
        }

        order.markAsPaid();
        orderRepository.save(order);
        return true;
    }

    public boolean deleteOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        
        if (!order.isDraft()) {
            return false; // Can't delete paid orders
        }

        return orderRepository.delete(orderId);
    }
}
