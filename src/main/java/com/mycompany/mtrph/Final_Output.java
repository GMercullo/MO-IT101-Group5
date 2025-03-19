# Payroll Management System

## Overview
This Java-based Payroll Management System calculates employees' wages, considering overtime, tax deductions, and benefits. The system supports both **full-time** and **part-time** employees, with different salary and tax calculations.

## Features
- Employee management (Full-time & Part-time employees)
- Hourly wage calculations with overtime rules
- Automatic tax and benefits deductions
- Persistent storage (employee data saved to file)
- Logging for error tracking and progress monitoring
- Unit tests for validation

## Installation & Setup
1. **Clone the Repository:**
   ```sh
   git clone https://github.com/GMercullo/MO-IT101-Group5.git
   cd MO-IT101-Group5
   ```
2. **Compile and Run:**
   ```sh
   javac -d bin src/com/project/*.java
   java -cp bin com.project.PayrollApp
   ```

## Usage
- **Adding Employees:**
  Employees are added as either full-time or part-time with:
  ```java
  app.addFullTimeEmployee(1, "Alice", 20.5);
  app.addPartTimeEmployee(2, "Bob", 18.0);
  ```
- **Logging Work Hours:**
  ```java
  app.addHoursWorked(1, 45); // Adds hours for Alice
  app.addHoursWorked(2, 38); // Adds hours for Bob
  ```
- **Viewing Payroll:**
  ```java
  app.listEmployees();
  ```

## Testing
Run built-in **unit tests** by executing the application. Assertions verify salary calculations:
```java
assert new FullTimeEmployee(1, "Alice", 20.0).calcGrossSalary() == 800.0;
assert new PartTimeEmployee(2, "Bob", 18.0).calcGrossSalary() == 720.0;
```

## File Storage
- Employee data is saved in `employees.dat`
- Reloads on startup to ensure persistence

## Logging & Monitoring
- Logs activities and errors using `java.util.logging`
- Provides debugging information and payroll progress updates

## Future Enhancements
- Database support (MySQL/PostgreSQL integration)
- Graphical User Interface (GUI) using JavaFX
- Multi-threading for large-scale payroll processing

## Authors
- **MO-IT101 Group 5** - Payroll System Development Team

## License
MIT License (Open Source). Feel free to contribute!
