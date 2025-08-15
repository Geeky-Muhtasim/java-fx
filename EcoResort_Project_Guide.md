# Eco-Resort (JavaFX + FXML) — Requirement Analysis & Project Setup (Cursor + Maven, Windows)

A **simple, no-database** desktop app to demonstrate the **four pillars of OOP** using **JavaFX (FXML)**. Built with **JDK 17**, **Maven**, and **Cursor** (as the AI pair-programmer/IDE). This document includes requirement analysis, design, setup, and build/run instructions.

> **Scope constraints**
> - No external database (use in-memory repositories; you may optionally persist to JSON later).
> - Must clearly demonstrate **Encapsulation**, **Abstraction**, **Inheritance**, and **Polymorphism**.
> - Keep UI minimal and focused on core flows.


---

## 1) Project Overview

The Eco-Resort app supports basic **Restaurant & Bar** tasks suitable for a university project:

- **Menu Management** (create/update/delete items)
- **Order Taking** (assign to table, add/remove items, check stock)
- **Pricing** (subtotal, tax, optional discount)
- **Payment Simulation** (cash/card with simple validation)
- **Receipt View** (basic summary after payment)

The emphasis is on **clean OOP design** + **JavaFX (FXML) UI**.

---

## 2) Requirement Analysis

### 2.1 Actors
- **Staff** (single role is enough)

### 2.2 Functional Requirements
1. **Menu Management**
   - Add/Edit/Delete `MenuItem` (name, price, type, stockQty, availability)
   - Validate: name required; price ≥ 0; stock ≥ 0

2. **Order Taking**
   - Start a new order; set table number (positive int)
   - Add/remove items; validate stock before adding
   - Update quantities; prevent negative or zero qty

3. **Pricing**
   - Calculate subtotal, fixed tax (e.g., 10%), discount (None or % strategy)
   - Display totals live as items/discount change

4. **Payment**
   - Choose **Cash** or **Card**
   - Cash: validate `cashGiven ≥ total`
   - Card: validate simple ####-####-####-#### mask/length
   - On success: mark order **PAID**, decrease stock, show receipt

5. **Receipt**
   - Display table number, lines, subtotal, tax, discount, total, payment method

### 2.3 Non-Functional Requirements
- **Simplicity**: short code paths, minimal UI
- **Testability**: unit tests for pricing and stock checks
- **Separation of Concerns**: models, services, repositories, controllers
- **Reliability**: safe validations; informative errors
- **Performance**: responsive UI (no external IO)

---

## 3) OOP Design (Four Pillars Explicit)

### 3.1 Encapsulation
- Private fields with getters/setters where appropriate
- Domain methods enforce invariants (e.g., `decreaseStock(qty)` prevents negatives)
- Controllers do not manipulate model internals directly; they call services

### 3.2 Abstraction
- **Interfaces** hide implementation details:
  - `PaymentMethod` → `processPayment(Order, PaymentInput) : PaymentResult`
  - `DiscountStrategy` → `apply(subtotal) : discountAmount`
  - Optional generic `Repository<T>` with `findAll/create/update/delete`

### 3.3 Inheritance
- `MenuItem` (abstract/base) → `FoodItem`, `DrinkItem`
- (Optional) `User` → `Staff`

### 3.4 Polymorphism
- `PaymentMethod` variable can reference `CashPayment` or `CardPayment`
- `DiscountStrategy` variable can reference `NoDiscount` or `PercentageDiscount`
- Optional: `MenuItem` subclasses override a pricing detail if needed

### 3.5 Domain Model (Lightweight)
- **MenuItem (abstract)**: `id`, `name`, `price`, `stockQty`, `available`  
  Methods: `decreaseStock(qty)`, `increaseStock(qty)`, `isAvailable()`
- **FoodItem / DrinkItem** (inherit `MenuItem`)
- **Order**: `id`, `tableNo`, `status (DRAFT|PAID)`, `List<OrderLine>`, `totals`  
  Methods: `addLine(item, qty)`, `removeLine(itemId)`, `recalculate(pricingService)`
- **OrderLine**: item snapshot (name, unitPrice), `qty`, `lineTotal`
- **DiscountStrategy**: `NoDiscount`, `PercentageDiscount`
- **PaymentMethod**: `CashPayment`, `CardPayment`
- **Repositories (in-memory)**: `MenuRepository`, `OrderRepository`
- **Services**: `MenuService`, `OrderService`, `PricingService`, `PaymentService`

---

## 4) UI Plan (FXML)

- **`Home.fxml`**
  - Buttons: “New Order”, “Menu Manager”, “About”

- **`Order.fxml`**
  - Inputs: Table No
  - Menu list/table, quantity spinner, “Add to Order”
  - Order lines table with remove/edit qty
  - Discount selector (None / 5% / 10% / 15%)
  - Totals area (subtotal, tax, discount, grand total)
  - Buttons: “Proceed to Payment”, “Cancel”

- **`Payment.fxml`**
  - Summary: subtotal, tax, discount, total
  - Radio: Cash / Card
  - Cash: Cash Given input
  - Card: Card Number input (mask ####-####-####-####)
  - Button: “Pay” → validates → success → mark PAID → show receipt

- **`MenuManager.fxml`**
  - Table of items with Add/Edit/Delete
  - Form: name, price, type (Food/Drink), stockQty, available
  - Validation messages inline

- **`Receipt.fxml`** (optional)
  - Final summary with simple branding

**Controllers**: One per FXML, with clear `fx:id` bindings and event handlers.

---

## 5) Validation Rules

- Menu item: `name` required; `price ≥ 0`; `stock ≥ 0`
- Order: `tableNo > 0`; `qty ≥ 1`; cannot exceed available stock
- Discount: percentage in `[0, 100]`
- Payment:
  - Cash: `cashGiven ≥ total`
  - Card: length/mask check; numeric groups separated by `-` (simulate only)

---

## 6) Acceptance Criteria

- Can add/edit/delete menu items and see changes immediately
- Can create an order, add items within stock, and view live totals
- Changing discount updates totals correctly
- Payment enforces rules; on success, order becomes **PAID** and stock decreases
- Receipt shows correct summary

---

## 7) Package Layout

```
src/main/java/cse213/ecoresort/
  app/            # MainApp, navigation helpers
  controller/     # JavaFX controllers for each FXML
  model/          # MenuItem, FoodItem, DrinkItem, Order, OrderLine, Payment*, Discount*
  repository/     # In-memory repositories (singletons)
  service/        # MenuService, OrderService, PricingService, PaymentService
  util/           # Validators, formatters (optional)

src/main/resources/cse213/ecoresort/
  view/           # *.fxml
  style/          # CSS (optional)
```

---

## 8) Project Setup (Windows, CMD)

### 8.1 Install prerequisites
- **JDK 17 LTS** (Adoptium/Oracle)
- **Maven 3.9.x**
- **Scene Builder** (optional; recommended for editing FXML visually)

### 8.2 Verify versions (Command Prompt)
```bat
java -version
javac -version
where java
echo %JAVA_HOME%

mvn -v
where mvn
```

Expected patterns:
```
java version "17.0.x"
javac 17.0.x
C:\Program Files\Java\jdk-17\bin\java.exe
C:\Program Files\Java\jdk-17

Apache Maven 3.9.x
Java version: 17.0.x, runtime: C:\Program Files\Java\jdk-17
C:\apache-maven-3.9.x\bin\mvn.cmd
```

### 8.3 (If needed) Environment variables
- GUI: *Edit the system environment variables* → **Environment Variables…**
  - `JAVA_HOME` → `C:\Program Files\Java\jdk-17`
  - Add to **Path**: `%JAVA_HOME%\bin`
  - (Optional) `MAVEN_HOME` → `C:\apache-maven-3.9.x` and add `%MAVEN_HOME%\bin` to Path

### 8.4 Create the project shell (Maven archetype)
```bat
mkdir eco-resort && cd eco-resort
mvn -q archetype:generate -DgroupId=cse213.ecoresort -DartifactId=eco-resort ^
  -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
cd eco-resort
```

### 8.5 POM essentials (JavaFX via Maven)
Use JavaFX 21.x on JDK 17. Configure `javafx-maven-plugin` so you can run with `mvn javafx:run` (no manual `--module-path`).

Key values to include in your `pom.xml`:
- `java.version` = `17`
- `javafx.version` = `21.0.4`
- `org.openjfx:javafx-controls` and `org.openjfx:javafx-fxml` dependencies
- `javafx-maven-plugin` with `mainClass` set to:  
  `cse213.ecoresort/cse213.ecoresort.app.MainApp`  
  (Adjust if your `MainApp` package differs; ensure it matches `module-info.java`.)

> **Tip:** You can generate/edit the entire `pom.xml` via the Cursor prompt in §10.

### 8.6 Minimal module declaration
Your `module-info.java` must `requires javafx.controls` & `javafx.fxml`, and `opens` the base package to `javafx.fxml` for controller injection.

### 8.7 Build & run
From the folder containing `pom.xml`:
```bat
mvn -q clean javafx:run
```

---

## 9) Development Flow

1. **Model first**: implement `MenuItem`, `FoodItem`, `DrinkItem`, `Order`, `OrderLine`.
2. **Services**: implement `PricingService` (subtotal, tax, discount), `MenuService` (CRUD, stock checks), `OrderService` (add/remove lines), `PaymentService` with polymorphic `PaymentMethod`s.
3. **Repositories**: wire simple in-memory stores (`Map`/`List`), singleton pattern.
4. **FXML & Controllers**: build screens and bind actions → services.
5. **Validation**: centralize checks in services or utils; show errors in UI labels/alerts.
6. **Tests**: JUnit for pricing and stock validation.
7. **Polish**: simple CSS, clean labels, small About dialog.

---

## 10) Cursor Project Guideline (Prompts)

### 10.1 Master Prompt (paste into Cursor; **no code in the prompt**)
> “Create a minimal JavaFX (FXML) desktop app (JDK 17, Maven) named **eco-resort** under package `cse213.ecoresort` that demonstrates all four OOP pillars without any external database.  
> **Project structure**: Java packages `app`, `controller`, `model`, `repository`, `service`, `util`. FXML in `resources/cse213/ecoresort/view`.  
> **Build**: Modular project with `module-info.java` requiring `javafx.controls` and `javafx.fxml`, and `opens cse213.ecoresort` to `javafx.fxml`. Add Maven deps for `javafx-controls` and `javafx-fxml` (JavaFX 21.x). Configure `javafx-maven-plugin` with mainClass `cse213.ecoresort/cse213.ecoresort.app.MainApp`.  
> **Domain & OOP**:  
> - `MenuItem` (abstract): id, name, price, stockQty, available; methods to change stock and check availability.  
> - `FoodItem` and `DrinkItem` extend `MenuItem`.  
> - `Order` with `List<OrderLine>`, tableNo, status (DRAFT/PAID), and totals.  
> - `OrderLine` stores snapshots (item name & unit price) and qty.  
> - `DiscountStrategy` interface with `NoDiscount` and `PercentageDiscount`.  
> - `PaymentMethod` interface with implementations `CashPayment` and `CardPayment`; simulate validation only.  
> - In-memory repositories: `MenuRepository`, `OrderRepository` (singletons).  
> - Services: `MenuService`, `OrderService`, `PricingService`, `PaymentService`.  
> **UI (FXML)**:  
> - `Home.fxml`: buttons to New Order, Menu Manager, About.  
> - `Order.fxml`: tableNo input; list of menu items; add/remove lines; discount selector; totals; “Proceed to Payment”.  
> - `Payment.fxml`: show totals; choose Cash/Card; validate inputs; “Pay”.  
> - `MenuManager.fxml`: table of items; add/edit/delete with form validation.  
> - Optionally `Receipt.fxml` to show last paid order.  
> **Controllers**: one per FXML with clear fx:id bindings; display validation errors in labels/alerts.  
> **Behavior**: Prevent adding beyond stock. Pricing: subtotal + 10% tax (constant), minus discount. On successful payment: set order to PAID, decrement stock, show receipt.  
> **Tests**: JUnit tests for `PricingService` (with discount variants) and stock checks in services.  
> **Deliverable**: Complete Maven project skeleton with all files, ready to run with `mvn javafx:run`. Keep code concise and readable.”

### 10.2 Follow-up Prompts
- “Add input validation to the Menu Manager form (name required, price ≥ 0, stock ≥ 0) with user-friendly error messages.”
- “Hook up the discount selector in Order.fxml (None/5%/10%/15%) to recalc totals on change.”
- “Implement cash/card flows in Payment.fxml; enforce cashGiven ≥ total or card mask ####-####-####-####; show errors in the UI.”
- “Add a simple Receipt.fxml that displays table number, lines, subtotal, tax, discount, total, and payment method.”

---

## 11) Troubleshooting

- **Object cannot be converted to Parent (compile)**  
  Use explicit type with `FXMLLoader.load(...)` and import `javafx.scene.Parent`.

- **FXML not found (NPE on load)**  
  Ensure resource path begins with `/` and file is under `src/main/resources/...`:
  `/cse213/ecoresort/view/YourFile.fxml`

- **javafx.controls module not found**  
  Run via Maven plugin: `mvn javafx:run` (it wires modules for you).

- **Wrong Java version in PATH**  
  ```bat
  where java
  ```
  Make sure `%JAVA_HOME%\bin` precedes any `...\Oracle\Java\javapath` paths.

---

## 12) Quick Command Reference

```bat
:: Version checks
java -version
javac -version
where java
echo %JAVA_HOME%
mvn -v
where mvn

:: Build & run
mvn -q clean javafx:run
```

---

### ✅ You’re ready
Use the **Master Prompt** in §10.1 inside **Cursor** to generate the full project. Then run it from CMD with `mvn javafx:run`. Expand screens and logic incrementally while keeping OOP clean and visible.