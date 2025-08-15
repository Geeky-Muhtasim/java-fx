package cse213.ecoresort.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Home.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Eco-Resort Management System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
