package cse213.ecoresort.service;

import cse213.ecoresort.model.*;
import cse213.ecoresort.repository.MenuRepository;
import cse213.ecoresort.repository.InMemoryMenuRepository;

import java.util.List;
import java.util.Optional;

public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService() {
        this.menuRepository = InMemoryMenuRepository.getInstance();
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public List<MenuItem> getAvailableItems() {
        return menuRepository.findAvailable();
    }

    public List<MenuItem> getItemsByType(MenuItem.ItemType type) {
        return menuRepository.findByType(type);
    }

    public Optional<MenuItem> getItemById(String id) {
        return menuRepository.findById(id);
    }

    public MenuItem createFoodItem(String name, double price, int stockQty, String cuisine, boolean isVegetarian) {
        validateMenuItemData(name, price, stockQty);
        
        FoodItem item = new FoodItem(name, price, stockQty, cuisine, isVegetarian);
        return menuRepository.save(item);
    }

    public MenuItem createDrinkItem(String name, double price, int stockQty, boolean isAlcoholic, String temperature) {
        validateMenuItemData(name, price, stockQty);
        
        DrinkItem item = new DrinkItem(name, price, stockQty, isAlcoholic, temperature);
        return menuRepository.save(item);
    }

    public MenuItem updateMenuItem(String id, String name, double price, int stockQty) {
        Optional<MenuItem> existing = menuRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Menu item not found with ID: " + id);
        }

        validateMenuItemData(name, price, stockQty);
        
        MenuItem item = existing.get();
        item.setName(name);
        item.setPrice(price);
        item.setStockQty(stockQty);
        
        return menuRepository.save(item);
    }

    public boolean deleteMenuItem(String id) {
        return menuRepository.delete(id);
    }

    public MenuItem save(MenuItem item) {
        return menuRepository.save(item);
    }

    public boolean hasStock(String itemId, int quantity) {
        Optional<MenuItem> item = menuRepository.findById(itemId);
        return item.isPresent() && item.get().hasStock(quantity);
    }

    public boolean decreaseStock(String itemId, int quantity) {
        Optional<MenuItem> item = menuRepository.findById(itemId);
        if (item.isPresent()) {
            return item.get().decreaseStock(quantity);
        }
        return false;
    }

    private void validateMenuItemData(String name, double price, int stockQty) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (stockQty < 0) {
            throw new IllegalArgumentException("Stock quantity must be non-negative");
        }
    }
}
