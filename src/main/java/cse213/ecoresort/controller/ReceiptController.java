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
        setupOrderItemsTable();
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
        this.order = order;
        updateDisplay();
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
