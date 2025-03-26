package com.mycompany.mtrph;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.time.LocalTime;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class PayrollCalculator {

    private static final Logger logger = Logger.getLogger(PayrollCalculator.class.getName());

    // Method for reading employee data
    private static final String EMPLOYEE_DATA_FILE = "src/Copy of MotorPH Employee Data.xlsx";
    private static final String ATTENDANCE_SHEET_NAME = "Attendance Record";
    private static final String EMPLOYEE_SHEET_NAME = "Employee Details";
    public static List<Row> readEmployeeData(String fileName, String sheetName) {
        List<Row> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.severe("❌ Sheet '" + sheetName + "' not found!");
                return rows;
            }

            for (Row row : sheet) {
                rows.add(row);
            }
        } catch (IOException e) {
            logger.severe("❌ Error reading employee file: " + e.getMessage());
        }
        return rows;
    }

    // Method for reading attendance data
    public static List<Row> readAttendanceData(String fileName, String sheetName) {
        List<Row> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.severe("❌ Sheet '" + sheetName + "' not found!");
                return rows;
            }

            for (Row row : sheet) {
                rows.add(row);
            }
        } catch (IOException e) {
            logger.severe("❌ Error reading attendance file: " + e.getMessage());
        }
        return rows;
    }
    public class PayrollSystem {

        private static final String EMPLOYEE_DATA_FILE = "employee_data.xlsx";
        private static final String ATTENDANCE_SHEET_NAME = "Attendance Record";
        private static final String EMPLOYEE_SHEET_NAME = "Employee Details";
        private static final DateTimeFormatter dfNumber = DateTimeFormatter.ofPattern("###,###.##");
        
    }
    // Calculates Weekly Salary
    public static void calcWeeklySalary(Row row, Scanner scanner) {
        while (true) {
            System.out.print("Enter Week Number (1-31): ");
            int weekNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Calculating Weekly Salary...");
            LocalDate weekStartDate = LocalDate.of(2024, 6, 3).plusWeeks(weekNumber - 1);
            LocalDate weekEndDate = weekStartDate.plusDays(4);

            if (weekEndDate.getMonthValue() != weekStartDate.getMonthValue()) {
                weekEndDate = weekStartDate.withDayOfMonth(weekStartDate.lengthOfMonth());
            }
            
            // === Basic Salary and Hourly Rate Calculation ===
            Object cellValue = getCellValue(row.getCell(13));
            double basicSalary = 0.0;
            if (cellValue instanceof Double) {
                basicSalary = (Double) cellValue;  // Directly use it as a double
            } else if (cellValue instanceof String) {
                basicSalary = Double.parseDouble((String) cellValue);  // Parse string to double if needed
            } else {
                // Handle other types or errors here
                System.out.println("Unexpected cell value type: " + cellValue.getClass());
            }
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

            // === Salary Calculation ===
            double regularPay = totalRegularHours * hourlyRate;
            double overtimePay = totalOvertimeHours * hourlyRate * overtimeRatePercentage;
            double weeklySalary = regularPay + overtimePay;


            cellValue = getCellValue(row.getCell(14));  // Rice Subsidy
            double riceSubsidy = 0.0;
            if (cellValue instanceof String) {
                riceSubsidy = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                riceSubsidy = (Double) cellValue;
            }
            riceSubsidy /= 4.33;

            cellValue = getCellValue(row.getCell(15));  // Phone Allowance
            double phoneAllowance = 0.0;
            if (cellValue instanceof String) {
                phoneAllowance = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                phoneAllowance = (Double) cellValue;
            }
            phoneAllowance /= 4.33;

            cellValue = getCellValue(row.getCell(16));  // Clothing Allowance
            double clothingAllowance = 0.0;
            if (cellValue instanceof String) {
                clothingAllowance = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                clothingAllowance = (Double) cellValue;
            }
            clothingAllowance /= 4.33;

            double sss = calculateSSS(weeklySalary);
            double philHealth = calculatePhilHealth(weeklySalary);
            double pagIbig = calculatePagIbig(weeklySalary);
            double withholdingTax = calculateWithholdingTax(weeklySalary, sss, philHealth, pagIbig);

            double deductionSum = weeklySalary - sss - philHealth - pagIbig - withholdingTax;
            double netSalary = deductionSum + riceSubsidy + phoneAllowance + clothingAllowance;
            DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Parse the birthday properly from the cell
            LocalDate birthday = parseDateFromCell(row.getCell(3));
            
            // Create a DecimalFormat instance
            DecimalFormat df = new DecimalFormat("#,###.00");

            // === Display Results ===
            System.out.println("==============================================");
            System.out.println("MotorPH Payroll Summary");
            System.out.println("==============================================");
            System.out.println("Employee Number: " + getCellValue(row.getCell(0)));
            System.out.println("Employee Name:   " + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
            System.out.println("Birthday:        " + birthdayFormat.format(birthday));
            System.out.println("----------------------------------------------");
            System.out.println("Week Number:     " + weekNumber);
            System.out.println("Period:          " + weekStartDate + " to " + weekEndDate);
            System.out.println("Total Regular Hours: " + totalRegularHours);
            System.out.println("Overtime Hours:  " + df.format(totalOvertimeHours));            
            System.out.println("----------------------------------------------");
            System.out.println("Basic Salary:    ₱ " + df.format(basicSalary));
            System.out.println("Hourly Rate:     ₱ " + df.format(hourlyRate));
            System.out.println("Gross Salary:    ₱ " + df.format(weeklySalary));
            System.out.println("SSS Deduction:   ₱ " + df.format(sss));
            System.out.println("PAG-IBIG:        ₱ " + df.format(pagIbig));
            System.out.println("PhilHealth:      ₱ " + df.format(philHealth));
            System.out.println("Withholding Tax: ₱ " + df.format(withholdingTax));
            System.out.println("Rice Subsidy:    ₱ " + df.format(riceSubsidy));
            System.out.println("Phone Allowance: ₱ " + df.format(phoneAllowance));
            System.out.println("Clothing Allowance: ₱ "+ df.format(clothingAllowance));
            System.out.println("----------------------------------------------");
            System.out.println("Net Salary:      ₱ " + df.format(netSalary));
            System.out.println("==============================================");
            System.out.println("(0) Home Page");
            System.out.println("(1) Compute Other Weekly Salary");
            System.out.println("(2) Edit Employee Details");
            System.out.println("(3) Delete Employee");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // Handling user's choice
            switch (choice) {
                case 0:
                    return;
                case 1:
                    break;
                case 2:
                    Employee.editEmployeeDetails(scanner);
                    break;
                case 3:
                    Employee.deleteEmployee(scanner);
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
                    break;
            }
        }
    }


        // Method for getting cell value
        private static String getCellValue(Cell cell) {
            if (cell == null) return ""; // Return empty string for null cells

            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // If it's a date, format it properly
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                        return cell.getLocalDateTimeCellValue().format(formatter);
                    } else {
                        // Convert numeric value to string without scientific notation
                        return String.valueOf((long) cell.getNumericCellValue());
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                default:
                    return "";
            }
        }
    
    public static void displayAllEmployeesWithWeeklySalary(Scanner scanner) {
    System.out.print("Enter Week Number (1-31): ");
    int weekNumber = scanner.nextInt();
    scanner.nextLine();
        
    if (weekNumber < 1 || weekNumber > 31) {
        System.out.println("❌ Invalid week number! Please enter a value between 1 and 31.");
        return;
    }
    System.out.println("Calculating Weekly Salaries...");
    LocalDate weekStartDate = LocalDate.of(2024, 6, 3).plusWeeks(weekNumber - 1);
    LocalDate weekEndDate = weekStartDate.plusDays(4);

    if (weekEndDate.getMonthValue() != weekStartDate.getMonthValue()) {
        weekEndDate = weekStartDate.withDayOfMonth(weekStartDate.lengthOfMonth());
    }

    try (FileInputStream fis = new FileInputStream(EMPLOYEE_DATA_FILE);
         Workbook workbook = new XSSFWorkbook(fis)) {

        Sheet employeeSheet = workbook.getSheet(EMPLOYEE_SHEET_NAME);
        if (employeeSheet == null) {
            System.out.println("❌ Employee data sheet not found!");
            return;
        }
        System.out.println();
        System.out.println("==============================================");
        System.out.println("MotorPH Payroll Management System");
        System.out.println("==============================================");

        DateTimeFormatter birthdayFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (Row row : employeeSheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
                      
            // Retrieve cell values safely
            String employeeNumber = (String) getCellValue(row.getCell(0));
            String employeeName = (String) getCellValue(row.getCell(1));
            
            // Ensure null checks before calling isEmpty()
            if ((employeeNumber == null || employeeNumber.trim().isEmpty()) &&
                (employeeName == null || employeeName.trim().isEmpty())) {
                continue; // Skip blank row
            }

            // === Basic Salary and Hourly Rate Calculation ===
            Object cellValue = getCellValue(row.getCell(13));
            double basicSalary = 0.0;
            if (cellValue instanceof Double) {
                basicSalary = (Double) cellValue;  // Directly use it as a double
            } else if (cellValue instanceof String) {
                basicSalary = Double.parseDouble((String) cellValue);  // Parse string to double if needed
            } else {
                // Handle other types or errors here
                //System.out.println("Unexpected cell value type: " + cellValue.getClass());
            }
            double hourlyRate = (basicSalary / 21) / 8;

            double totalRegularHours = 0;
            double totalOvertimeHours = 0;
            double overtimeRatePercentage = 1.25;

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime otStart = LocalTime.of(8, 0);
            LocalTime otEnd = LocalTime.of(8, 10);

            // === Attendance Data Processing ===
            try (FileInputStream attendanceFis = new FileInputStream(EMPLOYEE_DATA_FILE);
                 Workbook attendanceWorkbook = new XSSFWorkbook(attendanceFis)) {

                Sheet attendanceSheet = attendanceWorkbook.getSheet(ATTENDANCE_SHEET_NAME);
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
                System.out.println("❌ Error reading attendance file: " + e.getMessage());
            }

            // === Salary Calculation ===
            double regularPay = totalRegularHours * hourlyRate;
            double overtimePay = totalOvertimeHours * hourlyRate * overtimeRatePercentage;
            double weeklySalary = regularPay + overtimePay;

            cellValue = getCellValue(row.getCell(14));  // Rice Subsidy
            double riceSubsidy = 0.0;
            if (cellValue instanceof String) {
                riceSubsidy = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                riceSubsidy = (Double) cellValue;
            }
            riceSubsidy /= 4.33;

            cellValue = getCellValue(row.getCell(15));  // Phone Allowance
            double phoneAllowance = 0.0;
            if (cellValue instanceof String) {
                phoneAllowance = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                phoneAllowance = (Double) cellValue;
            }
            phoneAllowance /= 4.33;

            cellValue = getCellValue(row.getCell(16));  // Clothing Allowance
            double clothingAllowance = 0.0;
            if (cellValue instanceof String) {
                clothingAllowance = Double.parseDouble((String) cellValue);
            } else if (cellValue instanceof Double) {
                clothingAllowance = (Double) cellValue;
            }
            clothingAllowance /= 4.33;

            double sss = calculateSSS(weeklySalary);
            double philHealth = calculatePhilHealth(weeklySalary);
            double pagIbig = calculatePagIbig(weeklySalary);
            double withholdingTax = calculateWithholdingTax(weeklySalary, sss, philHealth, pagIbig);

            double deductionSum = weeklySalary - sss - philHealth - pagIbig - withholdingTax;
            double netSalary = deductionSum + riceSubsidy + phoneAllowance + clothingAllowance;

            // === Display Results ===
            LocalDate birthday = parseDateFromCell(row.getCell(3));
            
            // Create a DecimalFormat instance
            DecimalFormat df = new DecimalFormat("#,###.00");

            System.out.println("Employee Number: " + getCellValue(row.getCell(0)));
            System.out.println("Employee Name: " + getCellValue(row.getCell(2)) + " " + getCellValue(row.getCell(1)));
            System.out.println("Debug: Birthday value from cell = " + birthday);
            if (birthday != null) {
                System.out.println("Birthday: " + birthdayFormat.format(birthday));
            } else {
                System.out.println("Birthday: N/A"); // Handle missing birthday
            }
            System.out.println("Week Number: " + weekNumber);
            System.out.println("Period: " + weekStartDate + " to " + weekEndDate);
            System.out.println("Total Regular Hours: " + df.format(totalRegularHours));
            System.out.println("Overtime Hours: " + df.format(totalOvertimeHours));
            System.out.println("----------------------------------------------");
            System.out.println("Basic Salary: ₱ " + df.format(basicSalary));
            System.out.println("Hourly Rate: ₱ " + df.format(hourlyRate));
            System.out.println("Gross Salary: ₱ " + df.format(weeklySalary));
            System.out.println("SSS Deduction: ₱ " + df.format(sss));
            System.out.println("PAG-IBIG: ₱ " + df.format(pagIbig));
            System.out.println("PhilHealth: ₱ " + df.format(philHealth));
            System.out.println("Withholding Tax: ₱ " + df.format(withholdingTax));
            System.out.println("Rice Subsidy: ₱ " + df.format(riceSubsidy));
            System.out.println("Phone Allowance: ₱ " + df.format(phoneAllowance));
            System.out.println("Clothing Allowance: ₱ " + df.format(clothingAllowance));
            System.out.println("----------------------------------------------");
            System.out.println("Net Salary: ₱ " + df.format(netSalary));
            System.out.println("==============================================");
            //System.out.println("(0) Home Page");
            //System.out.println("(1) Display Other Week");
            //System.out.println("(2) Edit Employee Details");
            //System.out.println("(3) Delete Employee");
            /*System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // Handling user's choice
            switch (choice) {
                case 0:
                    return;
                case 1:
                    displayAllEmployeesWithWeeklySalary(scanner);
                    break;
                /*case 2:
                    Employee.editEmployeeDetails(scanner);
                    break;
                case 3:
                    Employee.deleteEmployee(scanner);
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
                    break;
            }*/
        }

    } catch (IOException e) {
        System.out.println("❌ Error reading file: " + e.getMessage());
    }
}


        // Method for parsing date from cell
        private static LocalDate parseDateFromCell(Cell cell) {
            if (cell == null || cell.getCellType() != CellType.NUMERIC) {
                return null;
            }
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
         // ===================== CALCULATION METHODS =====================
        private static LocalTime readTimeCell(Cell cell, DateTimeFormatter formatter) {
        if (cell == null) return LocalTime.MIDNIGHT; // Or some other default time value
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalTime();
        } else {
        try {
            return LocalTime.parse((CharSequence) getCellValue(cell), formatter);
        } catch (Exception e) {
            System.out.println("❌ Invalid time format");
            return LocalTime.MIDNIGHT; // Or another default value
        }
    }
}
    
    public static double calculateSSS(double weeklySalary) {
        //SssClass test = new SssClass();
        
        //test.run();
        
        //double monthlySalary = weeklySalary * 4.33;
        //return (monthlySalary <= 3250) ? 135.00 : 1125.00;
        double calculateSSS = 0;
        if (weeklySalary < 3250){
            calculateSSS = 135;
        }
        else if (weeklySalary <= 3750){
            calculateSSS = 157.50;
        }
        else if (weeklySalary <= 4250){
            calculateSSS = 180;
        }
        else if (weeklySalary <= 4750){
            calculateSSS = 202.50;
        }
        else if (weeklySalary <= 5250){
            calculateSSS = 225;
        }
        else if (weeklySalary <= 5750){
            calculateSSS = 247.50;
        }
        else if (weeklySalary <= 6250){
            calculateSSS = 270;
        }
        else if (weeklySalary <= 6750){
            calculateSSS = 292.50;
        }
        else if (weeklySalary <= 7250){
            calculateSSS = 315;
        }
        else if (weeklySalary <= 7750){
            calculateSSS = 337.50;
        }
        else if (weeklySalary <= 8250){
            calculateSSS = 360;
        }
        else if (weeklySalary <= 8750){
            calculateSSS = 382.50;
        }
        else if (weeklySalary <= 9250){
            calculateSSS = 405;
        }
        else if (weeklySalary <= 9750){
            calculateSSS = 427.50;
        }
        else if (weeklySalary <= 10250){
            calculateSSS = 450;
        }
        else if (weeklySalary <= 10750){
            calculateSSS = 472.50;
        }
        else if (weeklySalary <= 11250){
            calculateSSS = 495;
        }
        else if (weeklySalary <= 11750){
            calculateSSS = 517.50;
        }
        else if (weeklySalary <= 12250){
            calculateSSS = 540;
        }
        else if (weeklySalary <= 12750){
            calculateSSS = 562.50;
        }
        else if (weeklySalary <= 13250){
            calculateSSS = 585;
        }
        else if (weeklySalary <= 13750){
            calculateSSS = 607.50;
        }
        else if (weeklySalary <= 14250){
            calculateSSS = 630;
        }
        else if (weeklySalary <= 14750){
            calculateSSS = 652.50;
        }
        else if (weeklySalary <= 15250){
            calculateSSS = 675;
        }
        else if (weeklySalary <= 15750){
            calculateSSS = 697.50;
        }
        else if (weeklySalary <= 16250){
            calculateSSS = 720;
        }
        else if (weeklySalary <= 16750){
            calculateSSS = 742.50;
        }
        else if (weeklySalary <= 17250){
            calculateSSS = 765;
        }
        else if (weeklySalary <= 17750){
            calculateSSS = 787.50;
        }
        else if (weeklySalary <= 18250){
            calculateSSS = 810;
        }
        else if (weeklySalary <= 18750){
            calculateSSS = 832.50;
        }
        else if (weeklySalary <= 19250){
            calculateSSS = 855;
        }
        else if (weeklySalary <= 19750){
            calculateSSS = 877.50;
        }
        else if (weeklySalary <= 20250){
            calculateSSS = 900;
        }
        else if (weeklySalary <= 20750){
            calculateSSS = 922.50;
        }
        else if (weeklySalary <= 21250){
            calculateSSS = 945;
        }
        else if (weeklySalary <= 21750){
            calculateSSS = 967.50;
        }
        else if (weeklySalary <= 22250){
            calculateSSS = 990;
        }
        else if (weeklySalary <= 22750){
            calculateSSS = 1012.50;
        }
        else if (weeklySalary <= 23250){
            calculateSSS = 1035;
        }
        else if (weeklySalary <= 23750){
            calculateSSS = 1057.50;
        }
        else if (weeklySalary <= 24250){
            calculateSSS = 1080;
        }
        else if (weeklySalary <= 24750){
            calculateSSS = 1102.50;
        }
        else {
            calculateSSS = 1125;
        }
        return calculateSSS;
        
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

    private static double calculateWithholdingTax(double basicSalary, double weeklySalary, double sss, double philHealth) {
        double taxableIncome = basicSalary / 4.33; // Convert monthly salary to weekly

        double calculatedTax = 0;

        if (taxableIncome <= 4808.56) {
            calculatedTax = 0;
        } else if (taxableIncome <= 7692.31) {
            calculatedTax = (taxableIncome - 4808.56) * 0.20;
        } else if (taxableIncome <= 15384.62) {
            calculatedTax = 2500 + (taxableIncome - 7692.31) * 0.25;
        } else if (taxableIncome <= 38461.54) {
            calculatedTax = 10833 + (taxableIncome - 15384.62) * 0.30;
        } else if (taxableIncome <= 153846.15) {
            calculatedTax = 40833.33 + (taxableIncome - 38461.54) * 0.32;
        } else {
            calculatedTax = 200833.33 + (taxableIncome - 153846.15) * 0.35;
        }

        return calculatedTax;
    }
  }
