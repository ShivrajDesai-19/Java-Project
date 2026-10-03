package com.petcare.util;

import com.petcare.exception.DatabaseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Unit 1 Syllabus Concept:
 * - Static Keyword, Static Variables, Static Methods
 * - Static Initializer Block (Class Initializer Block) executed during class loading
 * Unit 3 Syllabus Concept:
 * - Exception Handling (try-catch-finally, Chained Exceptions)
 *
 * Singleton Database Connection Manager for XAMPP MySQL.
 */
public class DBConnection {

    // Static variables for MySQL connection
    private static String host = "localhost";
    private static int port = 3306;
    private static String database = "smart_pet_care_db";
    private static String username = "root";
    private static String password = "";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Singleton instance
    private static DBConnection instance = null;
    private static boolean driverLoaded = false;
    private static String lastErrorMessage = null;

    /**
     * Unit 1 Syllabus Demonstration:
     * "Java static block/Class Initializer Block is executed before the main method
     * at the time of classloading to initialize static data members."
     */
    static {
        try {
            Class.forName(DRIVER_CLASS);
            driverLoaded = true;
            System.out.println("[DBConnection] MySQL JDBC Driver registered successfully via static block.");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older driver class name if present
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
                System.out.println("[DBConnection] Legacy MySQL JDBC Driver registered.");
            } catch (ClassNotFoundException ex) {
                driverLoaded = false;
                lastErrorMessage = "MySQL JDBC Driver not found in classpath: " + e.getMessage();
                System.err.println("[DBConnection] ERROR: " + lastErrorMessage);
            }
        }
        loadConfigFromFile();
    }

    // Private constructor (Singleton pattern)
    private DBConnection() {}

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Obtains a live connection to the smart_pet_care_db MySQL database.
     * Throws custom chained DatabaseException on failure.
     */
    public static Connection getConnection() throws DatabaseException {
        if (!driverLoaded) {
            throw new DatabaseException("MySQL JDBC Driver could not be loaded: " + lastErrorMessage);
        }

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + 
                         "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            // Unit 3: Chained Exception - wrapping SQLException inside custom DatabaseException
            throw new DatabaseException("Could not connect to XAMPP MySQL at " + host + ":" + port + 
                                       ". Please ensure MySQL is started in XAMPP Control Panel. Detail: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if MySQL server is reachable
     */
    public static boolean testConnection(String testHost, int testPort, String testUser, String testPass) {
        String testUrl = "jdbc:mysql://" + testHost + ":" + testPort + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(testUrl, testUser, testPass);
            return conn != null;
        } catch (Exception e) {
            lastErrorMessage = e.getMessage();
            return false;
        } finally {
            closeQuietly(conn, null, null);
        }
    }

    /**
     * Initializes the database schema automatically if tables do not exist
     */
    public static synchronized void initializeDatabase() throws DatabaseException {
        Connection rootConn = null;
        Statement stmt = null;
        try {
            // First connect to MySQL server root to ensure the database exists
            String serverUrl = "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            rootConn = DriverManager.getConnection(serverUrl, username, password);
            stmt = rootConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + database + "` DEFAULT CHARACTER SET utf8mb4;");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to ensure database exists: " + e.getMessage(), e);
        } finally {
            closeQuietly(rootConn, stmt, null);
        }

        // Now connect to the created database and check/create tables
        Connection dbConn = null;
        try {
            dbConn = getConnection();
            stmt = dbConn.createStatement();

            // Check if users table exists
            boolean hasUsers = false;
            try (ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'")) {
                if (rs.next()) {
                    hasUsers = true;
                }
            }

            if (!hasUsers) {
                System.out.println("[DBConnection] Tables not found. Creating complete database schema and seed data...");
                createTablesAndSeedData(stmt);
                System.out.println("[DBConnection] Database schema initialized with sample seed data.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database tables: " + e.getMessage(), e);
        } finally {
            closeQuietly(dbConn, stmt, null);
        }
    }

    private static void createTablesAndSeedData(Statement stmt) throws SQLException {
        // Create Users table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `users` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `username` VARCHAR(50) NOT NULL UNIQUE," +
            "  `password` VARCHAR(100) NOT NULL," +
            "  `full_name` VARCHAR(100) NOT NULL," +
            "  `role` VARCHAR(30) NOT NULL," +
            "  `email` VARCHAR(100)," +
            "  `phone` VARCHAR(20)," +
            "  `specialization` VARCHAR(100)," +
            "  `status` VARCHAR(20) DEFAULT 'ACTIVE'," +
            "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB;"
        );

        // Create Pet Owners table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `pet_owners` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `full_name` VARCHAR(100) NOT NULL," +
            "  `phone` VARCHAR(20) NOT NULL," +
            "  `email` VARCHAR(100)," +
            "  `address` TEXT," +
            "  `emergency_contact` VARCHAR(100)," +
            "  `registered_date` DATE NOT NULL" +
            ") ENGINE=InnoDB;"
        );

        // Create Pets table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `pets` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `owner_id` INT NOT NULL," +
            "  `name` VARCHAR(100) NOT NULL," +
            "  `species` VARCHAR(50) NOT NULL," +
            "  `breed` VARCHAR(100)," +
            "  `gender` VARCHAR(10) NOT NULL," +
            "  `date_of_birth` DATE," +
            "  `weight_kg` DOUBLE DEFAULT 0.0," +
            "  `color` VARCHAR(50)," +
            "  `microchip_id` VARCHAR(50)," +
            "  `allergies` TEXT," +
            "  `blood_group` VARCHAR(15)," +
            "  `registered_date` DATE NOT NULL," +
            "  CONSTRAINT `fk_pet_owner` FOREIGN KEY (`owner_id`) REFERENCES `pet_owners` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Create Appointments table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `appointments` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `pet_id` INT NOT NULL," +
            "  `vet_id` INT NOT NULL," +
            "  `appointment_date` DATE NOT NULL," +
            "  `appointment_time` VARCHAR(20) NOT NULL," +
            "  `purpose` VARCHAR(100) NOT NULL," +
            "  `status` VARCHAR(30) DEFAULT 'SCHEDULED'," +
            "  `notes` TEXT," +
            "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  CONSTRAINT `fk_appointment_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `fk_appointment_vet` FOREIGN KEY (`vet_id`) REFERENCES `users` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Create Medical Records table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `medical_records` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `pet_id` INT NOT NULL," +
            "  `vet_id` INT NOT NULL," +
            "  `visit_date` DATE NOT NULL," +
            "  `record_type` VARCHAR(30) NOT NULL," +
            "  `symptoms` TEXT," +
            "  `diagnosis` TEXT," +
            "  `treatment_plan` TEXT," +
            "  `temperature_c` DOUBLE DEFAULT 38.5," +
            "  `pulse_rate` INT DEFAULT 80," +
            "  CONSTRAINT `fk_med_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `fk_med_vet` FOREIGN KEY (`vet_id`) REFERENCES `users` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Create Medicines table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `medicines` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `name` VARCHAR(100) NOT NULL," +
            "  `category` VARCHAR(50) NOT NULL," +
            "  `batch_number` VARCHAR(50) NOT NULL," +
            "  `stock_quantity` INT NOT NULL DEFAULT 0," +
            "  `min_stock_alert` INT NOT NULL DEFAULT 10," +
            "  `unit_price` DOUBLE NOT NULL," +
            "  `expiry_date` DATE NOT NULL," +
            "  `supplier` VARCHAR(100)" +
            ") ENGINE=InnoDB;"
        );

        // Create Prescriptions table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `prescriptions` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `medical_record_id` INT NOT NULL," +
            "  `medicine_id` INT," +
            "  `medicine_name` VARCHAR(100) NOT NULL," +
            "  `dosage` VARCHAR(100) NOT NULL," +
            "  `frequency` VARCHAR(100) NOT NULL," +
            "  `duration_days` INT NOT NULL DEFAULT 1," +
            "  `instructions` TEXT," +
            "  CONSTRAINT `fk_presc_medrec` FOREIGN KEY (`medical_record_id`) REFERENCES `medical_records` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `fk_presc_medicine` FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`id`) ON DELETE SET NULL" +
            ") ENGINE=InnoDB;"
        );

        // Create Vaccinations table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `vaccinations` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `pet_id` INT NOT NULL," +
            "  `vaccine_name` VARCHAR(100) NOT NULL," +
            "  `batch_no` VARCHAR(50)," +
            "  `date_administered` DATE NOT NULL," +
            "  `next_due_date` DATE NOT NULL," +
            "  `administered_by` INT," +
            "  `status` VARCHAR(20) DEFAULT 'COMPLETED'," +
            "  `remarks` TEXT," +
            "  CONSTRAINT `fk_vacc_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `fk_vacc_vet` FOREIGN KEY (`administered_by`) REFERENCES `users` (`id`) ON DELETE SET NULL" +
            ") ENGINE=InnoDB;"
        );

        // Create Lab Tests table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `lab_tests` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `pet_id` INT NOT NULL," +
            "  `test_name` VARCHAR(100) NOT NULL," +
            "  `test_date` DATE NOT NULL," +
            "  `test_category` VARCHAR(50) NOT NULL," +
            "  `results` TEXT," +
            "  `normal_range` VARCHAR(100)," +
            "  `status` VARCHAR(20) DEFAULT 'COMPLETED'," +
            "  `conducted_by` VARCHAR(100)," +
            "  CONSTRAINT `fk_lab_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Create Service Bookings table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `service_bookings` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `pet_id` INT NOT NULL," +
            "  `service_type` VARCHAR(30) NOT NULL," +
            "  `booking_date` DATE NOT NULL," +
            "  `start_date` DATE NOT NULL," +
            "  `end_date` DATE," +
            "  `cage_or_room_no` VARCHAR(20)," +
            "  `package_details` VARCHAR(255) NOT NULL," +
            "  `special_instructions` TEXT," +
            "  `cost` DOUBLE NOT NULL," +
            "  `status` VARCHAR(20) DEFAULT 'CONFIRMED'," +
            "  CONSTRAINT `fk_service_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Create Invoices table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `invoices` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `invoice_number` VARCHAR(50) NOT NULL UNIQUE," +
            "  `pet_id` INT NOT NULL," +
            "  `owner_id` INT NOT NULL," +
            "  `invoice_date` DATE NOT NULL," +
            "  `subtotal` DOUBLE NOT NULL," +
            "  `tax_amount` DOUBLE NOT NULL," +
            "  `discount_amount` DOUBLE NOT NULL DEFAULT 0.0," +
            "  `total_amount` DOUBLE NOT NULL," +
            "  `payment_method` VARCHAR(30) NOT NULL," +
            "  `payment_status` VARCHAR(20) NOT NULL," +
            "  CONSTRAINT `fk_inv_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`)," +
            "  CONSTRAINT `fk_inv_owner` FOREIGN KEY (`owner_id`) REFERENCES `pet_owners` (`id`)" +
            ") ENGINE=InnoDB;"
        );

        // Create Invoice Items table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `invoice_items` (" +
            "  `id` INT AUTO_INCREMENT PRIMARY KEY," +
            "  `invoice_id` INT NOT NULL," +
            "  `description` VARCHAR(200) NOT NULL," +
            "  `item_type` VARCHAR(50) NOT NULL," +
            "  `quantity` INT NOT NULL DEFAULT 1," +
            "  `unit_price` DOUBLE NOT NULL," +
            "  `total_price` DOUBLE NOT NULL," +
            "  CONSTRAINT `fk_inv_item` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB;"
        );

        // Insert initial seed users
        stmt.executeUpdate(
            "INSERT INTO `users` (`username`, `password`, `full_name`, `role`, `email`, `phone`, `specialization`, `status`) VALUES " +
            "('admin', 'admin123', 'Dr. Ramesh Sharma', 'ADMINISTRATOR', 'admin@smartpetcare.com', '+91 9876543210', 'Chief Veterinary Officer', 'ACTIVE')," +
            "('dr.priya', 'vet123', 'Dr. Priya Mehta', 'VETERINARIAN', 'dr.priya@smartpetcare.com', '+91 9822012345', 'Small Animal Surgery', 'ACTIVE')," +
            "('dr.patel', 'vet123', 'Dr. Amit Patel', 'VETERINARIAN', 'dr.patel@smartpetcare.com', '+91 9822054321', 'Feline Care & Dermatology', 'ACTIVE')," +
            "('receptionist', 'rec123', 'Neha Verma', 'RECEPTIONIST', 'neha.v@smartpetcare.com', '+91 9833098765', 'Front Desk Operations', 'ACTIVE')," +
            "('staff', 'staff123', 'Rajesh Kumar', 'STAFF', 'rajesh.k@smartpetcare.com', '+91 9844011223', 'Clinic & Boarding Care', 'ACTIVE');"
        );

        // Insert seed pet owners
        stmt.executeUpdate(
            "INSERT INTO `pet_owners` (`full_name`, `phone`, `email`, `address`, `emergency_contact`, `registered_date`) VALUES " +
            "('Aarav Kapoor', '9811122334', 'aarav.kapoor@gmail.com', '12 Ring Road, Rajkot, Gujarat', 'Sunita Kapoor (9811122330)', '2026-01-10')," +
            "('Ananya Dave', '9822233445', 'ananya.dave@outlook.com', '45 Kalawad Road, Rajkot, Gujarat', 'Bhavik Dave (9822233440)', '2026-01-15')," +
            "('Vikramaditya Jadeja', '9833344556', 'vikram.jadeja@yahoo.com', '7 Palace Road, Rajkot, Gujarat', 'Kiran Jadeja (9833344550)', '2026-02-01');"
        );

        // Insert seed pets
        stmt.executeUpdate(
            "INSERT INTO `pets` (`owner_id`, `name`, `species`, `breed`, `gender`, `date_of_birth`, `weight_kg`, `color`, `microchip_id`, `allergies`, `blood_group`, `registered_date`) VALUES " +
            "(1, 'Max', 'Dog', 'Golden Retriever', 'Male', '2023-04-12', 28.5, 'Golden Cream', 'CHIP-984210', 'None known', 'DEA 1.1+', '2026-01-10')," +
            "(1, 'Milo', 'Cat', 'Persian', 'Male', '2024-02-10', 4.2, 'White', 'CHIP-984211', 'Penicillin', 'Type A', '2026-01-12')," +
            "(2, 'Bella', 'Dog', 'Labrador Retriever', 'Female', '2022-08-25', 25.0, 'Chocolate Brown', 'CHIP-984332', 'Chicken protein', 'DEA 1.1-', '2026-01-15')," +
            "(3, 'Leo', 'Dog', 'German Shepherd', 'Male', '2021-11-19', 34.0, 'Black & Tan', 'CHIP-984501', 'None', 'DEA 1.1+', '2026-02-01');"
        );

        // Insert seed medicines
        stmt.executeUpdate(
            "INSERT INTO `medicines` (`name`, `category`, `batch_number`, `stock_quantity`, `min_stock_alert`, `unit_price`, `expiry_date`, `supplier`) VALUES " +
            "('Amoxicillin Clavulanate 250mg', 'Antibiotic', 'BAT-AMX-2026', 45, 15, 320.00, '2027-12-31', 'PetCare Pharma Ltd')," +
            "('Meloxicam Oral Suspension 1.5mg/ml', 'Painkiller', 'BAT-MLX-2025', 18, 10, 450.00, '2027-08-15', 'VetLife Therapeutics')," +
            "('Rabies Canine Vaccine (Rabisin)', 'Vaccine', 'BAT-RAB-109', 24, 10, 250.00, '2027-05-30', 'Boehringer Ingelheim')," +
            "('DHPP 5-in-1 Vaccine', 'Vaccine', 'BAT-DHP-441', 6, 12, 650.00, '2027-04-10', 'Zoetis India')," +
            "('Fipronil Spot-On Tick & Flea', 'Antiparasitic', 'BAT-FIP-005', 4, 10, 520.00, '2027-11-20', 'Frontline Vet');"
        );

        // Insert seed appointments
        stmt.executeUpdate(
            "INSERT INTO `appointments` (`pet_id`, `vet_id`, `appointment_date`, `appointment_time`, `purpose`, `status`, `notes`) VALUES " +
            "(1, 2, CURDATE(), '10:00 AM', 'Annual Routine Health Checkup', 'SCHEDULED', 'Comprehensive health screening.')," +
            "(2, 3, CURDATE(), '11:30 AM', 'Skin checkup and flea control', 'SCHEDULED', 'Ear irritation reported.')," +
            "(3, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '02:00 PM', 'DHPP Booster Vaccine', 'SCHEDULED', 'Booster dose schedule.');"
        );

        // Insert seed service bookings
        stmt.executeUpdate(
            "INSERT INTO `service_bookings` (`pet_id`, `service_type`, `booking_date`, `start_date`, `end_date`, `cage_or_room_no`, `package_details`, `special_instructions`, `cost`, `status`) VALUES " +
            "(1, 'GROOMING', CURDATE(), CURDATE(), CURDATE(), 'Groom-01', 'Deluxe Spa Bath + Nail Clipping', 'Gentle drying', 1200.00, 'COMPLETED')," +
            "(3, 'BOARDING', CURDATE(), CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Kennel-B2', '2-Day Boarding Suite with Diet', 'Feed twice daily', 1600.00, 'CONFIRMED');"
        );

        // Insert seed invoices
        stmt.executeUpdate(
            "INSERT INTO `invoices` (`invoice_number`, `pet_id`, `owner_id`, `invoice_date`, `subtotal`, `tax_amount`, `discount_amount`, `total_amount`, `payment_method`, `payment_status`) VALUES " +
            "('INV-202610-001', 1, 1, CURDATE(), 1200.00, 60.00, 0.00, 1260.00, 'UPI', 'PAID');"
        );

        stmt.executeUpdate(
            "INSERT INTO `invoice_items` (`invoice_id`, `description`, `item_type`, `quantity`, `unit_price`, `total_price`) VALUES " +
            "(1, 'Deluxe Spa Bath + Nail Clipping', 'GROOMING', 1, 1200.00, 1200.00);"
        );
    }

    /**
     * Unit 3: Helper utility to safely close JDBC resources in finally blocks
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    // Configuration persistence helpers
    private static void loadConfigFromFile() {
        File file = new File("db.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                host = props.getProperty("db.host", "localhost");
                port = Integer.parseInt(props.getProperty("db.port", "3306"));
                database = props.getProperty("db.name", "smart_pet_care_db");
                username = props.getProperty("db.user", "root");
                password = props.getProperty("db.password", "");
            } catch (Exception e) {
                System.err.println("[DBConnection] Could not read db.properties: " + e.getMessage());
            }
        }
    }

    public static void saveConfig(String newHost, int newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        database = newDb;
        username = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);

        try (FileOutputStream fos = new FileOutputStream("db.properties")) {
            props.store(fos, "Smart Pet Care Database Settings");
        } catch (Exception e) {
            System.err.println("[DBConnection] Failed to save config: " + e.getMessage());
        }
    }

    // Getters
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDatabase() { return database; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
    public static String getLastError() { return lastErrorMessage; }
}
