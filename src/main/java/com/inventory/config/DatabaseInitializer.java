package com.inventory.config;

import com.inventory.entity.*;
import com.inventory.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(AdminRepository adminRepository,
                               CategoryRepository categoryRepository,
                               SupplierRepository supplierRepository,
                               ProductRepository productRepository,
                               StockTransactionRepository transactionRepository,
                               PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Initialize Default Admin
        if (adminRepository.count() == 0) {
            Admin admin = new Admin(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    "admin@enterprise.com",
                    "ADMIN"
            );
            adminRepository.save(admin);
            log.info("Default administrator account created. Username: admin");
        }

        // Initialize Categories
        if (categoryRepository.count() == 0) {
            Category c1 = categoryRepository.save(new Category("Computing Hardware", "Processors, memory modules, and high speed storage units"));
            Category c2 = categoryRepository.save(new Category("Networking Devices", "Enterprise routers, switches, and patch accessories"));
            Category c3 = categoryRepository.save(new Category("Industrial Tools", "Precision workshop tools, diagnostic equipment, and meters"));
            Category c4 = categoryRepository.save(new Category("Power Equipment", "Voltage regulators, uninterruptible power supplies, and battery units"));
            Category c5 = categoryRepository.save(new Category("Packaging Supplies", "Reinforced boxes, protective static bags, and strapping"));

            // Initialize Suppliers
            Supplier s1 = supplierRepository.save(new Supplier("Apex Industrial Supplies", "contact@apexsupplies.com", "+1 555 0192", "742 Evergreen Road, Sector 4", "Robert Vance"));
            Supplier s2 = supplierRepository.save(new Supplier("Global Tech Logistics", "sales@globaltechlogistics.com", "+1 555 0843", "1200 Innovation Parkway", "Elena Rostova"));
            Supplier s3 = supplierRepository.save(new Supplier("Prime Semiconductor Corp", "support@primesemicorp.com", "+1 555 0471", "55 Silicon Boulevard", "David Chen"));
            Supplier s4 = supplierRepository.save(new Supplier("Vertex Packaging Solutions", "orders@vertexpackaging.com", "+1 555 0329", "89 Harbor Way", "Sarah Jenkins"));

            // Initialize Diverse Products
            Product p1 = productRepository.save(new Product("Intel Core i7 Processor", "SKU1001", "8 core high efficiency desktop processor with cache", new BigDecimal("329.99"), 45, 10, c1, s3));
            Product p2 = productRepository.save(new Product("DDR5 32GB RAM Module", "SKU1002", "High speed 5600MHz desktop memory stick", new BigDecimal("119.50"), 5, 15, c1, s3));
            Product p3 = productRepository.save(new Product("NVMe SSD 1TB High Speed", "SKU1003", "PCIe Gen 4 solid state storage unit", new BigDecimal("89.90"), 0, 10, c1, s2));
            Product p4 = productRepository.save(new Product("Gigabit 24 Port Managed Switch", "SKU1004", "Rackmount manageable network distribution switch", new BigDecimal("249.00"), 18, 5, c2, s2));
            Product p5 = productRepository.save(new Product("Cat6 Shielded Patch Cable 50m", "SKU1005", "Outdoor rated copper networking patch cable", new BigDecimal("34.50"), 8, 20, c2, s1));
            Product p6 = productRepository.save(new Product("Digital Multimeter Pro", "SKU1006", "True RMS electrical diagnostic handheld multimeter", new BigDecimal("79.99"), 30, 8, c3, s1));
            Product p7 = productRepository.save(new Product("Thermal Heat Sink Compound", "SKU1007", "Non conductive carbon thermal interface paste", new BigDecimal("12.50"), 0, 25, c1, s3));
            Product p8 = productRepository.save(new Product("Reinforced Storage Container", "SKU1008", "Heavy duty industrial composite modular container", new BigDecimal("45.00"), 62, 15, c5, s4));
            Product p9 = productRepository.save(new Product("Industrial Soldering Station", "SKU1009", "Adjustable temperature digital soldering unit", new BigDecimal("145.00"), 4, 6, c3, s1));
            Product p10 = productRepository.save(new Product("UPS 1500VA Battery Backup", "SKU1010", "Pure sine wave power protection unit", new BigDecimal("399.00"), 14, 4, c4, s2));
            Product p11 = productRepository.save(new Product("Laser Temperature Sensor", "SKU1011", "Non contact infrared digital thermometer", new BigDecimal("55.00"), 22, 5, c3, s1));
            Product p12 = productRepository.save(new Product("Optical Transceiver SFP Module", "SKU1012", "10G single mode fiber optic pluggable module", new BigDecimal("68.00"), 3, 10, c2, s2));

            // Initialize Stock Transactions
            LocalDateTime now = LocalDateTime.now();
            transactionRepository.save(new StockTransaction(p1, "STOCK_IN", 50, p1.getUnitPrice(), "PO1001", "Initial batch delivery from supplier", s3, now.minusDays(18)));
            transactionRepository.save(new StockTransaction(p1, "STOCK_OUT", 5, p1.getUnitPrice(), "SO2001", "Fulfillment for order ref 8492", null, now.minusDays(12)));
            transactionRepository.save(new StockTransaction(p2, "STOCK_IN", 20, p2.getUnitPrice(), "PO1002", "Restock shipment from Prime Semiconductor", s3, now.minusDays(15)));
            transactionRepository.save(new StockTransaction(p2, "STOCK_OUT", 15, p2.getUnitPrice(), "SO2002", "Assembly requisition order", null, now.minusDays(8)));
            transactionRepository.save(new StockTransaction(p4, "STOCK_IN", 25, p4.getUnitPrice(), "PO1003", "Warehouse transfer from regional hub", s2, now.minusDays(10)));
            transactionRepository.save(new StockTransaction(p4, "STOCK_OUT", 7, p4.getUnitPrice(), "SO2003", "Infrastructure upgrade project dispatch", null, now.minusDays(4)));
            transactionRepository.save(new StockTransaction(p6, "STOCK_IN", 35, p6.getUnitPrice(), "PO1004", "Standard tool replenishment", s1, now.minusDays(7)));
            transactionRepository.save(new StockTransaction(p6, "STOCK_OUT", 5, p6.getUnitPrice(), "SO2004", "Testing lab equipment allocation", null, now.minusDays(2)));
            transactionRepository.save(new StockTransaction(p8, "STOCK_IN", 70, p8.getUnitPrice(), "PO1005", "Bulk packaging carton delivery", s4, now.minusDays(5)));
            transactionRepository.save(new StockTransaction(p8, "STOCK_OUT", 8, p8.getUnitPrice(), "SO2005", "Outbound shipping pack allocation", null, now.minusDays(1)));

            log.info("Database initialized with sample categories, suppliers, products, and stock movements.");
        }
    }
}
