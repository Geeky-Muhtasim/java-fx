package cse213.ecoresort.controller;

import cse213.ecoresort.model.Order;
import cse213.ecoresort.model.OrderLine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ReceiptController {
    
    @FXML private Label tableNumberLabel;
    @FXML private Label orderIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label statusLabel;
    
    @FXML private TableView<OrderLine> orderItemsTable;
    @FXML private TableColumn<OrderLine, String> itemNameColumn;
    @FXML private TableColumn<OrderLine, String> itemPriceColumn;
    @FXML private TableColumn<OrderLine, String> itemQuantityColumn;
    @FXML private TableColumn<OrderLine, String> itemTotalColumn;
    
    private Order order;
    private final ObservableList<OrderLine> orderItems = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        System.out.println("DEBUG: ReceiptController.initialize() called");
        
        try {
            System.out.println("DEBUG: Setting up order items table");
            setupOrderItemsTable();
            System.out.println("DEBUG: Order items table setup complete");
            
            System.out.println("DEBUG: ReceiptController initialization complete");
        } catch (Exception e) {
            System.err.println("ERROR: Exception during ReceiptController initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupOrderItemsTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemPriceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getUnitPrice())));
        itemQuantityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        itemTotalColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getLineTotal())));
        
        orderItemsTable.setItems(orderItems);
    }
    
    public void setOrder(Order order) {
        System.out.println("DEBUG: ReceiptController.setOrder() called");
        System.out.println("DEBUG: Order parameter: " + (order != null ? "not null" : "null"));
        
        if (order == null) {
            System.err.println("ERROR: Order is null in setOrder method");
            return;
        }
        
        System.out.println("DEBUG: Order details - ID: " + order.getId() + ", Table: " + order.getTableNo());
        System.out.println("DEBUG: Order lines count: " + order.getOrderLines().size());
        
        this.order = order;
        System.out.println("DEBUG: Order assigned to controller");
        
        System.out.println("DEBUG: Calling updateDisplay()");
        updateDisplay();
        System.out.println("DEBUG: updateDisplay() completed");
    }
    
    private void updateDisplay() {
        if (order != null) {
            tableNumberLabel.setText(String.valueOf(order.getTableNo()));
            orderIdLabel.setText(order.getId());
            dateLabel.setText(order.getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            subtotalLabel.setText(String.format("$%.2f", order.getSubtotal()));
            taxLabel.setText(String.format("$%.2f", order.getTax()));
            discountLabel.setText(String.format("$%.2f", order.getDiscount()));
            totalLabel.setText(String.format("$%.2f", order.getTotal()));
            
            statusLabel.setText(order.getStatus().toString());
            
            // Load order items
            orderItems.clear();
            orderItems.addAll(order.getOrderLines());
        }
    }
    
    @FXML
    private void handlePrint() {
        // In a real application, this would implement actual printing
        // For now, just show a message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Receipt");
        alert.setHeaderText(null);
        alert.setContentText("Receipt printing functionality would be implemented here.\n" +
                           "This could include:\n" +
                           "• Sending to printer\n" +
                           "• Saving as PDF\n" +
                           "• Email receipt");
        alert.showAndWait();
    }
    
    @FXML
    private void handleClose() {
        ((Stage) tableNumberLabel.getScene().getWindow()).close();
    }
}
