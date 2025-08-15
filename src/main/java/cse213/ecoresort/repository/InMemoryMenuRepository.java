package cse213.ecoresort.repository;

import cse213.ecoresort.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMenuRepository implements MenuRepository {
    private static InMemoryMenuRepository instance;
    private final Map<String, MenuItem> items;

    private InMemoryMenuRepository() {
        items = new ConcurrentHashMap<>();
        initializeSampleData();
    }

    public static synchronized InMemoryMenuRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryMenuRepository();
        }
        return instance;
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public MenuItem save(MenuItem item) {
        if (item.getId() == null) {
            throw new IllegalArgumentException("Item must have an ID");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    @Override
    public List<MenuItem> findByType(MenuItem.ItemType type) {
        return items.values().stream()
                .filter(item -> item.getType() == type)
                .toList();
    }

    @Override
    public List<MenuItem> findAvailable() {
        return items.values().stream()
                .filter(MenuItem::isAvailable)
                .toList();
    }

    private void initializeSampleData() {
        // Sample food items
        FoodItem burger = new FoodItem("Classic Burger", 12.99, 50, "American", false);
        FoodItem salad = new FoodItem("Garden Salad", 8.99, 30, "International", true);
        FoodItem pasta = new FoodItem("Pasta Carbonara", 14.99, 25, "Italian", false);
        
        // Sample drink items
        DrinkItem coffee = new DrinkItem("Espresso", 3.99, 100, false, "Hot");
        DrinkItem beer = new DrinkItem("Craft Beer", 6.99, 40, true, "Cold");
        DrinkItem juice = new DrinkItem("Orange Juice", 4.99, 60, false, "Cold");

        // Save all items
        save(burger);
        save(salad);
        save(pasta);
        save(coffee);
        save(beer);
        save(juice);
    }
}
