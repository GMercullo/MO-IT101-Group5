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
import org.apache.poi.ss.usermodel.Row;
import java.util.List;

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
    private static String weekNumber;
    private static boolean weekEndDate;
    private static String weekStartDate;

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
    public static void mainMenu(Scanner scanner) {
        while (true) {
            System.out.println("(0) Log Out");
            System.out.println("(1) Search Employee");
            System.out.println("(2) Display All Employees");
            System.out.println("(3) Add Employee");
            System.out.println("(4) Delete Employee");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 0:
                    System.out.println("Logging out...");
                    return;
                case 1:
                    Employee.searchEmployee(scanner);
                    break;
                case 2:
                    System.out.println("Displaying all employees...");
                    Employee.displayAllEmployees();
                    break;
                case 3:
                    Employee.addEmployee(scanner);
                    break;
                case 4:
                    Employee.deleteEmployee(scanner);
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
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