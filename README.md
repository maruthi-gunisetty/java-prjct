# Digital Book

A full stack enterprise inventory and logistics management web application developed using JavaScript, Node.js, Express, EJS, SQLite / MySQL, and Bootstrap 5.

Suitable for academic evaluations and real world business inventory tracking.

---

## Key Highlights

- **Full Stack JavaScript Architecture**: Built on Node.js and Express with modular MVC structure (Controllers, Models, Routes, Views, Configuration).
- **Direct Executive Access**: Landing page routes directly to the executive dashboard without requiring mandatory prior authentication.
- **Top Right Authentication Portal**: Accessible login logo in the header with support for both official administrator credentials and guest/fake credentials for frictionless demonstration.
- **Cascading Product Deletion**: Safe deletion of products that automatically cleans up child transaction records, completely eliminating foreign key lock errors.
- **Dashboard Analytics**: Real time stock valuation, category distribution charts, stock overview graphs, and recent transaction feeds via Chart.js.
- **Complete Product Catalog**: Full CRUD operations, unique SKU enforcement, multi parameter search, category filtering, and sorting.
- **Category and Supplier Management**: Complete lifecycle management with relationship safety guards.
- **Stock Movement Operations**: Stock In (automatic receipt and increment) and Stock Out (validation against current stock and automatic decrement).
- **Automated Alerts**: Low stock and depletion alerts with immediate one click restock shortcuts.
- **Reporting and Exports**: Inventory valuation summaries, status filtering, printable executive view, and direct CSV file download.
- **Typography and Design**: Roboto Google Font, clean navy blue palette, zero emojis, and zero hyphens in all user facing text.

---

## Technology Stack

### Backend
- **Runtime**: Node.js (v18+)
- **Framework**: Express.js 4
- **Persistence Engine**: SQLite (`better-sqlite3`) with optional MySQL support (`mysql2`)
- **Security**: BCryptJS password hashing and session management

### Frontend
- **Template Engine**: EJS (Embedded JavaScript)
- **CSS Framework**: Bootstrap 5.3.3
- **Typography**: Roboto (Google Fonts)
- **Iconography**: Bootstrap Icons (clean vector line icons)
- **Data Visualization**: Chart.js 4.4.2

---

## Project Structure

```
c:\Users\marut\java prjct\
├── config/
│   └── db.js                 # Database configuration, schema migration, and initial seeding
├── models/
│   ├── adminModel.js         # Administrator lookup, authentication, and demo access
│   ├── categoryModel.js      # Category CRUD and product count tracking
│   ├── supplierModel.js      # Supplier CRUD and product unlinking
│   ├── productModel.js       # Product CRUD, search, filter, sort, cascade delete, stats
│   └── transactionModel.js   # Stock In, Stock Out with balance checks, audit history
├── controllers/
│   ├── authController.js     # Login with credentials/fake mode, demo login, logout
│   ├── dashboardController.js# Dashboard KPI metrics, recent feeds, and analytics
│   ├── productController.js  # Product management actions
│   ├── categoryController.js # Category operations
│   ├── supplierController.js # Supplier operations
│   ├── stockController.js    # Stock In, Stock Out, history log, low stock monitor
│   └── reportController.js   # Valuation reports and CSV file download
├── routes/
│   ├── authRoutes.js         # Authentication routes
│   ├── dashboardRoutes.js    # Dashboard routes
│   ├── productRoutes.js      # Product routes
│   ├── categoryRoutes.js     # Category routes
│   ├── supplierRoutes.js     # Supplier routes
│   ├── stockRoutes.js        # Stock routes
│   ├── reportRoutes.js       # Report and export routes
│   └── apiRoutes.js          # REST JSON endpoints for dashboard charts
├── views/
│   ├── partials/
│   │   ├── header.ejs        # Header with Digital Book brand, Roboto font, top right login
│   │   ├── sidebar.ejs       # Clean navigation sidebar without emojis or hyphens
│   │   ├── alerts.ejs        # Alert flash notifications
│   │   └── footer.ejs        # Footer and script bundles
│   ├── auth/                 # Admin login view with fake credentials support
│   ├── dashboard/            # Executive KPI cards and Chart.js visualizations
│   ├── products/             # Product list, form, and view templates
│   ├── categories/           # Category list and form templates
│   ├── suppliers/            # Supplier list and form templates
│   ├── stock/                # Stock In, Stock Out, history, low stock templates
│   ├── reports/              # Valuation reports and CSV export template
│   └── error/                # 404 and 500 enterprise error templates
├── public/
│   ├── css/style.css         # Enterprise navy stylesheet with Roboto typography
│   └── js/main.js            # Sidebar toggle, modal confirmations, and print trigger
├── test/
│   └── app.test.js           # Automated integration verification suite
├── database.sql              # MySQL schema and reference seed dataset
├── package.json              # Project dependencies and run scripts
├── server.js                 # Express server entry point (Port 8080)
└── README.md                 # System documentation
```

---

## Local Setup and Running

### Prerequisites
- Node.js (version 18 or newer)
- npm (comes with Node.js)

### Installation

1. Navigate to the project root directory:
   ```bash
   cd "c:\Users\marut\java prjct"
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Launch the application:
   ```bash
   npm start
   ```

4. Open the application in your browser:
   ```
   http://localhost:8080
   ```

The application automatically seeds an administrator, product categories, suppliers, catalog products, and initial transaction logs on first run.

---

## Access Credentials

| Role | Username | Password | Notes |
|---|---|---|---|
| System Administrator | `admin` | `admin123` | Default administrator account |
| Guest / Demo User | *Any username* | *Any password* | Allows immediate exploration via fake credentials mode |
| Instant Access | Click "Demo Access" | No typing needed | Top right header shortcut |

---

## Automated Verification Suite

Run the included verification suite to test database models, calculations, stock constraints, and cascading deletion:

```bash
node test/app.test.js
```
