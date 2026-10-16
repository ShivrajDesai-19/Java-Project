# 🐾 Smart Pet Care & Veterinary Management System

**A 100-Marker Major Academic Project for BCA Semester 3 (Programming using Java)**  
*Marwadi University Syllabus Alignment*

---

## 📌 1. Project Overview

**Smart Pet Care & Veterinary Management System** is a comprehensive Java-based desktop application designed to modernize the operations of veterinary clinics and pet care hospitals. Developed strictly following Object-Oriented Software Engineering (OOSE) principles, the system handles end-to-end clinical workflows including pet patient records, owner profiles, appointments, clinical diagnoses, vaccination tracking, laboratory tests, pharmacy inventory, grooming & boarding reservations, POS billing & tax invoicing, employee management, and executive analytics.

### 🌟 Key Features
- **Secure Role-Based Access Control (RBAC):** Distinct dashboards and access limits for **Administrator**, **Veterinarian**, **Receptionist**, and **Staff**.
- **Pet Owner & Pet Management:** Comprehensive demographic and medical profiles including species, breed, microchip ID, allergies, and blood group.
- **Appointment Scheduling & Calendar Queue:** Multi-doctor appointment booking, time-slot management, and status updates (Scheduled, In Progress, Completed, Cancelled).
- **Clinical Consultations & Diagnosis Records:** Symptom logs, vital signs monitoring (Temperature, Pulse Rate), and treatment prescriptions.
- **Vaccination Tracker & Immunization Alerts:** Next due date booster tracking with automatic reminder generation.
- **Diagnostic Laboratory Reports:** Hematology, Biochemistry, Radiology/X-Ray, and Urinalysis reports with normal reference ranges.
- **Pharmacy & Stock Inventory:** Batch numbers, expiry date checks, and automatic low-stock warnings powered by background multithreading.
- **Grooming & Boarding Services:** Kennel cage reservations with automatic day-rate calculation and grooming spa booking.
- **POS Billing & Official Tax Invoice:** Line-item calculations (Consultation + Pharmacy + Tests + Grooming), GST tax computation, and printable receipt dialogs.
- **Staff & Credential Administration:** Full CRUD employee management restricted to administrators.
- **Executive Analytics Dashboard:** Statistical breakdowns of patient species, appointment fulfillments, and revenue metrics.
- **Database Backup & Restore Utility:** Standalone SQL dump export and restore running on background worker threads without freezing the GUI.

---

## 🎓 2. BCA Sem-3 Syllabus Concept Mapping (Viva Preparation)

This project was intentionally engineered to cover **100% of the concepts** across all 4 units of the syllabus:

| Unit | Syllabus Concept | Code Implementation in Project |
| :--- | :--- | :--- |
| **Unit 1: OOP & Java Basics** | • Classes & Objects<br>• Default & Parameterized Constructors<br>• Constructor Overloading<br>• `this` keyword usage<br>• `static` variables, methods & static blocks<br>• Java `enum` data type<br>• Operators & Control Flow (`switch`, loops) | • Defined across all entities (`Pet`, `PetOwner`, `Medicine`, `Invoice`)<br>• Parameterized constructors in models, e.g. `Pet(...)` vs `Pet()`<br>• `this.name = name;` and `this(...)` constructor chaining<br>• `DBConnection.java` has a **static initializer block** `static { ... }` executing at classloading<br>• Enums with methods: `UserRole`, `AppointmentStatus`<br>• Enums utilized in switch statements (`UserDAO.java`, `BillingPanel.java`) |
| **Unit 2: Inheritance, Interfaces & Packages** | • Hierarchical Inheritance<br>• Multilevel Inheritance<br>• Single Inheritance<br>• Method Overriding (`@Override`)<br>• Method Overloading<br>• Abstract Classes & Methods<br>• Java Interfaces<br>• Multiple Interface Implementation<br>• `super` keyword<br>• `final` keyword (variables & constants)<br>• User-defined Packages<br>• `StringBuilder` / `StringBuffer` | • **Hierarchical Inheritance:** Base `User` class extended by `Administrator`, `Veterinarian`, `Receptionist`, `StaffMember`<br>• **Multilevel / Single:** `ServiceItem` extended by `BoardingBooking` and `GroomingBooking`<br>• **Abstract Class:** `MedicalRecord` has abstract methods `getRecordCategory()` and `getSummaryDescription()` overridden by `DiagnosisRecord`, `VaccinationRecord`, `LabTestRecord`<br>• **Interfaces:** `Billable` and `Searchable`<br>• **Multiple Interfaces:** `Medicine implements Billable, Searchable`<br>• **Method Overloading:** `PetOwnerDAO.search(keyword)` vs `search(name, phone)`<br>• **Packages:** Clean structure (`com.petcare.model`, `dao`, `service`, `ui`, `thread`, `util`, `exception`)<br>• **Receipt Formatting:** Uses `StringBuilder` in `ReceiptPrinter.java` |
| **Unit 3: Exception Handling** | • `try-catch-finally` blocks<br>• `throw` & `throws` keywords<br>• Custom Exceptions<br>• Chained Exceptions (Nesting exceptions)<br>• Transaction rollback handling | • Custom Exceptions: `PetCareException`, `AuthenticationException`, `DatabaseException`, `InvalidInputException`, `InsufficientStockException`, `RecordNotFoundException`<br>• **Chained Exceptions:** In `DBConnection.java`, wraps low-level `SQLException` inside `DatabaseException(msg, cause)`<br>• **Transaction Safety:** In `InvoiceDAO.java`, uses `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` inside `catch(SQLException e)` |
| **Unit 4: Multi-Threading & Concurrency** | • Extending `Thread` class<br>• Implementing `Runnable` interface<br>• Thread Lifecycle (`start()`, `sleep()`, `yield()`)<br>• Thread Priority (`setPriority`)<br>• `synchronized` methods<br>• Custom `ThreadGroup` management | • `StockAlertThread extends Thread`: Background daemon polling low-stock medicines<br>• `LiveClockThread extends Thread`: Updates dashboard clock every 1000ms<br>• `AppointmentReminderThread implements Runnable`: Checks today's appointments<br>• **Thread Priority:** `setPriority(Thread.NORM_PRIORITY)` and `Thread.MIN_PRIORITY`<br>• **Synchronization:** `MedicineDAO.dispenseMedicine()` is declared `synchronized` to prevent race conditions during concurrent stock deduction<br>• **ThreadGroup:** `ThreadGroup("SmartPetCareServicesGroup")` groups background threads and tracks `activeCount()` in the status bar |

---

## 🗄️ 3. Database & XAMPP Setup

### Step 1: Start XAMPP
1. Open the **XAMPP Control Panel**.
2. Click **Start** next to **MySQL** (Default Port: `3306`).
3. (Optional) Start **Apache** if you want to inspect tables in phpMyAdmin (`http://localhost/phpmyadmin`).

### Step 2: Database Initialization (Automatic!)
- The application automatically connects to MySQL, creates the database `smart_pet_care_db`, constructs all tables, and populates initial sample data on first run!
- You can also view or manually import the database script from:
  `sql/schema.sql`

---

## 🔑 4. Demo Login Credentials

For quick evaluation during the viva or lab presentation, the login screen includes **1-Click Demo Login** buttons:

| Role | Username | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access: All clinical panels, Staff management, Database backup/restore |
| **Veterinarian** | `dr.priya` | `vet123` | Patient profiles, Appointments, Diagnoses, Prescriptions, Lab tests |
| **Receptionist** | `receptionist` | `rec123` | Client registration, Pet registration, Appointments, POS Billing & Invoicing |
| **Staff Member** | `staff` | `staff123` | Inventory viewing, Stock dispensing, Grooming & Boarding reservations |

---

## 🚀 5. How to Compile and Run

### Option A: Using Batch Files (Windows 1-Click)
1. Double-click `compile.bat` to compile all source files into the `bin/` directory.
2. Double-click `build_and_run.bat` to start the application.

### Option B: Using Command Line (PowerShell / Command Prompt)
Navigate to the project root directory:
```powershell
# Compile all source files
javac -cp "lib/mysql-connector-j.jar" -d bin src/com/petcare/*.java src/com/petcare/*/*.java src/com/petcare/*/*/*.java

# Run the application
java -cp "bin;lib/mysql-connector-j.jar" com.petcare.Main
```

---

## 📁 6. Project Architecture

```
SmartPetCareSystem/
├── bin/                             # Compiled Java bytecode (.class files)
├── lib/
│   └── mysql-connector-j.jar        # MySQL JDBC Driver Connector
├── sql/
│   └── schema.sql                   # Complete relational database script
├── src/
│   └── com/petcare/
│       ├── Main.java                # Main entry point with CLI argument parsing
│       ├── model/                   # OOP Encapsulation, Enums, Interfaces, Inheritance
│       │   ├── UserRole.java
│       │   ├── User.java
│       │   ├── Administrator.java
│       │   ├── Veterinarian.java
│       │   ├── Receptionist.java
│       │   ├── StaffMember.java
│       │   ├── PetOwner.java
│       │   ├── Pet.java
│       │   ├── Appointment.java
│       │   ├── AppointmentStatus.java
│       │   ├── MedicalRecord.java (Abstract)
│       │   ├── DiagnosisRecord.java
│       │   ├── VaccinationRecord.java
│       │   ├── LabTestRecord.java
│       │   ├── Medicine.java
│       │   ├── ServiceItem.java
│       │   ├── GroomingBooking.java
│       │   ├── BoardingBooking.java
│       │   ├── Billable.java (Interface)
│       │   ├── Searchable.java (Interface)
│       │   ├── Invoice.java
│       │   └── InvoiceItem.java
│       ├── dao/                     # Data Access Object Layer
│       │   ├── GenericDAO.java (Generic Interface)
│       │   ├── UserDAO.java
│       │   ├── PetOwnerDAO.java
│       │   ├── PetDAO.java
│       │   ├── AppointmentDAO.java
│       │   ├── MedicalRecordDAO.java
│       │   ├── MedicineDAO.java
│       │   ├── ServiceBookingDAO.java
│       │   └── InvoiceDAO.java
│       ├── service/                 # Business Logic & Validation Layer
│       │   ├── AuthService.java
│       │   ├── PetManagementService.java
│       │   ├── AppointmentService.java
│       │   ├── MedicalService.java
│       │   ├── PharmacyService.java
│       │   ├── ServiceBookingService.java
│       │   ├── BillingService.java
│       │   └── ReportService.java
│       ├── thread/                  # Multithreading & Synchronization (Unit 4)
│       │   ├── StockAlertThread.java
│       │   ├── AppointmentReminderThread.java
│       │   ├── LiveClockThread.java
│       │   └── BackgroundServiceManager.java
│       ├── exception/               # Custom & Chained Exceptions (Unit 3)
│       │   ├── PetCareException.java
│       │   ├── AuthenticationException.java
│       │   ├── DatabaseException.java
│       │   ├── InvalidInputException.java
│       │   ├── InsufficientStockException.java
│       │   └── RecordNotFoundException.java
│       ├── util/                    # Utilities & UI Helpers
│       │   ├── DBConnection.java (Singleton & Static Initializer)
│       │   ├── InputValidator.java
│       │   ├── ReceiptPrinter.java (StringBuilder & Printing)
│       │   ├── DatabaseBackupUtil.java (Java IO)
│       │   └── UIStyleUtil.java
│       └── ui/                      # Swing GUI Forms, Panels & Dialogs
│           ├── LoginFrame.java
│           ├── MainDashboardFrame.java
│           ├── panels/
│           │   ├── OverviewPanel.java
│           │   ├── OwnersPanel.java
│           │   ├── PetsPanel.java
│           │   ├── AppointmentsPanel.java
│           │   ├── MedicalHistoryPanel.java
│           │   ├── PharmacyPanel.java
│           │   ├── ServicesPanel.java
│           │   ├── BillingPanel.java
│           │   ├── StaffPanel.java
│           │   ├── ReportsPanel.java
│           │   └── BackupRestorePanel.java
│           └── dialogs/
│               ├── DBConfigDialog.java
│               ├── OwnerDialog.java
│               ├── PetDialog.java
│               ├── AppointmentDialog.java
│               ├── MedicalRecordDialog.java
│               ├── MedicineDialog.java
│               ├── ServiceBookingDialog.java
│               └── StaffDialog.java
├── compile.bat
├── build_and_run.bat
└── README.md
```
