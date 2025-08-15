package cse213.ecoresort.repository;

import cse213.ecoresort.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(String id);
    Order save(Order order);
    boolean delete(String id);
    List<Order> findByTable(int tableNo);
    List<Order> findByStatus(Order.OrderStatus status);
}
