package cse213.ecoresort.controller;

import cse213.ecoresort.model.*;
import cse213.ecoresort.service.PaymentService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PaymentController {
    
    @FXML private Label tableNumberLabel;
    @FXML private Label orderIdLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Label cashChangeLabel;
    
    @FXML private ToggleGroup paymentMethodGroup;
    @FXML private RadioButton cashRadioButton;
    @FXML private RadioButton cardRadioButton;
    
    @FXML private VBox cashInputSection;
    @FXML private VBox cardInputSection;
    
    @FXML private TextField cashGivenField;
    @FXML private TextField cardNumberField;
    
    private Order order;
    private final PaymentService paymentService = new PaymentService();
    
    @FXML
    public void initialize() {
        setupPaymentMethodHandlers();
        setupCashInputHandler();
    }
    
    private void setupPaymentMethodHandlers() {
        cashRadioButton.setOnAction(e -> {
            cashInputSection.setVisible(true);
            cardInputSection.setVisible(false);
        });
        
        cardRadioButton.setOnAction(e -> {
            cashInputSection.setVisible(false);
            cardInputSection.setVisible(true);
        });
    }
    
    private void setupCashInputHandler() {
        cashGivenField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    double cashGiven = Double.parseDouble(newValue);
                    double change = cashGiven - order.getTotal();
                    if (change >= 0) {
                        cashChangeLabel.setText(String.format("Change: $%.2f", change));
                        cashChangeLabel.setStyle("-fx-text-fill: #4CAF50;");
                    } else {
                        cashChangeLabel.setText(String.format("Short: $%.2f", Math.abs(change)));
                        cashChangeLabel.setStyle("-fx-text-fill: #f44336;");
                    }
                } else {
                    cashChangeLabel.setText("Change: $0.00");
                    cashChangeLabel.setStyle("-fx-text-fill: #666;");
                }
            } catch (NumberFormatException ex) {
                cashChangeLabel.setText("Invalid amount");
                cashChangeLabel.setStyle("-fx-text-fill: #f44336;");
            }
        });
    }
    
    public void setOrder(Order order) {
        this.order = order;
        updateDisplay();
    }
    
    private void updateDisplay() {
        if (order != null) {
            tableNumberLabel.setText(String.valueOf(order.getTableNo()));
            orderIdLabel.setText(order.getId());
            subtotalLabel.setText(String.format("$%.2f", order.getSubtotal()));
            taxLabel.setText(String.format("$%.2f", order.getTax()));
            discountLabel.setText(String.format("$%.2f", order.getDiscount()));
            totalLabel.setText(String.format("$%.2f", order.getTotal()));
        }
    }
    
    @FXML
    private void handleProcessPayment() {
        if (order == null) {
            showAlert("Error", "No order to process.");
            return;
        }
        
        PaymentInput paymentInput;
        
        if (cashRadioButton.isSelected()) {
            try {
                double cashGiven = Double.parseDouble(cashGivenField.getText().trim());
                if (cashGiven <= 0) {
                    showAlert("Invalid Amount", "Cash amount must be positive.");
                    return;
                }
                paymentInput = PaymentInput.forCash(cashGiven);
            } catch (NumberFormatException e) {
                showAlert("Invalid Amount", "Please enter a valid cash amount.");
                return;
            }
        } else if (cardRadioButton.isSelected()) {
            String cardNumber = cardNumberField.getText().trim();
            if (cardNumber.isEmpty()) {
                showAlert("Invalid Card", "Please enter a card number.");
                return;
            }
            paymentInput = PaymentInput.forCard(cardNumber);
        } else {
            showAlert("Payment Method", "Please select a payment method.");
            return;
        }
        
        // Process payment
        PaymentResult result = paymentService.processPayment(order.getId(), paymentInput);
        
        if (result.isSuccess()) {
            showAlert("Payment Successful", result.getMessage());
            if (result.getChange() > 0) {
                showAlert("Change", String.format("Change: $%.2f", result.getChange()));
            }
            
            // Show receipt
            showReceipt();
            
            // Close payment window
            ((Stage) tableNumberLabel.getScene().getWindow()).close();
            
        } else {
            showAlert("Payment Failed", result.getMessage());
        }
    }
    
    private void showReceipt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Receipt.fxml"));
            Parent root = loader.load();
            
            ReceiptController receiptController = loader.getController();
            receiptController.setOrder(order);
            
            Stage stage = new Stage();
            stage.setTitle("Receipt - Eco-Resort");
            stage.setScene(new Scene(root, 500, 600));
            stage.show();
            
        } catch (IOException e) {
            showAlert("Error", "Could not display receipt: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        ((Stage) tableNumberLabel.getScene().getWindow()).close();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
