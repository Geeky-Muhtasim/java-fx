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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Order.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("New Order - Eco-Resort");
            stage.setScene(new Scene(root, 1000, 700));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
            
        } catch (IOException e) {
            showError("Error", "Could not open Order screen", e.getMessage());
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
