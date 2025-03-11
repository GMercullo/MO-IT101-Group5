package com.mycompany.mtrph;

// Import necessary libraries for file handling, date manipulation, and formatting
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @author gmmercullo
 */
public class MTRPH {

    // Formatter for displaying numerical values in a readable format
    private static final DecimalFormat dfNumber = new DecimalFormat("#,##0.00");
    
    // File paths and sheet names for employee data and attendance records
    private static final String EMPLOYEE_DATA_FILE = "src/Copy of MotorPH Employee Data.xlsx";
    private static final String EMPLOYEE_SHEET_NAME = "Employee Details";
    private static final String ATTENDANCE_SHEET_NAME = "Attendance Record";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        login(scanner); // Handle login process
        mainMenu(scanner); // Display main menu options
        scanner.close(); // Close the scanner after the program ends
    }

    /**
     * Handles user login with username and password validation.
     */
    private static void login(Scanner scanner) {
        while (true) {
            System.out.println("MotorPH Payroll Management System");
            System.out.print("User: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (username.equals("admin") && password.equals("password123")) {
                System.out.println("\n✅ Login Successful!\n");
                System.out.println("MotorPH Payroll Management System\nWelcome " + username + "!\n");
                break;
            } else {
                System.out.println("❌ Incorrect user or password. Please try again.");
            }
        }
    }

     /**
     * Displays the main menu options for the payroll system.
     */
    private static void mainMenu(Scanner scanner) {
        while (true) {
            System.out.println("(0) Log Out");
            System.out.println("(1) Search Employee");
            System.out.println("(2) Add Employee");
            System.out.println("(3) Edit Employee Details");
            System.out.println("(4) Delete Employee");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 0:
                    System.out.println("Logging out..."); // Log out
                    return;
                case 1:
                    searchEmployee(scanner);
                    break;
                case 2:
                    addEmployee(scanner);
                    break;
                case 3:
                    editEmployeeDetails(scanner);
                    break;
                case 4:
                    deleteEmployee(scanner);
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

     /**
     * Searches for an employee by name and displays their details if found.
     */
    private static void searchEmployee(Scanner scanner) {
        System.out.print("Employee Name: ");
        String searchEmployeeName = scanner.nextLine().trim();

        try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
            if (employeeSheet == null) {
                System.out.println("❌ Sheet '" + EMPLOYEE_SHEET_NAME + "' not found!");
                return;
            }

            boolean found = false;
            for (Row row : employeeSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String fullName = getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1));

                if (fullName.equalsIgnoreCase(searchEmployeeName)) {
                    displayEmployeeDetails(row, scanner); // Display employee details
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ Employee not Found. Please try again.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }
    }

    private static void displayEmployeeDetails(Row row, Scanner scanner) {
        DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

         /**
        * Displays detailed employee information and provides options for further actions.
        */
        // Parse the birthday properly from the cell
        LocalDate birthday = parseDateFromCell(row.getCell(3));
        System.out.println("==============================================");
        System.out.println("\nMotorPH Payroll Management System");
        System.out.println("==============================================");
        System.out.println("Employee Name: "   + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
        System.out.println("Employee Number: " + getCellValue(row.getCell(0)));
        System.out.println("Birthday:        " + birthdayFormat.format(birthday));
        System.out.println("Position:        " + getCellValue(row.getCell(11)));
        System.out.println("Immediate Supervisor: " + getCellValue(row.getCell(12)));
        System.out.println("Status:          " + getCellValue(row.getCell(10)));
        System.out.println("Address:         " + getCellValue(row.getCell(4)));
        System.out.println("Phone #:         " + getCellValue(row.getCell(5)));
        System.out.println("SSS #:           " + getCellValue(row.getCell(6)));
        System.out.println("PhilHealth #:    " + getCellValue(row.getCell(7)));
        System.out.println("TIN #:           " + getCellValue(row.getCell(8)));
        System.out.println("Pag-IBIG #:      " + getCellValue(row.getCell(9)));
        System.out.println("==============================================");
 
        // Options for further actions on the selected employee
        System.out.println("(0) Home Page");
        System.out.println("(1) Compute Weekly Salary");
        System.out.println("(2) Edit Employee Details");
        System.out.println("(3) Delete Employee");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 0:
                return; // Go back to home page
            case 1:
                computeWeeklySalary(row, scanner); // Calculate Weekly Salary
                break;
            case 2:
                editEmployeeDetails(scanner); // Edit the employee details
                break;
            case 3:
                deleteEmployee(scanner); // Delete employee to the data
                break;
            default:
                System.out.println("❌ Invalid choice.");
        }
    }
    
     /**
     * Edit the employee details from the data
     */
    private static void editEmployeeDetails(Scanner scanner) {
        System.out.print("Enter Employee Number to Edit: "); // enter the employee number that wanted to edit
        String employeeNumber = scanner.nextLine().trim();

        try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
            if (employeeSheet == null) {
                System.out.println("❌ Sheet '" + EMPLOYEE_SHEET_NAME + "' not found!");
                return;
            }

            boolean found = false;
            for (Row row : employeeSheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String existingEmployeeNumber = getCellValue(row.getCell(0));
                if (existingEmployeeNumber.equals(employeeNumber)) {
                    found = true;

                    System.out.println("\n✅ Employee Found! Please enter new details:");

                    System.out.print("Enter New Last Name (Press Enter to keep current): ");
                    String newLastName = scanner.nextLine().trim();
                    if (!newLastName.isEmpty()) row.getCell(1).setCellValue(newLastName);

                    System.out.print("Enter New First Name (Press Enter to keep current): ");
                    String newFirstName = scanner.nextLine().trim();
                    if (!newFirstName.isEmpty()) row.getCell(2).setCellValue(newFirstName);

                    System.out.print("Enter New Birthday (MM/dd/yyyy) (Press Enter to keep current): ");
                    String newBirthday = scanner.nextLine().trim();
                    if (!newBirthday.isEmpty()) row.getCell(3).setCellValue(newBirthday);

                    System.out.print("Enter New Address (Press Enter to keep current): ");
                    String newAddress = scanner.nextLine().trim();
                    if (!newAddress.isEmpty()) row.getCell(4).setCellValue(newAddress);

                    System.out.print("Enter New Phone Number (Press Enter to keep current): ");
                    String newPhoneNumber = scanner.nextLine().trim();
                    if (!newPhoneNumber.isEmpty()) row.getCell(5).setCellValue(newPhoneNumber);

                    System.out.print("Enter New Position (Press Enter to keep current): ");
                    String newPosition = scanner.nextLine().trim();
                    if (!newPosition.isEmpty()) row.getCell(11).setCellValue(newPosition);

                    System.out.print("Enter New Immediate Supervisor (Press Enter to keep current): ");
                    String newSupervisor = scanner.nextLine().trim();
                    if (!newSupervisor.isEmpty()) row.getCell(12).setCellValue(newSupervisor);

                    System.out.print("Enter New Basic Salary (Press Enter to keep current): ");
                    String newBasicSalary = scanner.nextLine().trim();
                    if (!newBasicSalary.isEmpty()) row.getCell(13).setCellValue(Double.parseDouble(newBasicSalary));

                    System.out.print("Enter New Rice Subsidy (Press Enter to keep current): ");
                    String newRiceSubsidy = scanner.nextLine().trim();
                    if (!newRiceSubsidy.isEmpty()) row.getCell(14).setCellValue(Double.parseDouble(newRiceSubsidy));

                    System.out.print("Enter New Phone Allowance (Press Enter to keep current): ");
                    String newPhoneAllowance = scanner.nextLine().trim();
                    if (!newPhoneAllowance.isEmpty()) row.getCell(15).setCellValue(Double.parseDouble(newPhoneAllowance));

                    System.out.print("Enter New Clothing Allowance (Press Enter to keep current): ");
                    String newClothingAllowance = scanner.nextLine().trim();
                    if (!newClothingAllowance.isEmpty()) row.getCell(16).setCellValue(Double.parseDouble(newClothingAllowance));

                    try (FileOutputStream fos = new FileOutputStream(EMPLOYEE_DATA_FILE)) {
                        workbook.write(fos);
                        System.out.println("\n✅ Employee details successfully updated.");
                    }
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ Employee not found.");
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("❌ Error updating employee details: " + e.getMessage());
        }
    }
    
    /**
     * Adding new employee and its details from the data
     */
    private static void addEmployee(Scanner scanner) {
        System.out.print("Enter Employee Number: ");
        String employeeNumber = scanner.nextLine().trim();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter Birthday (MM/dd/yyyy): ");
        String birthday = scanner.nextLine().trim();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();
        System.out.print("Enter SSS Number: ");
        String sssNumber = scanner.nextLine().trim();
        System.out.print("Enter PhilHealth Number: ");
        String philHealthNumber = scanner.nextLine().trim();
        System.out.print("Enter TIN Number: ");
        String tinNumber = scanner.nextLine().trim();
        System.out.print("Enter Pag-IBIG Number: ");
        String pagIbigNumber = scanner.nextLine().trim();
        System.out.print("Enter Employment Status: ");
        String status = scanner.nextLine().trim();
        System.out.print("Enter Position: ");
        String position = scanner.nextLine().trim();
        System.out.print("Enter Immediate Supervisor: ");
        String supervisor = scanner.nextLine().trim();
        System.out.print("Enter Basic Salary: ");
        double basicSalary = scanner.nextDouble();
        System.out.print("Enter Rice Subsidy: ");
        double riceSubsidy = scanner.nextDouble();
        System.out.print("Enter Phone Allowance: ");
        double phoneAllowance = scanner.nextDouble();
        System.out.print("Enter Clothing Allowance: ");
        double clothingAllowance = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
            if (employeeSheet == null) {
                System.out.println("❌ Sheet '" + EMPLOYEE_SHEET_NAME + "' not found!");
                return;
            }

            // Create a new row for the employee
            int lastRowNum = employeeSheet.getLastRowNum() + 1;
            Row newRow = employeeSheet.createRow(lastRowNum);

            newRow.createCell(0).setCellValue(employeeNumber);
            newRow.createCell(1).setCellValue(lastName);
            newRow.createCell(2).setCellValue(firstName);
            newRow.createCell(3).setCellValue(birthday);
            newRow.createCell(4).setCellValue(address);
            newRow.createCell(5).setCellValue(phoneNumber);
            newRow.createCell(6).setCellValue(sssNumber);
            newRow.createCell(7).setCellValue(philHealthNumber);
            newRow.createCell(8).setCellValue(tinNumber);
            newRow.createCell(9).setCellValue(pagIbigNumber);
            newRow.createCell(10).setCellValue(status);
            newRow.createCell(11).setCellValue(position);
            newRow.createCell(12).setCellValue(supervisor);
            newRow.createCell(13).setCellValue(basicSalary);
            newRow.createCell(14).setCellValue(riceSubsidy);
            newRow.createCell(15).setCellValue(phoneAllowance);
            newRow.createCell(16).setCellValue(clothingAllowance);

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(EMPLOYEE_DATA_FILE)) {
                workbook.write(fos);
                System.out.println("✅ Employee successfully added.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error writing file: " + e.getMessage());
        }
    }

     /**
     * Delete employee details from the data
     */
    private static void deleteEmployee(Scanner scanner) {
        System.out.print("Enter Employee Number to Delete: "); // enter the employees number that wanted to be deleted
        String employeeNumber = scanner.nextLine().trim();

        try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
            if (employeeSheet == null) {
                System.out.println("❌ Sheet '" + EMPLOYEE_SHEET_NAME + "' not found!");
                return;
            }

            boolean found = false;
            int rowIndexToDelete = -1;

            // Find the employee by employee number
            for (Row row : employeeSheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                if (getCellValue(row.getCell(0)).equals(employeeNumber)) {
                    rowIndexToDelete = row.getRowNum();
                    found = true;
                    break;
                }
            }

            if (found && rowIndexToDelete != -1) {
                employeeSheet.removeRow(employeeSheet.getRow(rowIndexToDelete));

                // Shift rows up to maintain structure
                int lastRowNum = employeeSheet.getLastRowNum();
                if (rowIndexToDelete >= 0 && rowIndexToDelete < lastRowNum) {
                    employeeSheet.shiftRows(rowIndexToDelete + 1, lastRowNum, -1);
                }

                // Save changes
                try (FileOutputStream fos = new FileOutputStream(EMPLOYEE_DATA_FILE)) {
                    workbook.write(fos);
                    System.out.println("✅ Employee successfully deleted.");
                }
            } else {
                System.out.println("❌ Employee not found.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error accessing file: " + e.getMessage());
        }
    }

     /**
     * Compute employee weekly salary
     */
    private static void computeWeeklySalary(Row row, Scanner scanner) {
    System.out.print("Enter Week Number (1-31): ");
    int weekNumber = scanner.nextInt();
    scanner.nextLine();

    LocalDate weekStartDate = LocalDate.of(2024, 6, 3).plusWeeks(weekNumber - 1);

    // Change the week to only span 5 working days
    LocalDate weekEndDate = weekStartDate.plusDays(4); 

    // Ensure the week does not overlap into the next month
    if (weekEndDate.getMonthValue() != weekStartDate.getMonthValue()) {
        weekEndDate = weekStartDate.withDayOfMonth(weekStartDate.lengthOfMonth());
    }

    double basicSalary = Double.parseDouble(getCellValue(row.getCell(13)).replaceAll("[^0-9.]", ""));
    double hourlyRate = (basicSalary / 21) / 8;

    double totalRegularHours = 0;
    double totalOvertimeHours = 0;
    double overtimeRatePercentage = 1.25;

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime otStart = LocalTime.of(8, 0);
    LocalTime otEnd = LocalTime.of(8, 10);

    try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
         Workbook workbook = new XSSFWorkbook(fis)) {

        Sheet attendanceSheet = workbook.getSheet(ATTENDANCE_SHEET_NAME);
        if (attendanceSheet == null) {
            System.out.println("❌ Sheet '" + ATTENDANCE_SHEET_NAME + "' not found!");
            return;
        }

        for (Row attendanceRow : attendanceSheet) {
            if (attendanceRow.getRowNum() == 0) continue;

            String fullName = getCellValue(attendanceRow.getCell(2)) + " " + getCellValue(attendanceRow.getCell(1));
            if (fullName.equalsIgnoreCase(getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)))) {
                LocalDate date = parseDateFromCell(attendanceRow.getCell(3));
                if (date.isBefore(weekStartDate) || date.isAfter(weekEndDate)) continue;

                LocalTime logIn = readTimeCell(attendanceRow.getCell(4), timeFormatter);
                LocalTime logOut = readTimeCell(attendanceRow.getCell(5), timeFormatter);

                double dailyHours = ChronoUnit.MINUTES.between(logIn, logOut) / 60.0;

                dailyHours = Math.round(dailyHours * 10.0) / 10.0;

                boolean eligibleForOT = (logIn.equals(otStart) || (logIn.isAfter(otStart) && logIn.isBefore(otEnd)));

                dailyHours -= 1; // Deduct 1 hour for lunch

                if (dailyHours <= 8) {
                    totalRegularHours += dailyHours;
                } else {
                    totalRegularHours += 8;
                    totalOvertimeHours += dailyHours - 8;
                }
            }
        }

    } catch (IOException e) {
        System.out.println("❌ Error reading file: " + e.getMessage());
    }

    double regularPay = totalRegularHours * hourlyRate;
    double overtimePay = totalOvertimeHours * hourlyRate * overtimeRatePercentage;
    double weeklySalary = regularPay + overtimePay;
    
    double riceSubsidy = Double.parseDouble(getCellValue(row.getCell(14))) / 4.33;
    double phoneAllowance = Double.parseDouble(getCellValue(row.getCell(15))) / 4.33;
    double clothingAllowance = Double.parseDouble(getCellValue(row.getCell(16))) / 4.33;
    
    double sss = calculateSSS(weeklySalary);
    double philHealth = calculatePhilHealth(weeklySalary);
    double pagIbig = calculatePagIbig(weeklySalary);
    double withholdingTax = calculateWithholdingTax(weeklySalary, sss, philHealth, pagIbig);

    double deductionSum = weeklySalary - sss - philHealth - pagIbig - withholdingTax;
    double netSalary = deductionSum + riceSubsidy + phoneAllowance + clothingAllowance;
    DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    // Parse the birthday properly from the cell
    LocalDate birthday = parseDateFromCell(row.getCell(3));

    System.out.println("==============================================");
    System.out.println("MotorPH Payroll Summary");
    System.out.println("==============================================");
    System.out.println("Employee Number: " + getCellValue(row.getCell(0)));
    System.out.println("Employee Name:   " + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
    System.out.println("Birthday:        " + birthdayFormat.format(birthday));
    System.out.println("----------------------------------------------");
    System.out.println("Week Number:     " + weekNumber);
    System.out.println("Period:          " + weekStartDate + " to " + weekEndDate);
    System.out.println("Total Regular Hours: " + dfNumber.format(totalRegularHours));
    System.out.println("Overtime Hours:  " + dfNumber.format(totalOvertimeHours));
    System.out.println("----------------------------------------------");
    System.out.println("Basic Salary:    ₱ " + dfNumber.format(basicSalary));
    System.out.println("Hourly Rate:     ₱ " + dfNumber.format(hourlyRate));
    System.out.println("Gross Salary:    ₱ " + dfNumber.format(weeklySalary));
    System.out.println("SSS Deduction:   ₱ " + dfNumber.format(sss));
    System.out.println("PAG-IBIG:        ₱ " + dfNumber.format(pagIbig));
    System.out.println("PhilHealth:      ₱ " + dfNumber.format(philHealth));
    System.out.println("Withholding Tax: ₱ " + dfNumber.format(withholdingTax));
    System.out.println("Rice Subsidy:    ₱ " + dfNumber.format(riceSubsidy));
    System.out.println("Phone Allowance: ₱ " + dfNumber.format(phoneAllowance));
    System.out.println("Clothing Allowance: ₱ "+ dfNumber.format(clothingAllowance));
    System.out.println("----------------------------------------------");
    System.out.println("Net Salary:      ₱ " + dfNumber.format(netSalary));
    System.out.println("==============================================");
    System.out.println("(0) Home Page");
    System.out.println("(1) Compute Other Weekly Salary");
    System.out.println("(2) Edit Employee Details");
    System.out.println("(3) Delete Employee");
    System.out.print("Enter your choice: ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
        case 0:
            return;
    case 1:
        computeWeeklySalary(row, scanner);
        break;
    case 2:
        editEmployeeDetails(scanner);
        break;
    case 4:
        deleteEmployee(scanner);
        break;
    default:
        System.out.println("❌ Invalid choice. Please try again.");
    }
  }  

    private static LocalDate parseDateFromCell(Cell cell) {
        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else {
                String text = getCellValue(cell).trim();
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("M/d/yyyy"));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error parsing date: " + e.getMessage());
            return LocalDate.now(); // Returning current date or you can return null based on your need
        }
    }

        // ===================== CALCULATION METHODS =====================
        private static LocalTime readTimeCell(Cell cell, DateTimeFormatter formatter) {
        if (cell == null) return LocalTime.MIDNIGHT; // Or some other default time value
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalTime();
        } else {
        try {
            return LocalTime.parse(getCellValue(cell).trim(), formatter);
        } catch (Exception e) {
            System.out.println("❌ Invalid time format");
            return LocalTime.MIDNIGHT; // Or another default value
        }
    }
}
    
    private static double calculateSSS(double weeklySalary) {
        double monthlySalary = weeklySalary * 4.33;
        return (monthlySalary <= 3250) ? 135.00 : 1125.00;
    }

    private static double calculatePhilHealth(double weeklySalary) {
        double monthlySalary = weeklySalary * 4.33;
        double premium = (monthlySalary <= 10000) ? 300 : (monthlySalary >= 60000) ? 1800 : monthlySalary * 0.03;
        return (premium / 2) / 4.33;
    }

    private static double calculatePagIbig(double weeklySalary) {
        double monthlySalary = weeklySalary * 4.33;
        double contributionRate = (monthlySalary <= 1500) ? 0.01 : 0.02;
        return (monthlySalary * contributionRate) / 4.33;
    }

    private static double calculateWithholdingTax(double weeklySalary, double sss, double philHealth, double pagIbig) {
        double monthlySalary = weeklySalary * (30.42 / 7); 
        double totalDeductions = sss + philHealth + pagIbig;
        double taxableIncome = monthlySalary - totalDeductions;

        double calculatedTax = 0;

        if (taxableIncome <= 20833) {
            calculatedTax = 0.0;
        } else if (taxableIncome <= 33333) {
            calculatedTax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            calculatedTax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            calculatedTax = 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            calculatedTax = 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            calculatedTax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }
        return 0;
    }
    
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
            return (cell.getNumericCellValue() % 1 == 0) ? String.valueOf((long) cell.getNumericCellValue()) : String.valueOf(cell.getNumericCellValue());
        default:
            return "";
        }
    }

}
