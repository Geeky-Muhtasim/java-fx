package cse213.ecoresort.repository;

import cse213.ecoresort.model.Order;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {
    private static InMemoryOrderRepository instance;
    private final Map<String, Order> orders;

    private InMemoryOrderRepository() {
        orders = new ConcurrentHashMap<>();
    }

    public static synchronized InMemoryOrderRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryOrderRepository();
        }
        return instance;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order must have an ID");
        }
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public boolean delete(String id) {
        return orders.remove(id) != null;
    }

    @Override
    public List<Order> findByTable(int tableNo) {
        return orders.values().stream()
                .filter(order -> order.getTableNo() == tableNo)
                .toList();
    }

    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }
}
