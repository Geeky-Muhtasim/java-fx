module cse213.ecoresort {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens cse213.ecoresort.controller to javafx.fxml;
    opens cse213.ecoresort.model to javafx.base;
    
    exports cse213.ecoresort.app;
    exports cse213.ecoresort.controller;
    exports cse213.ecoresort.model;
    exports cse213.ecoresort.service;
}
