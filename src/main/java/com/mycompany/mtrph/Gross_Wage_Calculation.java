package com.project;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// Abstract Employee class
abstract class Employee {
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
    
    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Hourly Rate: " + hourlyRate + ", Hours Worked: " + hoursWorked + ", Gross Salary: " + calcGrossSalary();
    }
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
}

// Utility class for payroll calculations
class PayrollCalculator {
    public static double calcGrossSalary(Employee employee) {
        return employee.calcGrossSalary();
    }
}

// Payroll system to manage employee details
public class PayrollApp {
    private static final Logger logger = Logger.getLogger(PayrollApp.class.getName());
    private List<Employee> employees;
    
    public PayrollApp() {
        this.employees = new ArrayList<>();
    }
    
    public void addFullTimeEmployee(int id, String name, double hourlyRate) {
        employees.add(new FullTimeEmployee(id, name, hourlyRate));
    }
    
    public void addPartTimeEmployee(int id, String name, double hourlyRate) {
        employees.add(new PartTimeEmployee(id, name, hourlyRate));
    }
    
    public void addHoursWorked(int id, double hours) {
        for (Employee emp : employees) {
            if (emp.getId() == id) {
                emp.addHoursWorked(hours);
                logger.info("Added " + hours + " hours to employee ID " + id);
                return;
            }
        }
        logger.warning("Employee ID " + id + " not found.");
    }
    
    public void listEmployees() {
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
    
    public static void main(String[] args) {
        logger.info("Initializing Payroll Application...");
        PayrollApp app = new PayrollApp();
        
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
