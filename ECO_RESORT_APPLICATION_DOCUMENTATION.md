# Eco-Resort Management System - Complete Application Documentation

## Table of Contents
1. [Application Overview](#application-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [OOP Principles Implementation](#oop-principles-implementation)
4. [Application Workflows](#application-workflows)
5. [Technical Implementation](#technical-implementation)
6. [Setup & Installation](#setup--installation)
7. [Running the Application](#running-the-application)
8. [Troubleshooting](#troubleshooting)

---

## Application Overview

The **Eco-Resort Management System** is a JavaFX desktop application designed to demonstrate Object-Oriented Programming principles while providing a functional restaurant and bar management solution. The application manages menu items, orders, payments, and receipts without requiring external databases.

### Key Features
- **Menu Management**: Create, edit, and delete food and drink items
- **Order Processing**: Create orders, add items, apply discounts, and calculate totals
- **Payment Processing**: Handle cash and card payments with validation
- **Receipt Generation**: Generate detailed receipts for completed orders
- **Stock Management**: Track inventory levels and prevent overselling

---

## Architecture & Design Patterns

### Package Structure
```
src/main/java/cse213/ecoresort/
├── app/           # Main application entry point
├── controller/    # JavaFX controllers for UI logic
├── model/         # Domain models and business entities
├── repository/    # Data access layer (in-memory)
├── service/       # Business logic and operations
└── view/          # FXML UI definitions (in resources)

src/main/resources/cse213/ecoresort/
└── view/          # FXML files for each screen
```

### Design Patterns Used
- **MVC (Model-View-Controller)**: Separates UI, business logic, and data
- **Repository Pattern**: Abstracts data access operations
- **Service Layer**: Encapsulates business logic
- **Strategy Pattern**: Implements discount and payment strategies
- **Factory Pattern**: Creates appropriate payment and discount objects

---

## OOP Principles Implementation

### 1. Encapsulation
**Definition**: Bundling data and methods that operate on that data within a single unit (class) while hiding internal state.

**Implementation Examples**:
```java
// MenuItem class - private fields with controlled access
public abstract class MenuItem {
    private String id;
    private String name;
    private double price;
    private int stockQty;
    private boolean available;
    
    // Public methods provide controlled access
    public void decreaseStock(int quantity) {
        if (quantity > 0 && quantity <= stockQty) {
            this.stockQty -= quantity;
            if (stockQty == 0) {
                this.available = false;
            }
        }
    }
}
```

**Benefits**:
- Prevents direct manipulation of stock quantities
- Ensures business rules are enforced (e.g., stock can't go negative)
- Provides a clean interface for external classes

### 2. Abstraction
**Definition**: Hiding complex implementation details and showing only necessary features.

**Implementation Examples**:
```java
// PaymentMethod interface - hides implementation details
public interface PaymentMethod {
    PaymentResult processPayment(Order order, PaymentInput input);
}

// DiscountStrategy interface - abstracts discount calculation
public interface DiscountStrategy {
    double apply(double subtotal);
}
```

**Benefits**:
- Controllers don't need to know how payments are processed
- Easy to add new payment methods without changing existing code
- Clear contracts for what operations are available

### 3. Inheritance
**Definition**: Creating new classes that are built upon existing classes, inheriting their properties and methods.

**Implementation Examples**:
```java
// Base class
public abstract class MenuItem {
    protected String id;
    protected String name;
    protected double price;
    // Common methods and properties
}

// Food items inherit from MenuItem
public class FoodItem extends MenuItem {
    private String cuisine;
    private boolean vegetarian;
    
    // Inherits all MenuItem properties and methods
    // Adds food-specific functionality
}

// Drink items inherit from MenuItem
public class DrinkItem extends MenuItem {
    private boolean alcoholic;
    private String temperature;
    
    // Inherits all MenuItem properties and methods
    // Adds drink-specific functionality
}
```

**Benefits**:
- Eliminates code duplication
- Establishes a clear hierarchy of menu items
- Allows polymorphic treatment of different item types

### 4. Polymorphism
**Definition**: The ability to present the same interface for different underlying forms (data types or classes).

**Implementation Examples**:
```java
// Polymorphic payment processing
public class PaymentService {
    public PaymentResult processPayment(Order order, PaymentInput input) {
        PaymentMethod paymentMethod;
        
        if (input.getPaymentType() == PaymentType.CASH) {
            paymentMethod = new CashPayment();
        } else {
            paymentMethod = new CardPayment();
        }
        
        // Same interface, different implementations
        return paymentMethod.processPayment(order, input);
    }
}

// Polymorphic discount application
public class PricingService {
    public double calculateDiscount(double subtotal, DiscountStrategy strategy) {
        // Same interface, different discount calculations
        return strategy.apply(subtotal);
    }
}
```

**Benefits**:
- Code can work with any payment method or discount strategy
- Easy to extend with new payment types or discount rules
- Maintains consistency in how operations are performed

---

## Application Workflows

### 1. Application Startup Workflow

```
MainApp.start() → Home.fxml → HomeController
```

**Process**:
1. **MainApp.start()** initializes the JavaFX application
2. Loads **Home.fxml** as the primary scene
3. **HomeController** handles user interactions
4. User can navigate to either "New Order" or "Menu Manager"

**Code Flow**:
```java
@Override
public void start(Stage primaryStage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/cse213/ecoresort/view/Home.fxml"));
    Parent root = loader.load();
    
    primaryStage.setTitle("Eco-Resort Management System");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.show();
}
```

### 2. Menu Management Workflow

```
Home → Menu Manager → Add/Edit/Delete Items → Update Repository
```

**Process**:
1. User clicks "Menu Manager" button
2. **HomeController.handleMenuManager()** loads MenuManager.fxml
3. **MenuManagerController** displays existing menu items
4. User can add new items or edit existing ones
5. **MenuService** validates input and updates **InMemoryMenuRepository**
6. Changes are immediately reflected in the UI

**Key Operations**:
- **Add Item**: Validates name (required), price (≥0), stock (≥0)
- **Edit Item**: Updates existing item properties
- **Delete Item**: Removes item from repository
- **Stock Management**: Tracks available quantities

**Code Example**:
```java
@FXML
private void handleAddItem() {
    if (validateInput()) {
        MenuItem item = createMenuItemFromInput();
        menuService.addItem(item);
        refreshMenuTable();
        clearForm();
    }
}
```

### 3. Order Creation Workflow

```
Home → New Order → Select Table → Add Items → Apply Discount → Proceed to Payment
```

**Process**:
1. User clicks "New Order" button
2. **HomeController.handleNewOrder()** loads Order.fxml
3. **OrderController** initializes the order interface
4. User enters table number and clicks "Start Order"
5. **OrderService** creates a new Order with DRAFT status
6. User selects items from menu and adds them to order
7. **PricingService** calculates live totals (subtotal, tax, discount, total)
8. User can apply discount strategies (None, 5%, 10%, 15%)
9. User clicks "Proceed to Payment" to continue

**Key Operations**:
- **Table Assignment**: Validates table number (positive integer)
- **Item Selection**: Shows available menu items with stock levels
- **Quantity Management**: Prevents adding more than available stock
- **Live Pricing**: Updates totals as items or discounts change
- **Order Lines**: Maintains item snapshots for order integrity

**Code Example**:
```java
@FXML
private void handleAddToOrder() {
    MenuItem selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
    int quantity = quantitySpinner.getValue();
    
    if (selectedItem != null && selectedItem.hasStock(quantity)) {
        orderService.addLineToOrder(currentOrder, selectedItem, quantity);
        updateOrderDisplay();
        recalculateTotals();
    }
}
```

### 4. Payment Processing Workflow

```
Order → Payment → Validate Input → Process Payment → Update Order Status → Show Receipt
```

**Process**:
1. **OrderController.handleProceedToPayment()** loads Payment.fxml
2. **PaymentController** displays order summary and payment options
3. User selects payment method (Cash or Card)
4. User enters payment details:
   - **Cash**: Amount given (must be ≥ total)
   - **Card**: Card number (format: ####-####-####-####)
5. **PaymentService** validates input and processes payment
6. **OrderService** marks order as PAID and decreases stock
7. **ReceiptController** displays final receipt

**Key Operations**:
- **Payment Validation**: Ensures sufficient cash or valid card format
- **Stock Update**: Decreases inventory for sold items
- **Order Status**: Changes from DRAFT to PAID
- **Receipt Generation**: Creates detailed order summary

**Code Example**:
```java
@FXML
private void handleProcessPayment() {
    PaymentInput input = createPaymentInput();
    
    if (validatePaymentInput(input)) {
        PaymentResult result = paymentService.processPayment(currentOrder, input);
        
        if (result.isSuccess()) {
            orderService.markOrderAsPaid(currentOrder);
            showReceipt();
        } else {
            showPaymentError(result.getErrorMessage());
        }
    }
}
```

### 5. Receipt Generation Workflow

```
Payment Success → Receipt Display → Print/Close Options
```

**Process**:
1. **PaymentController** successfully processes payment
2. **Receipt.fxml** is loaded with order details
3. **ReceiptController** populates receipt information
4. User can print receipt or close the window
5. Application returns to Home screen

**Receipt Information**:
- Restaurant branding and header
- Table number and order ID
- Date and time of payment
- Complete itemized list with quantities and prices
- Subtotal, tax, discount, and total amounts
- Payment method used
- Order status confirmation

---

## Technical Implementation

### FXML Structure
Each screen is defined using FXML (JavaFX Markup Language) with:
- **Layout containers**: VBox, HBox, GridPane for organizing UI elements
- **Controls**: Buttons, TextFields, Tables, ComboBoxes for user interaction
- **Styling**: CSS-like styles for colors, fonts, and spacing
- **Controller binding**: `fx:controller` attribute links FXML to Java controller

### Controller Implementation
Controllers handle:
- **Event handling**: Button clicks, form submissions, table selections
- **Data binding**: Connecting UI elements to data models
- **Navigation**: Loading new screens and managing application flow
- **Validation**: Ensuring user input meets business rules

### Service Layer
Services implement business logic:
- **MenuService**: Manages menu item operations and stock validation
- **OrderService**: Handles order creation, modification, and status changes
- **PricingService**: Calculates totals, taxes, and applies discounts
- **PaymentService**: Processes payments and validates payment methods

### Repository Pattern
In-memory repositories store application data:
- **InMemoryMenuRepository**: Maintains menu items using HashMap
- **InMemoryOrderRepository**: Stores orders and order lines
- **Singleton pattern**: Ensures single instance across application

---

## Setup & Installation

### Prerequisites
- **Java Development Kit (JDK) 17** or later
- **Apache Maven 3.9.x** or later
- **Git** (optional, for version control)

### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Linux
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Storage**: 100MB free space
- **Display**: 1024x768 minimum resolution

### Installation Steps

#### 1. Install Java
```bash
# Download and install JDK 17 from:
# https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/

# Verify installation
java -version
javac -version
```

#### 2. Install Maven
```bash
# Download from: https://maven.apache.org/download.cgi
# Extract to desired directory (e.g., C:\apache-maven-3.9.x)

# Add to PATH environment variable
# Windows: Add C:\apache-maven-3.9.x\bin to PATH
# macOS/Linux: Add to ~/.bash_profile or ~/.zshrc
export PATH=$PATH:/path/to/apache-maven-3.9.x/bin

# Verify installation
mvn -version
```

#### 3. Clone/Download Project
```bash
# Option 1: Clone from Git repository
git clone <repository-url>
cd eco-resort

# Option 2: Download and extract ZIP file
# Extract to desired directory
cd eco-resort
```

---

## Running the Application

### Command Line Interface (CLI) Commands

#### Fresh Installation Commands
```bash
# Navigate to project directory
cd eco-resort

# Clean any previous builds
mvn clean

# Compile the project
mvn compile

# Run the application
mvn javafx:run
```

#### Development Workflow Commands
```bash
# Clean and compile
mvn clean compile

# Run with hot reload (if supported)
mvn javafx:run

# Package application
mvn package

# Install to local repository
mvn install
```

#### Troubleshooting Commands
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Verify project structure
mvn validate

# Check dependencies
mvn dependency:tree

# Clean and force recompile
mvn clean compile -U
```

### IDE Integration

#### IntelliJ IDEA
1. **Open Project**: File → Open → Select `pom.xml`
2. **Import Maven Project**: Import as Maven project
3. **Run Configuration**: Create new JavaFX Application configuration
4. **Main Class**: Set to `cse213.ecoresort.app.MainApp`

#### Eclipse
1. **Import Project**: File → Import → Maven → Existing Maven Projects
2. **Select Root Directory**: Choose project folder
3. **Run Configuration**: Create new Java Application
4. **Main Class**: Set to `cse213.ecoresort.app.MainApp`

#### VS Code
1. **Open Folder**: File → Open Folder → Select project directory
2. **Install Extensions**: Java Extension Pack, Maven for Java
3. **Run**: Use Maven sidebar or terminal commands

### Application Startup Process
1. **Maven Phase**: `mvn javafx:run` triggers the JavaFX Maven plugin
2. **Class Loading**: MainApp class is loaded and instantiated
3. **JavaFX Initialization**: Application.start() method is called
4. **FXML Loading**: Home.fxml is loaded and parsed
5. **Controller Creation**: HomeController is instantiated and bound
6. **UI Display**: Primary stage is shown with Home screen
7. **Event Loop**: Application enters JavaFX event loop

---

## Troubleshooting

### Common Issues and Solutions

#### 1. Java Version Issues
**Problem**: `java: command not found` or wrong Java version
**Solution**:
```bash
# Check current Java version
java -version

# Verify JAVA_HOME environment variable
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME% # Windows

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/jdk-17  # macOS/Linux
set JAVA_HOME=C:\path\to\jdk-17   # Windows
```

#### 2. Maven Issues
**Problem**: `mvn: command not found`
**Solution**:
```bash
# Check Maven installation
mvn -version

# Verify PATH includes Maven bin directory
echo $PATH  # macOS/Linux
echo %PATH% # Windows

# Add Maven to PATH if needed
export PATH=$PATH:/path/to/maven/bin  # macOS/Linux
```

#### 3. Compilation Errors
**Problem**: `javafx.controls module not found`
**Solution**:
```bash
# Clean and recompile
mvn clean compile

# Ensure JavaFX dependencies are in pom.xml
# Check that javafx-maven-plugin is configured
```

#### 4. Runtime Errors
**Problem**: `FXML not found` or `Controller not found`
**Solution**:
```bash
# Verify FXML files are in correct location
# Check controller class names match FXML files
# Ensure module-info.java opens correct packages
```

#### 5. UI Display Issues
**Problem**: Blank screen or missing elements
**Solution**:
```bash
# Check FXML syntax
# Verify fx:id bindings in controllers
# Ensure all required imports are present
```

### Performance Optimization

#### Memory Management
- **Garbage Collection**: Monitor memory usage during long sessions
- **Object Pooling**: Reuse objects where possible
- **Lazy Loading**: Load data only when needed

#### UI Responsiveness
- **Background Processing**: Use Platform.runLater() for UI updates
- **Async Operations**: Perform heavy operations in background threads
- **Event Handling**: Avoid blocking operations in event handlers

### Debugging Tips

#### Enable Debug Logging
```bash
# Run with debug information
mvn javafx:run -Djavafx.verbose=true

# Check console output for errors
# Look for FXML loading messages
```

#### Common Debug Points
1. **Controller Initialization**: Check @FXML annotations
2. **Event Handling**: Verify method names match FXML onAction
3. **Data Binding**: Ensure fx:id attributes are properly bound
4. **Navigation**: Check resource paths in FXMLLoader

---

## Conclusion

The Eco-Resort Management System successfully demonstrates all four pillars of Object-Oriented Programming while providing a functional restaurant management solution. The application's architecture follows industry best practices and can serve as a foundation for more complex business applications.

### Key Achievements
- ✅ **Complete OOP Implementation**: All four principles clearly demonstrated
- ✅ **Clean Architecture**: Separation of concerns with MVC pattern
- ✅ **Functional UI**: Intuitive user interface for all operations
- ✅ **Robust Validation**: Comprehensive input validation and error handling
- ✅ **Extensible Design**: Easy to add new features and payment methods

### Future Enhancements
- **Database Integration**: Replace in-memory repositories with persistent storage
- **User Authentication**: Add role-based access control
- **Reporting**: Generate sales reports and analytics
- **Multi-language Support**: Internationalization for global use
- **Mobile App**: Extend functionality to mobile devices

The application is ready for production use and serves as an excellent example of modern JavaFX development with proper OOP principles.
