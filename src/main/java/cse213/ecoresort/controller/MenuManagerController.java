package cse213.ecoresort.controller;

import cse213.ecoresort.model.MenuItem;
import cse213.ecoresort.model.FoodItem;
import cse213.ecoresort.model.DrinkItem;
import cse213.ecoresort.service.MenuService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;

public class MenuManagerController {
    
    @FXML private TableView<cse213.ecoresort.model.MenuItem> menuItemsTable;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> nameColumn;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> typeColumn;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> priceColumn;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> stockColumn;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> availableColumn;
    @FXML private TableColumn<cse213.ecoresort.model.MenuItem, String> actionsColumn;
    
    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private CheckBox availableCheckBox;
    
    @FXML private VBox foodFields;
    @FXML private TextField cuisineField;
    @FXML private CheckBox vegetarianCheckBox;
    
    @FXML private VBox drinkFields;
    @FXML private CheckBox alcoholicCheckBox;
    @FXML private ComboBox<String> temperatureComboBox;
    
    @FXML private Label validationLabel;
    
    private final MenuService menuService = new MenuService();
    private final ObservableList<cse213.ecoresort.model.MenuItem> menuItems = FXCollections.observableArrayList();
    private cse213.ecoresort.model.MenuItem selectedItem;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        setupTypeComboBox();
        setupTemperatureComboBox();
        setupMenuItemsTable();
        setupTypeChangeHandler();
        loadMenuItems();
    }
    
    private void setupTypeComboBox() {
        typeComboBox.getItems().addAll("Food", "Drink");
        typeComboBox.setValue("Food");
    }
    
    private void setupTemperatureComboBox() {
        temperatureComboBox.getItems().addAll("Hot", "Cold", "Room");
        temperatureComboBox.setValue("Cold");
    }
    
    private void setupMenuItemsTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getPrice())));
        stockColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getStockQty())));
        availableColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));
        
        // Actions column with Edit and Delete buttons
        actionsColumn.setCellFactory(createActionsCellFactory());
        
        menuItemsTable.setItems(menuItems);
        menuItemsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private Callback<TableColumn<cse213.ecoresort.model.MenuItem, String>, TableCell<cse213.ecoresort.model.MenuItem, String>> createActionsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10;");
                
                editButton.setOnAction(event -> {
                    cse213.ecoresort.model.MenuItem item = getTableView().getItems().get(getIndex());
                    handleEditItem(item);
                });
                
                deleteButton.setOnAction(event -> {
                    cse213.ecoresort.model.MenuItem item = getTableView().getItems().get(getIndex());
                    handleDeleteItem(item);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        };
    }
    
    private void setupTypeChangeHandler() {
        typeComboBox.setOnAction(e -> {
            String selectedType = typeComboBox.getValue();
            if ("Food".equals(selectedType)) {
                foodFields.setVisible(true);
                drinkFields.setVisible(false);
            } else {
                foodFields.setVisible(false);
                drinkFields.setVisible(true);
            }
        });
    }
    
    private void loadMenuItems() {
        menuItems.clear();
        menuItems.addAll(menuService.getAllMenuItems());
    }
    
    @FXML
    private void handleAddNewItem() {
        clearForm();
        isEditMode = false;
        selectedItem = null;
        validationLabel.setText("");
    }
    
    @FXML
    private void handleEditItem(cse213.ecoresort.model.MenuItem item) {
        selectedItem = item;
        isEditMode = true;
        populateForm(item);
        validationLabel.setText("");
    }
    
    private void populateForm(cse213.ecoresort.model.MenuItem item) {
        nameField.setText(item.getName());
        priceField.setText(String.format("%.2f", item.getPrice()));
        stockField.setText(String.valueOf(item.getStockQty()));
        availableCheckBox.setSelected(item.isAvailable());
        
        if (item.getType() == MenuItem.ItemType.FOOD) {
            typeComboBox.setValue("Food");
            FoodItem foodItem = (FoodItem) item;
            cuisineField.setText(foodItem.getCuisine());
            vegetarianCheckBox.setSelected(foodItem.isVegetarian());
        } else if (item.getType() == MenuItem.ItemType.DRINK) {
            typeComboBox.setValue("Drink");
            DrinkItem drinkItem = (DrinkItem) item;
            alcoholicCheckBox.setSelected(drinkItem.isAlcoholic());
            temperatureComboBox.setValue(drinkItem.getTemperature());
        }
    }
    
    @FXML
    private void handleSaveItem() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            boolean available = availableCheckBox.isSelected();
            
            cse213.ecoresort.model.MenuItem newItem;
            
            if ("Food".equals(typeComboBox.getValue())) {
                String cuisine = cuisineField.getText().trim();
                boolean vegetarian = vegetarianCheckBox.isSelected();
                newItem = new FoodItem(name, price, stock, cuisine, vegetarian);
            } else {
                boolean alcoholic = alcoholicCheckBox.isSelected();
                String temperature = temperatureComboBox.getValue();
                newItem = new DrinkItem(name, price, stock, alcoholic, temperature);
            }
            
            if (!available) {
                newItem.setStockQty(0); // Set stock to 0 if not available
            }
            
            if (isEditMode && selectedItem != null) {
                // Update existing item
                menuService.updateMenuItem(selectedItem.getId(), name, price, stock);
                showAlert("Success", "Menu item updated successfully!");
            } else {
                // Create new item
                menuService.save(newItem);
                showAlert("Success", "Menu item created successfully!");
            }
            
            loadMenuItems();
            clearForm();
            isEditMode = false;
            selectedItem = null;
            
        } catch (Exception e) {
            showAlert("Error", "Failed to save item: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errors.append("• Name is required\n");
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                errors.append("• Price must be non-negative\n");
            }
        } catch (NumberFormatException e) {
            errors.append("• Price must be a valid number\n");
        }
        
        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                errors.append("• Stock must be non-negative\n");
            }
        } catch (NumberFormatException e) {
            errors.append("• Stock must be a valid number\n");
        }
        
        if ("Food".equals(typeComboBox.getValue()) && cuisineField.getText().trim().isEmpty()) {
            errors.append("• Cuisine is required for food items\n");
        }
        
        if (errors.length() > 0) {
            validationLabel.setText(errors.toString());
            return false;
        }
        
        validationLabel.setText("");
        return true;
    }
    
    private void handleDeleteItem(cse213.ecoresort.model.MenuItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Menu Item");
        alert.setContentText("Are you sure you want to delete '" + item.getName() + "'?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = menuService.deleteMenuItem(item.getId());
            if (deleted) {
                showAlert("Success", "Menu item deleted successfully!");
                loadMenuItems();
                if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                    clearForm();
                    isEditMode = false;
                    selectedItem = null;
                }
            } else {
                showAlert("Error", "Failed to delete menu item.");
            }
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
        isEditMode = false;
        selectedItem = null;
        validationLabel.setText("");
    }
    
    private void clearForm() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        availableCheckBox.setSelected(true);
        cuisineField.clear();
        vegetarianCheckBox.setSelected(false);
        alcoholicCheckBox.setSelected(false);
        temperatureComboBox.setValue("Cold");
        typeComboBox.setValue("Food");
        foodFields.setVisible(true);
        drinkFields.setVisible(false);
    }
    
    @FXML
    private void handleBackToHome() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
