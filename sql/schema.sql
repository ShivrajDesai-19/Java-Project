-- =====================================================================
-- Smart Pet Care & Veterinary Management System
-- Database Schema for MySQL / XAMPP (MariaDB)
-- BCA Semester 3 Final Project - Marwadi University
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `smart_pet_care_db`;
USE `smart_pet_care_db`;

-- Drop tables in reverse order of foreign keys
DROP TABLE IF EXISTS `invoice_items`;
DROP TABLE IF EXISTS `invoices`;
DROP TABLE IF EXISTS `service_bookings`;
DROP TABLE IF EXISTS `prescriptions`;
DROP TABLE IF EXISTS `lab_tests`;
DROP TABLE IF EXISTS `vaccinations`;
DROP TABLE IF EXISTS `medical_records`;
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `pets`;
DROP TABLE IF EXISTS `pet_owners`;
DROP TABLE IF EXISTS `medicines`;
DROP TABLE IF EXISTS `users`;

-- 1. Users Table (Role-based Authentication)
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(30) NOT NULL, -- ADMINISTRATOR, VETERINARIAN, RECEPTIONIST, STAFF
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `specialization` VARCHAR(100),
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Pet Owners Table
CREATE TABLE `pet_owners` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100),
    `address` TEXT,
    `emergency_contact` VARCHAR(100),
    `registered_date` DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Pets Table
CREATE TABLE `pets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `owner_id` INT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `species` VARCHAR(50) NOT NULL, -- Dog, Cat, Bird, Rabbit, etc.
    `breed` VARCHAR(100),
    `gender` VARCHAR(10) NOT NULL, -- Male, Female
    `date_of_birth` DATE,
    `weight_kg` DOUBLE DEFAULT 0.0,
    `color` VARCHAR(50),
    `microchip_id` VARCHAR(50),
    `allergies` TEXT,
    `blood_group` VARCHAR(15),
    `registered_date` DATE NOT NULL,
    CONSTRAINT `fk_pet_owner` FOREIGN KEY (`owner_id`) REFERENCES `pet_owners` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Appointments Table
CREATE TABLE `appointments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `vet_id` INT NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` VARCHAR(20) NOT NULL,
    `purpose` VARCHAR(100) NOT NULL, -- Routine Checkup, Vaccination, Surgery, Follow-up, Grooming
    `status` VARCHAR(30) DEFAULT 'SCHEDULED', -- SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_appointment_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_appointment_vet` FOREIGN KEY (`vet_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Medical Records Table
CREATE TABLE `medical_records` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `vet_id` INT NOT NULL,
    `visit_date` DATE NOT NULL,
    `record_type` VARCHAR(30) NOT NULL, -- DIAGNOSIS, VACCINATION, LAB_TEST, SURGERY
    `symptoms` TEXT,
    `diagnosis` TEXT,
    `treatment_plan` TEXT,
    `temperature_c` DOUBLE DEFAULT 38.5,
    `pulse_rate` INT DEFAULT 80,
    CONSTRAINT `fk_med_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_med_vet` FOREIGN KEY (`vet_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Medicines / Pharmacy Inventory Table
CREATE TABLE `medicines` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `category` VARCHAR(50) NOT NULL, -- Antibiotic, Painkiller, Vaccine, Antiseptic, Vitamin, Antiparasitic
    `batch_number` VARCHAR(50) NOT NULL,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `min_stock_alert` INT NOT NULL DEFAULT 10,
    `unit_price` DOUBLE NOT NULL,
    `expiry_date` DATE NOT NULL,
    `supplier` VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Prescriptions Table
CREATE TABLE `prescriptions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `medical_record_id` INT NOT NULL,
    `medicine_id` INT,
    `medicine_name` VARCHAR(100) NOT NULL,
    `dosage` VARCHAR(100) NOT NULL,
    `frequency` VARCHAR(100) NOT NULL,
    `duration_days` INT NOT NULL DEFAULT 1,
    `instructions` TEXT,
    CONSTRAINT `fk_presc_medrec` FOREIGN KEY (`medical_record_id`) REFERENCES `medical_records` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_presc_medicine` FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Vaccinations Table
CREATE TABLE `vaccinations` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `vaccine_name` VARCHAR(100) NOT NULL,
    `batch_no` VARCHAR(50),
    `date_administered` DATE NOT NULL,
    `next_due_date` DATE NOT NULL,
    `administered_by` INT,
    `status` VARCHAR(20) DEFAULT 'COMPLETED', -- COMPLETED, DUE, OVERDUE
    `remarks` TEXT,
    CONSTRAINT `fk_vacc_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_vacc_vet` FOREIGN KEY (`administered_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Laboratory Tests Table
CREATE TABLE `lab_tests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `test_name` VARCHAR(100) NOT NULL,
    `test_date` DATE NOT NULL,
    `test_category` VARCHAR(50) NOT NULL, -- Hematology, Biochemistry, Radiology, Urinalysis
    `results` TEXT,
    `normal_range` VARCHAR(100),
    `status` VARCHAR(20) DEFAULT 'COMPLETED', -- REQUESTED, IN_ANALYSIS, COMPLETED
    `conducted_by` VARCHAR(100),
    CONSTRAINT `fk_lab_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Grooming and Boarding Service Bookings Table
CREATE TABLE `service_bookings` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `service_type` VARCHAR(30) NOT NULL, -- GROOMING, BOARDING
    `booking_date` DATE NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE,
    `cage_or_room_no` VARCHAR(20),
    `package_details` VARCHAR(255) NOT NULL,
    `special_instructions` TEXT,
    `cost` DOUBLE NOT NULL,
    `status` VARCHAR(20) DEFAULT 'CONFIRMED', -- CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED
    CONSTRAINT `fk_service_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Invoices Table
CREATE TABLE `invoices` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_number` VARCHAR(50) NOT NULL UNIQUE,
    `pet_id` INT NOT NULL,
    `owner_id` INT NOT NULL,
    `invoice_date` DATE NOT NULL,
    `subtotal` DOUBLE NOT NULL,
    `tax_amount` DOUBLE NOT NULL,
    `discount_amount` DOUBLE NOT NULL DEFAULT 0.0,
    `total_amount` DOUBLE NOT NULL,
    `payment_method` VARCHAR(30) NOT NULL, -- Cash, UPI, Credit Card, Debit Card, NetBanking
    `payment_status` VARCHAR(20) NOT NULL, -- PAID, PENDING, CANCELLED
    CONSTRAINT `fk_inv_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_inv_owner` FOREIGN KEY (`owner_id`) REFERENCES `pet_owners` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Invoice Line Items Table
CREATE TABLE `invoice_items` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_id` INT NOT NULL,
    `description` VARCHAR(200) NOT NULL,
    `item_type` VARCHAR(50) NOT NULL, -- CONSULTATION, MEDICINE, LAB_TEST, GROOMING, BOARDING, VACCINE
    `quantity` INT NOT NULL DEFAULT 1,
    `unit_price` DOUBLE NOT NULL,
    `total_price` DOUBLE NOT NULL,
    CONSTRAINT `fk_inv_item` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- SEED DATA FOR DEMO & TESTING
-- =====================================================================

-- Users (Pass: plain text for educational clarity or demo, e.g. admin123, vet123, etc.)
INSERT INTO `users` (`username`, `password`, `full_name`, `role`, `email`, `phone`, `specialization`, `status`) VALUES
('admin', 'admin123', 'Dr. Ramesh Sharma', 'ADMINISTRATOR', 'admin@smartpetcare.com', '+91 9876543210', 'Chief Veterinary Officer', 'ACTIVE'),
('dr.smith', 'vet123', 'Dr. Priya Mehta', 'VETERINARIAN', 'dr.priya@smartpetcare.com', '+91 9822012345', 'Small Animal Surgery & Canine Medicine', 'ACTIVE'),
('dr.patel', 'vet123', 'Dr. Amit Patel', 'VETERINARIAN', 'dr.patel@smartpetcare.com', '+91 9822054321', 'Feline Care & Dermatology', 'ACTIVE'),
('receptionist', 'rec123', 'Neha Verma', 'RECEPTIONIST', 'neha.v@smartpetcare.com', '+91 9833098765', 'Front Desk Operations', 'ACTIVE'),
('staff', 'staff123', 'Rajesh Kumar', 'STAFF', 'rajesh.k@smartpetcare.com', '+91 9844011223', 'Pet Care & Pharmacy Assistant', 'ACTIVE');

-- Pet Owners
INSERT INTO `pet_owners` (`full_name`, `phone`, `email`, `address`, `emergency_contact`, `registered_date`) VALUES
('Aarav Kapoor', '9811122334', 'aarav.kapoor@gmail.com', '12 Ring Road, Rajkot, Gujarat', 'Sunita Kapoor (9811122330)', '2026-01-10'),
('Ananya Dave', '9822233445', 'ananya.dave@outlook.com', '45 Kalawad Road, Rajkot, Gujarat', 'Bhavik Dave (9822233440)', '2026-01-15'),
('Vikramaditya Jadeja', '9833344556', 'vikram.jadeja@yahoo.com', '7 Palace Road, Rajkot, Gujarat', 'Kiran Jadeja (9833344550)', '2026-02-01'),
('Sneha Trivedi', '9844455667', 'sneha.t@gmail.com', '108 University Road, Rajkot, Gujarat', 'Manish Trivedi (9844455660)', '2026-02-12'),
('Harshil Joshi', '9855566778', 'harshil.j@gmail.com', '88 Mavdi Main Road, Rajkot, Gujarat', 'Devanshi Joshi (9855566770)', '2026-03-05');

-- Pets
INSERT INTO `pets` (`owner_id`, `name`, `species`, `breed`, `gender`, `date_of_birth`, `weight_kg`, `color`, `microchip_id`, `allergies`, `blood_group`, `registered_date`) VALUES
(1, 'Max', 'Dog', 'Golden Retriever', 'Male', '2023-04-12', 28.5, 'Golden Cream', 'CHIP-984210', 'None known', 'DEA 1.1+', '2026-01-10'),
(1, 'Milo', 'Cat', 'Persian', 'Male', '2024-02-10', 4.2, 'White', 'CHIP-984211', 'Penicillin', 'Type A', '2026-01-12'),
(2, 'Bella', 'Dog', 'Labrador Retriever', 'Female', '2022-08-25', 25.0, 'Chocolate Brown', 'CHIP-984332', 'Chicken protein', 'DEA 1.1-', '2026-01-15'),
(3, 'Leo', 'Dog', 'German Shepherd', 'Male', '2021-11-19', 34.0, 'Black & Tan', 'CHIP-984501', 'None', 'DEA 1.1+', '2026-02-01'),
(4, 'Luna', 'Cat', 'Siamese', 'Female', '2023-09-05', 3.8, 'Seal Point', 'CHIP-984712', 'Pollen', 'Type B', '2026-02-12'),
(5, 'Coco', 'Rabbit', 'Holland Lop', 'Female', '2024-05-18', 1.8, 'Sandy Brown', 'CHIP-984890', 'None', 'Unknown', '2026-03-05');

-- Medicines
INSERT INTO `medicines` (`name`, `category`, `batch_number`, `stock_quantity`, `min_stock_alert`, `unit_price`, `expiry_date`, `supplier`) VALUES
('Amoxicillin Clavulanate 250mg', 'Antibiotic', 'BAT-AMX-2026', 45, 15, 320.00, '2027-12-31', 'PetCare Pharma Ltd'),
('Meloxicam Oral Suspension 1.5mg/ml', 'Painkiller', 'BAT-MLX-2025', 18, 10, 450.00, '2027-08-15', 'VetLife Therapeutics'),
('Rabies Canine Vaccine (Rabisin)', 'Vaccine', 'BAT-RAB-109', 24, 10, 250.00, '2027-05-30', 'Boehringer Ingelheim'),
('DHPP 5-in-1 Vaccine', 'Vaccine', 'BAT-DHP-441', 8, 12, 650.00, '2027-04-10', 'Zoetis India'),
('Prednisolone 5mg Tablets', 'Steroid', 'BAT-PRD-882', 60, 20, 180.00, '2028-01-20', 'Cipla Vet Care'),
('Cephalexin 300mg Tablets', 'Antibiotic', 'BAT-CPH-302', 12, 15, 290.00, '2027-10-15', 'PetCare Pharma Ltd'),
('Fipronil Spot-On Tick & Flea (10-20kg)', 'Antiparasitic', 'BAT-FIP-005', 5, 10, 520.00, '2027-11-20', 'Frontline Vet'),
('Pet Cal-D Multivitamin Syrup', 'Vitamin', 'BAT-VIT-112', 35, 10, 210.00, '2027-09-30', 'Himalaya Animal Health');

-- Appointments
INSERT INTO `appointments` (`pet_id`, `vet_id`, `appointment_date`, `appointment_time`, `purpose`, `status`, `notes`) VALUES
(1, 2, '2026-10-09', '10:00 AM', 'Annual Routine Health Checkup', 'SCHEDULED', 'Owner requested full blood profile and general wellness screening.'),
(2, 3, '2026-10-09', '11:30 AM', 'Hairball treatment and skin allergy check', 'SCHEDULED', 'Pet showing itching near ears.'),
(3, 2, '2026-10-10', '02:00 PM', 'DHPP Booster Vaccination', 'SCHEDULED', 'Due for scheduled booster dose.'),
(4, 3, '2026-10-08', '04:30 PM', 'Eye discharge consultation', 'COMPLETED', 'Prescribed antibiotic eye drops and anti-inflammatory syrup.'),
(5, 2, '2026-10-12', '11:00 AM', 'Dental scaling and nail clipping', 'SCHEDULED', 'Mild tartar buildup observed on previous visit.');

-- Medical Records
INSERT INTO `medical_records` (`pet_id`, `vet_id`, `visit_date`, `record_type`, `symptoms`, `diagnosis`, `treatment_plan`, `temperature_c`, `pulse_rate`) VALUES
(4, 3, '2026-10-08', 'DIAGNOSIS', 'Bilateral ocular discharge, squinting, slight conjunctival erythema', 'Acute Allergic Conjunctivitis', 'Topical Ciprofloxacin eye drops twice daily for 5 days. Oral Meloxicam 0.1mg/kg.', 38.6, 120),
(1, 2, '2026-09-15', 'DIAGNOSIS', 'Lethargy, decreased appetite for 2 days', 'Mild Gastric Indigestion', 'Administered Ranitidine injection and fluid therapy. Prescribed digestive enzymes.', 39.1, 95);

-- Prescriptions
INSERT INTO `prescriptions` (`medical_record_id`, `medicine_id`, `medicine_name`, `dosage`, `frequency`, `duration_days`, `instructions`) VALUES
(1, 2, 'Meloxicam Oral Suspension 1.5mg/ml', '0.5 ml', 'Once daily after food', 3, 'Shake bottle well before giving.'),
(1, 1, 'Amoxicillin Clavulanate 250mg', '1 Tablet', 'Twice daily after food', 5, 'Give with a small amount of food to prevent nausea.');

-- Vaccinations
INSERT INTO `vaccinations` (`pet_id`, `vaccine_name`, `batch_no`, `date_administered`, `next_due_date`, `administered_by`, `status`, `remarks`) VALUES
(1, 'Rabies Canine Vaccine (Rabisin)', 'BAT-RAB-109', '2026-04-15', '2027-04-15', 2, 'COMPLETED', 'Administered subcutaneous, pet tolerated well.'),
(3, 'DHPP 5-in-1 Vaccine', 'BAT-DHP-441', '2025-10-10', '2026-10-10', 2, 'DUE', 'Booster dose reminder alert generated.');

-- Lab Tests
INSERT INTO `lab_tests` (`pet_id`, `test_name`, `test_date`, `test_category`, `results`, `normal_range`, `status`, `conducted_by`) VALUES
(1, 'Complete Blood Count (CBC)', '2026-09-15', 'Hematology', 'WBC: 11.2 x10^3/uL, RBC: 6.8 x10^6/uL, Platelets: 280 x10^3/uL. Normal.', 'WBC: 6.0-17.0, RBC: 5.5-8.5', 'COMPLETED', 'City Vet Diagnostic Lab'),
(4, 'Ocular Swab Culture', '2026-10-08', 'Pathology', 'No bacterial colony observed after 24 hrs. Confirms allergic origin.', 'No pathogenic growth', 'COMPLETED', 'In-Clinic Pathology Unit');

-- Service Bookings (Grooming and Boarding)
INSERT INTO `service_bookings` (`pet_id`, `service_type`, `booking_date`, `start_date`, `end_date`, `cage_or_room_no`, `package_details`, `special_instructions`, `cost`, `status`) VALUES
(1, 'GROOMING', '2026-10-05', '2026-10-05', '2026-10-05', 'Groom-02', 'Deluxe Spa Bath + De-shedding + Ear Cleansing', 'Use hypoallergenic shampoo only', 1200.00, 'COMPLETED'),
(3, 'BOARDING', '2026-10-15', '2026-10-15', '2026-10-18', 'Kennel-A4', '3-Day Luxury Dog Boarding Suite with 2 Daily Walks', 'Feed Royal Canin Adult dry food twice daily at 8AM and 7PM', 3600.00, 'CONFIRMED');

-- Invoices
INSERT INTO `invoices` (`invoice_number`, `pet_id`, `owner_id`, `invoice_date`, `subtotal`, `tax_amount`, `discount_amount`, `total_amount`, `payment_method`, `payment_status`) VALUES
('INV-202610-001', 1, 1, '2026-10-05', 1200.00, 60.00, 0.00, 1260.00, 'UPI', 'PAID'),
('INV-202610-002', 4, 4, '2026-10-08', 950.00, 47.50, 50.00, 947.50, 'Cash', 'PAID');

-- Invoice Items
INSERT INTO `invoice_items` (`invoice_id`, `description`, `item_type`, `quantity`, `unit_price`, `total_price`) VALUES
(1, 'Deluxe Spa Bath + De-shedding + Ear Cleansing', 'GROOMING', 1, 1200.00, 1200.00),
(2, 'Veterinary Consultation & Ophthalmic Examination', 'CONSULTATION', 1, 500.00, 500.00),
(2, 'Meloxicam Oral Suspension 1.5mg/ml', 'MEDICINE', 1, 450.00, 450.00);
