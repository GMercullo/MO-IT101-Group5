package com.mycompany.mtrph;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class Employee {
    private static final String EMPLOYEE_DATA_FILE = "src/Copy of MotorPH Employee Data.xlsx";
    private static final String EMPLOYEE_SHEET_NAME = "Employee Details";
    
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String position;
    private String supervisor;
    private String status;
    private String address;
    private String phone;
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;

    /**
     * Constructor to initialize Employee object.
     */
    public Employee(String employeeNumber, String firstName, String lastName, LocalDate birthday, String position,
                    String supervisor, String status, String address, String phone, String sssNumber,
                    String philHealthNumber, String tinNumber, String pagIbigNumber) {
        this.employeeNumber = employeeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.position = position;
        this.supervisor = supervisor;
        this.status = status;
        this.address = address;
        this.phone = phone;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
    }

/**
     * Displays all employees and their details.
     */
    public static void displayAllEmployees() {
        try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
            if (employeeSheet == null) {
                System.out.println("❌ Employee data sheet not found!");
                return;
            }
            System.out.println("==============================================");
            System.out.println("MotorPH Payroll Management System");
            System.out.println("==============================================");

            DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            for (Row row : employeeSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                if (getCellValue(row.getCell(0)).isEmpty() && getCellValue(row.getCell(2)).isEmpty()) {
                    continue;
                }

                LocalDate birthday = parseDateFromCell(row.getCell(3));

                // Display employee details
                System.out.println("Employee Name: " + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
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
           }

        } catch (IOException e) {
            System.out.println("❌ Error reading employee data: " + e.getMessage());
        }
    }
    
    /**
     * Searches for an employee by name.
     */
    /**
     * Searches for an employee by name.
     */
    public static void searchEmployee(Scanner scanner) {
        System.out.print("Employee Name: ");
        String searchEmployeeName = scanner.nextLine().trim();
        System.out.println("Searching employee...");

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
                    displayEmployeeDetails(row, scanner);
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

    /**
     * Displays detailed employee information.
     */
    private static void displayEmployeeDetails(Row row, Scanner scanner) {
        DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthday = parseDateFromCell(row.getCell(3));
        System.out.println();
        System.out.println("==============================================");
        System.out.println("MotorPH Payroll Management System");
        System.out.println("==============================================");
        System.out.println("Employee Name: " + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
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
        System.out.println("(0) Home Page");
        System.out.println("(1) Compute Weekly Salary");
        System.out.println("(2) Edit Employee Details");
        System.out.println("(3) Delete Employee");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 0:
                return;
            case 1:
                PayrollCalculator.calcWeeklySalary(row, scanner);
                break;
            case 2:
                editEmployeeDetails(scanner);
                break;
            case 3:
                deleteEmployee(scanner);
                break;
            default:
                System.out.println("❌ Invalid choice. Please try again.");
        }
    }
    /**
     * Edit the employee details from the data
     */
    public static void editEmployeeDetails(Scanner scanner) {
        System.out.print("Enter Employee  Full Name to edit: "); // enter the employee name that wanted to edit
        String FullName = scanner.nextLine().trim();

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

                String existingfullName = getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1));
                if (existingfullName.equals(existingfullName)) {
                    found = true;
                    System.out.println("Searching employee...");
                    System.out.println();
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
                        System.out.println("Updating Employee Details.....");
                        System.out.println();
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
    public static void addEmployee(Scanner scanner) {
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
    public static void deleteEmployee(Scanner scanner) {
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
     * Helper method to get the cell value as a string.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Parses a date from an Excel cell.
     */
    private static LocalDate parseDateFromCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return null;
        return cell.getLocalDateTimeCellValue().toLocalDate();
    }
}
