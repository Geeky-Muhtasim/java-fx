package cse213.ecoresort.controller;

import cse213.ecoresort.model.MenuItem;
import cse213.ecoresort.model.Order;
import cse213.ecoresort.model.OrderLine;
import cse213.ecoresort.service.MenuService;
import cse213.ecoresort.service.OrderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class OrderController {
    
    @FXML private TextField tableNumberField;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private ComboBox<String> discountComboBox;
    
    @FXML private TableView<MenuItem> menuItemsTable;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, String> typeColumn;
    @FXML private TableColumn<MenuItem, String> priceColumn;
    @FXML private TableColumn<MenuItem, String> stockColumn;
    
    @FXML private TableView<OrderLine> orderLinesTable;
    @FXML private TableColumn<OrderLine, String> itemNameColumn;
    @FXML private TableColumn<OrderLine, String> itemPriceColumn;
    @FXML private TableColumn<OrderLine, String> itemQuantityColumn;
    @FXML private TableColumn<OrderLine, String> lineTotalColumn;
    @FXML private TableColumn<OrderLine, String> actionsColumn;
    
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    
    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();
    private Order currentOrder;
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<OrderLine> orderLines = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        System.out.println("DEBUG: OrderController.initialize() called");
        
        try {
            System.out.println("DEBUG: Setting up quantity spinner");
            setupQuantitySpinner();
            System.out.println("DEBUG: Quantity spinner setup complete");
            
            System.out.println("DEBUG: Setting up discount combo box");
            setupDiscountComboBox();
            System.out.println("DEBUG: Discount combo box setup complete");
            
            System.out.println("DEBUG: Setting up menu items table");
            setupMenuItemsTable();
            System.out.println("DEBUG: Menu items table setup complete");
            
            System.out.println("DEBUG: Setting up order lines table");
            setupOrderLinesTable();
            System.out.println("DEBUG: Order lines table setup complete");
            
            System.out.println("DEBUG: Loading menu items");
            loadMenuItems();
            System.out.println("DEBUG: Menu items loaded: " + menuItems.size() + " items");
            
            System.out.println("DEBUG: OrderController initialization complete");
        } catch (Exception e) {
            System.err.println("ERROR: Exception during OrderController initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupQuantitySpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);
    }
    
    private void setupDiscountComboBox() {
        discountComboBox.getItems().addAll("No Discount", "5%", "10%", "15%");
        discountComboBox.setValue("No Discount");
    }
    
    private void setupMenuItemsTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getPrice())));
        stockColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getStockQty())));
        
        menuItemsTable.setItems(menuItems);
        menuItemsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupOrderLinesTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemPriceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getUnitPrice())));
        itemQuantityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        lineTotalColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getLineTotal())));
        
        // Actions column with Remove button
        actionsColumn.setCellFactory(createRemoveButtonCellFactory());
        
        orderLinesTable.setItems(orderLines);
    }
    
    private Callback<TableColumn<OrderLine, String>, TableCell<OrderLine, String>> createRemoveButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    OrderLine line = getTableView().getItems().get(getIndex());
                    handleRemoveOrderLine(line);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        };
    }
    
    private void loadMenuItems() {
        menuItems.clear();
        menuItems.addAll(menuService.getAvailableItems());
    }
    
    @FXML
    private void handleStartOrder() {
        try {
            int tableNo = Integer.parseInt(tableNumberField.getText().trim());
            if (tableNo <= 0) {
                showAlert("Invalid Table Number", "Table number must be a positive integer.");
                return;
            }
            
            currentOrder = orderService.createOrder(tableNo);
            updateOrderDisplay();
            showAlert("Order Started", "Order created for table " + tableNo);
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid table number.");
        }
    }
    
    @FXML
    private void handleAddToOrder() {
        if (currentOrder == null) {
            showAlert("No Order", "Please start an order first.");
            return;
        }
        
        MenuItem selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a menu item.");
            return;
        }
        
        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            showAlert("Invalid Quantity", "Quantity must be positive.");
            return;
        }
        
        if (!selectedItem.hasStock(quantity)) {
            showAlert("Insufficient Stock", "Not enough stock available for this quantity.");
            return;
        }
        
        boolean added = orderService.addItemToOrder(currentOrder.getId(), selectedItem.getId(), quantity);
        if (added) {
            loadOrderLines();
            updateOrderDisplay();
            showAlert("Item Added", selectedItem.getName() + " added to order.");
        } else {
            showAlert("Error", "Failed to add item to order.");
        }
    }
    
    private void handleRemoveOrderLine(OrderLine line) {
        if (currentOrder == null) return;
        
        boolean removed = orderService.removeItemFromOrder(currentOrder.getId(), line.getItemId());
        if (removed) {
            loadOrderLines();
            updateOrderDisplay();
        }
    }
    
    @FXML
    private void handleApplyDiscount() {
        if (currentOrder == null) {
            showAlert("No Order", "Please start an order first.");
            return;
        }
        
        String selectedDiscount = discountComboBox.getValue();
        if (selectedDiscount == null || selectedDiscount.equals("No Discount")) {
            currentOrder.setDiscount(0.0);
        } else {
            double percentage = Double.parseDouble(selectedDiscount.replace("%", ""));
            orderService.applyDiscount(currentOrder.getId(), percentage);
        }
        
        updateOrderDisplay();
    }
    
    @FXML
    private void handleCancelOrder() {
        if (currentOrder != null) {
            orderService.deleteOrder(currentOrder.getId());
            currentOrder = null;
            orderLines.clear();
            updateOrderDisplay();
            showAlert("Order Cancelled", "Order has been cancelled.");
        }
    }
    
    @FXML
    private void handleProceedToPayment() {
        System.out.println("DEBUG: handleProceedToPayment() called");
        
        if (currentOrder == null || currentOrder.getOrderLines().isEmpty()) {
            System.out.println("DEBUG: No order or empty order - showing alert");
            showAlert("No Order", "Please create an order with items first.");
            return;
        }
        
        System.out.println("DEBUG: Order validation passed, proceeding to payment");
        System.out.println("DEBUG: Current order ID: " + currentOrder.getId());
        System.out.println("DEBUG: Order lines count: " + currentOrder.getOrderLines().size());
        
        try {
            System.out.println("DEBUG: Creating FXMLLoader for Payment.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Payment.fxml"));
            
            if (loader.getLocation() == null) {
                System.err.println("ERROR: Payment.fxml resource not found!");
                showAlert("FXML Error", "Payment.fxml not found - Could not locate Payment.fxml resource");
                return;
            }
            
            System.out.println("DEBUG: Payment.fxml resource found at: " + loader.getLocation());
            System.out.println("DEBUG: Loading Payment.fxml...");
            
            Parent root = loader.load();
            System.out.println("DEBUG: Payment.fxml loaded successfully");
            
            System.out.println("DEBUG: Getting PaymentController instance");
            PaymentController paymentController = loader.getController();
            if (paymentController == null) {
                System.err.println("ERROR: PaymentController is null!");
                showAlert("Controller Error", "PaymentController not found - Could not get PaymentController instance");
                return;
            }
            
            System.out.println("DEBUG: Setting order in PaymentController");
            paymentController.setOrder(currentOrder);
            System.out.println("DEBUG: Order set successfully");
            
            System.out.println("DEBUG: Creating Payment stage");
            Stage stage = new Stage();
            stage.setTitle("Payment - Eco-Resort");
            stage.setScene(new Scene(root, 600, 500));
            
            System.out.println("DEBUG: Showing Payment stage");
            stage.show();
            System.out.println("DEBUG: Payment stage displayed successfully");
            
            // Close the order window
            System.out.println("DEBUG: Closing Order window");
            ((Stage) tableNumberField.getScene().getWindow()).close();
            System.out.println("DEBUG: Order window closed");
            
        } catch (IOException e) {
            System.err.println("ERROR: IOException while loading Payment.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Could not open payment screen: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error while loading Payment.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackToHome() {
        if (currentOrder != null && !currentOrder.isPaid()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Exit");
            alert.setHeaderText("Order in Progress");
            alert.setContentText("You have an order in progress. Are you sure you want to exit?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                orderService.deleteOrder(currentOrder.getId());
            }
        }
        
        ((Stage) tableNumberField.getScene().getWindow()).close();
    }
    
    private void loadOrderLines() {
        if (currentOrder != null) {
            orderLines.clear();
            orderLines.addAll(currentOrder.getOrderLines());
        }
    }
    
    private void updateOrderDisplay() {
        if (currentOrder != null) {
            subtotalLabel.setText(String.format("$%.2f", currentOrder.getSubtotal()));
            taxLabel.setText(String.format("$%.2f", currentOrder.getTax()));
            discountLabel.setText(String.format("$%.2f", currentOrder.getDiscount()));
            totalLabel.setText(String.format("$%.2f", currentOrder.getTotal()));
        } else {
            subtotalLabel.setText("$0.00");
            taxLabel.setText("$0.00");
            discountLabel.setText("$0.00");
            totalLabel.setText("$0.00");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
