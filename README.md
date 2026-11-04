# Digital Book

A full stack enterprise inventory and logistics management web application developed using Java, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, MySQL, Thymeleaf, and Bootstrap 5.

Suitable for academic evaluations and real world business inventory tracking.

---

## Key Highlights

- **Enterprise Architecture**: Complete separation of Controller, Service, Repository, Entity, Exception, and Configuration layers.
- **Role Authentication**: Secure administrator authentication with BCrypt password hashing and session management.
- **Dashboard Analytics**: Real time stock valuation, category distribution charts, stock overview graphs, and recent transaction feeds.
- **Complete Product Catalog**: Full CRUD operations, unique SKU enforcement, multi parameter search, category filtering, and sorting.
- **Category and Supplier Management**: Full lifecycle management with relationship safety guards.
- **Stock Movement Operations**: Stock In (automatic receipt and increment) and Stock Out (validation against current stock and automatic decrement).
- **Automated Alerts**: Low stock and depletion alerts with immediate one click restock shortcuts.
- **Reporting and Exports**: Inventory valuation summaries, date range filtering, printable executive view, and CSV data export.
- **Zero Config Fallback**: Connects directly to MySQL when available on port 3306, with seamless embedded database capability for demonstration portability.

---

## Technology Stack

### Backend
- **Language**: Java (JDK 21 / 27 compatible)
- **Framework**: Spring Boot 3.3.5
- **Web MVC**: Spring MVC, Spring Validation
- **Persistence**: Spring Data JPA, Hibernate 6
- **Security**: Spring Security Crypto (BCrypt)
- **Build Tool**: Apache Maven 3.9.9

### Frontend
- **Template Engine**: Thymeleaf 3
- **CSS Framework**: Bootstrap 5.3.3
- **Typography**: Roboto (Google Fonts)
- **Iconography**: Bootstrap Icons (clean vector line icons)
- **Data Visualization**: Chart.js 4.4.2

### Database
- **Primary Database**: MySQL (Database name: `inventory_management`)
- **Dialect**: MySQLDialect
- **Script**: `database.sql` included in root folder

---

## Project Structure

```
c:\Users\marut\java prjct\
├── pom.xml                                   # Maven project object model
├── mvnw / mvnw.cmd                           # Maven wrapper scripts
├── database.sql                              # Complete MySQL database creation script
├── README.md                                 # Documentation and run instructions
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── inventory
    │   │           ├── InventoryApplication.java
    │   │           ├── config
    │   │           │   ├── AppConfig.java
    │   │           │   ├── AuthInterceptor.java
    │   │           │   ├── DataSourceConfig.java
    │   │           │   ├── DatabaseInitializer.java
    │   │           │   └── WebMvcConfig.java
    │   │           ├── controller
    │   │           │   ├── AuthController.java
    │   │           │   ├── CategoryController.java
    │   │           │   ├── CustomErrorController.java
    │   │           │   ├── DashboardController.java
    │   │           │   ├── ProductController.java
    │   │           │   ├── ReportController.java
    │   │           │   ├── StockController.java
    │   │           │   └── SupplierController.java
    │   │           ├── entity
    │   │           │   ├── Admin.java
    │   │           │   ├── Category.java
    │   │           │   ├── Product.java
    │   │           │   ├── StockTransaction.java
    │   │           │   └── Supplier.java
    │   │           ├── exception
    │   │           │   ├── DuplicateResourceException.java
    │   │           │   ├── GlobalExceptionHandler.java
    │   │           │   ├── InsufficientStockException.java
    │   │           │   └── ResourceNotFoundException.java
    │   │           ├── repository
    │   │           │   ├── AdminRepository.java
    │   │           │   ├── CategoryRepository.java
    │   │           │   ├── ProductRepository.java
    │   │           │   ├── StockTransactionRepository.java
    │   │           │   └── SupplierRepository.java
    │   │           └── service
    │   │               ├── AdminService.java
    │   │               ├── CategoryService.java
    │   │               ├── DashboardService.java
    │   │               ├── ProductService.java
    │   │               ├── ReportService.java
    │   │               ├── StockTransactionService.java
    │   │               └── SupplierService.java
    │   └── resources
    │       ├── application.properties        # Application configuration
    │       ├── static
    │       │   ├── css
    │       │   │   └── style.css             # Enterprise navy and blue theme
    │       │   └── js
    │       │       └── main.js               # Client interactions and prints
    │       └── templates
    │           ├── auth
    │           │   └── login.html            # Admin login view
    │           ├── categories
    │           │   ├── form.html             # Add and edit category
    │           │   ├── list.html             # List all categories
    │           │   └── view.html             # Category product inventory
    │           ├── dashboard
    │           │   └── index.html            # Analytics dashboard
    │           ├── error
    │           │   ├── 404.html              # Resource not found view
    │           │   └── 500.html              # Error view
    │           ├── layout
    │           │   └── base.html             # Common master layout fragments
    │           ├── products
    │           │   ├── form.html             # Add and edit product
    │           │   ├── list.html             # Filterable product catalog
    │           │   └── view.html             # Product specification
    │           ├── reports
    │           │   └── index.html            # Reports and valuation
    │           ├── stock
    │           │   ├── history.html          # Transaction movement history
    │           │   ├── low_stock.html        # Depletion alert view
    │           │   ├── stock_in.html         # Receive inventory form
    │           │   └── stock_out.html        # Dispatch inventory form
    │           └── suppliers
    │               ├── form.html             # Add and edit supplier
    │               └── list.html             # Supplier directory
    └── test
        └── java
            └── com
                └── inventory
                    ├── InventorySystemTests.java       # Service and transaction tests
                    └── WebSecurityAndMvcTests.java     # Security and controller tests
```

---

## Database Configuration

### Step 1: Create Database in MySQL

Open MySQL Command Line Client, MySQL Workbench, or phpMyAdmin and execute:

```sql
CREATE DATABASE IF NOT EXISTS inventory_management;
```

You can optionally import the complete schema and seed data using the included `database.sql` script:

```bash
mysql -u root -p inventory_management < database.sql
```

### Step 2: Configure Credentials

Review or edit `src/main/resources/application.properties` to match your local MySQL settings:

```properties
spring.datasource.mysql.host=127.0.0.1
spring.datasource.mysql.port=3306
spring.datasource.mysql.database=inventory_management
spring.datasource.mysql.username=root
spring.datasource.mysql.password=your_mysql_password
```

Note: If MySQL is not running on your computer, the application automatically detects it and uses an in memory database instance with pre loaded demonstration data so that you can test every page immediately without any setup errors.

---

## How to Run the Application

### Option A: Using Maven Wrapper (Recommended)

Open terminal in the project root directory and execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Or on Linux / macOS:

```bash
./mvnw spring-boot:run
```

### Option B: Using Installed Maven

```powershell
mvn spring-boot:run
```

### Option C: Package and Run JAR

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\inventory-management-system-1.0.0.jar
```

---

## Application Access

Once the application starts, open your browser and navigate to:

```
http://localhost:8080
```

### Default Administrator Credentials

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin123` |
| Access Role | `System Administrator` |

---

## Features and Module Walkthrough

### 1. Admin Authentication
- Secure login view with validation and credential checking.
- Session interceptor protects all internal routes (`/dashboard`, `/products`, `/categories`, `/suppliers`, `/stock`, `/reports`).
- Unauthorized requests are automatically redirected to `/login`.
- Single click logout invalidates session cleanly.

### 2. Executive Dashboard
- Dynamic database driven cards: Total Products, Total Stock Units, Low Stock Products, Out of Stock Products, Total Inventory Valuation.
- Interactive Chart.js graphs:
  - Stock Distribution by Category (Doughnut chart)
  - Product Stock Overview: Available Stock vs Minimum Stock Threshold (Bar chart)
- Recent 5 Products table with direct link to details.
- Recent 5 Stock Transactions table with real time status indicators.

### 3. Product Management (CRUD)
- View complete product inventory with SKU, category, supplier, unit price, stock count, and status badges.
- Filter products dynamically by Category.
- Search products by Name or SKU code.
- Sort records by Name, SKU, Price, Quantity, or Date Added.
- Create new products with duplicate SKU prevention.
- Edit existing product specifications.
- Safe delete operation with modal confirmation.

### 4. Category and Supplier Management
- Categorize catalog items into modular groups.
- View all products belonging to any specific category.
- Safety protection prevents deleting categories or suppliers that currently have active products associated with them.
- Supplier directory with contact person, phone, email, and supplied product counts.

### 5. Stock Management
- **Stock In**: Select product, specify incoming quantity, optional supplier, reference order number, and notes. Automatically increments available product stock and records a transaction history log.
- **Stock Out**: Select product, specify outgoing quantity, order reference, and destination notes. Validates that requested units do not exceed available stock. Automatically decrements inventory.
- **Low Stock Alerts**: Identifies products at or below their minimum reorder point. Provides instant one click restock shortcuts directly into the Stock In form.
- **Transaction History**: Comprehensive historical audit log with filter by product, transaction type (Stock In / Stock Out), and date ranges.

### 6. Inventory Reports and Data Export
- Comprehensive status report showing product counts, units, and cumulative valuation.
- Filter by report type (Complete Catalog, In Stock Healthy, Low Stock, Out of Stock) and Category.
- Export to CSV format for Excel or external processing.
- Clean printable layout with dedicated print styles for PDF generation via browser print (`Ctrl + P`).

---

## Test Verification

Run all unit, integration, and security tests with:

```powershell
.\mvnw.cmd test
```

All 12 automated test cases pass with zero failures and zero errors.

