package cse213.ecoresort.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private void handleNewOrder() {
        System.out.println("DEBUG: handleNewOrder() called - attempting to open Order.fxml");
        
        try {
            System.out.println("DEBUG: Creating FXMLLoader for Order.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Order.fxml"));
            
            if (loader.getLocation() == null) {
                System.err.println("ERROR: FXML resource not found! Resource path: /cse213/ecoresort/view/Order.fxml");
                showError("FXML Error", "FXML resource not found", "Could not locate Order.fxml resource");
                return;
            }
            
            System.out.println("DEBUG: FXML resource found at: " + loader.getLocation());
            System.out.println("DEBUG: Loading FXML...");
            
            Parent root = loader.load();
            System.out.println("DEBUG: FXML loaded successfully");
            
            System.out.println("DEBUG: Creating new stage for Order");
            Stage stage = new Stage();
            stage.setTitle("New Order - Eco-Resort");
            stage.setScene(new Scene(root, 1000, 700));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            System.out.println("DEBUG: Showing Order stage");
            stage.show();
            System.out.println("DEBUG: Order stage displayed successfully");
            
        } catch (IOException e) {
            System.err.println("ERROR: IOException while loading Order.fxml: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Could not open Order screen", e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error while loading Order.fxml: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Unexpected error", e.getMessage());
        }
    }

    @FXML
    private void handleMenuManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/MenuManager.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Menu Manager - Eco-Resort");
            stage.setScene(new Scene(root, 900, 600));
            stage.setMinWidth(700);
            stage.setMinHeight(500);
            stage.show();
            
        } catch (IOException e) {
            showError("Error", "Could not open Menu Manager screen", e.getMessage());
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Eco-Resort");
        alert.setHeaderText("Eco-Resort Management System");
        alert.setContentText(
            "A JavaFX application demonstrating Object-Oriented Programming principles:\n\n" +
            "• Encapsulation: Private fields with controlled access\n" +
            "• Abstraction: Interfaces hiding implementation details\n" +
            "• Inheritance: MenuItem → FoodItem/DrinkItem\n" +
            "• Polymorphism: Payment methods and discount strategies\n\n" +
            "Built with JavaFX, FXML, and Maven\n" +
            "JDK 17, JavaFX 21"
        );
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
