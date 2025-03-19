package com.project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// Abstract Employee class
abstract class Employee implements Serializable {
    protected String name;
    protected int id;
    protected double hourlyRate;
    protected double hoursWorked;

    public Employee(int id, String name, double hourlyRate) {
        this.id = id;
        this.name = name;
        this.hourlyRate = hourlyRate;
        this.hoursWorked = 0;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public double getHourlyRate() { return hourlyRate; }
    public double getHoursWorked() { return hoursWorked; }
    
    public void addHoursWorked(double hours) { 
        if (hours < 0) {
            throw new IllegalArgumentException("Hours worked cannot be negative.");
        }
        this.hoursWorked += hours; 
    }
    
    public abstract double calcGrossSalary();
    public abstract double calcNetSalary();
}

// Full-time Employee with 1.5x overtime pay after 40 hours
class FullTimeEmployee extends Employee {
    public FullTimeEmployee(int id, String name, double hourlyRate) {
        super(id, name, hourlyRate);
    }

    @Override
    public double calcGrossSalary() {
        double overtimeHours = Math.max(0, hoursWorked - 40);
        double regularHours = hoursWorked - overtimeHours;
        return (regularHours * hourlyRate) + (overtimeHours * hourlyRate * 1.5);
    }
    
    @Override
    public double calcNetSalary() {
        double grossSalary = calcGrossSalary();
        double tax = grossSalary * 0.2; // 20% tax
        double deductions = grossSalary * 0.05; // 5% for benefits
        return grossSalary - tax - deductions;
    }
}

// Part-time Employee with no overtime pay
class PartTimeEmployee extends Employee {
    public PartTimeEmployee(int id, String name, double hourlyRate) {
        super(id, name, hourlyRate);
    }

    @Override
    public double calcGrossSalary() {
        return hoursWorked * hourlyRate; // No overtime pay
    }
    
    @Override
    public double calcNetSalary() {
        double grossSalary = calcGrossSalary();
        double tax = grossSalary * 0.15; // 15% tax for part-time
        return grossSalary - tax;
    }
}

// Payroll system to manage employee details
public class PayrollApp {
    private static final Logger logger = Logger.getLogger(PayrollApp.class.getName());
    private List<Employee> employees;
    private static final String FILE_PATH = "employees.dat";
    
    public PayrollApp() {
        this.employees = loadEmployeesFromFile();
    }
    
    public void addFullTimeEmployee(int id, String name, double hourlyRate) {
        employees.add(new FullTimeEmployee(id, name, hourlyRate));
        saveEmployeesToFile();
    }
    
    public void addPartTimeEmployee(int id, String name, double hourlyRate) {
        employees.add(new PartTimeEmployee(id, name, hourlyRate));
        saveEmployeesToFile();
    }
    
    public void addHoursWorked(int id, double hours) {
        for (Employee emp : employees) {
            if (emp.getId() == id) {
                emp.addHoursWorked(hours);
                saveEmployeesToFile();
                logger.info("Added " + hours + " hours to employee ID " + id);
                return;
            }
        }
        logger.warning("Employee ID " + id + " not found.");
    }
    
    public void listEmployees() {
        for (Employee emp : employees) {
            System.out.println("ID: " + emp.getId() + ", Name: " + emp.getName() + 
                ", Hours Worked: " + emp.getHoursWorked() + ", Gross Salary: " + emp.calcGrossSalary() +
                ", Net Salary: " + emp.calcNetSalary());
        }
    }
    
    private void saveEmployeesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            logger.severe("Error saving employees to file: " + e.getMessage());
        }
    }
    
    private List<Employee> loadEmployeesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Employee>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warning("No existing employee file found. Starting fresh.");
            return new ArrayList<>();
        }
    }
    
    public static void main(String[] args) {
        logger.info("Initializing Payroll Application...");
        PayrollApp app = new PayrollApp();
        
        // Unit tests
        assert new FullTimeEmployee(1, "Alice", 20.0).calcGrossSalary() == 800.0 : "Full-time salary calculation failed";
        assert new PartTimeEmployee(2, "Bob", 18.0).calcGrossSalary() == 720.0 : "Part-time salary calculation failed";
        
        logger.info("All tests passed successfully!");
        
        // Adding sample employees
        app.addFullTimeEmployee(1, "Alice", 20.5);
        app.addPartTimeEmployee(2, "Bob", 18.0);
        
        // Adding hours worked
        app.addHoursWorked(1, 45); // Full-time employee with overtime
        app.addHoursWorked(2, 38); // Part-time employee without overtime
        
        // Displaying employee details
        logger.info("Displaying Employee Details...");
        app.listEmployees();
    }
}
