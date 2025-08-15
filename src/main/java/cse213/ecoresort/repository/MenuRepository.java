package cse213.ecoresort.repository;

import cse213.ecoresort.model.MenuItem;
import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    List<MenuItem> findAll();
    Optional<MenuItem> findById(String id);
    MenuItem save(MenuItem item);
    boolean delete(String id);
    List<MenuItem> findByType(MenuItem.ItemType type);
    List<MenuItem> findAvailable();
}
