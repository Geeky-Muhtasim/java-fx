module cse213.ecoresort {
    requires javafx.controls;
    requires javafx.fxml;
    
    // opens cse213.ecoresort to javafx.fxml; // This package doesn't exist
    opens cse213.ecoresort.model to javafx.base;
    opens cse213.ecoresort.controller to javafx.fxml;
    
    // exports cse213.ecoresort; // This package doesn't exist
    exports cse213.ecoresort.app;
    exports cse213.ecoresort.controller;
    exports cse213.ecoresort.model;
    exports cse213.ecoresort.service;
}
