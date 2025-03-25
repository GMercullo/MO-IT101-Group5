/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.mtrph;

import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author gmmercullo
 */
public class PayrollCalculatorTest {
    
    public PayrollCalculatorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of calculateSSS method, of class PayrollCalculator.
     */
    @Test
    public void testCalculateSSS() {
        System.out.println("calculateSSS");
        double weeklySalary = 0.0;
        double expResult = 0.0;
        double result = PayrollCalculator.calculateSSS(weeklySalary);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readEmployeeData method, of class PayrollCalculator.
     */
    @Test
    public void testReadEmployeeData() {
        System.out.println("readEmployeeData");
        String fileName = "";
        String sheetName = "";
        List<Row> expResult = null;
        List<Row> result = PayrollCalculator.readEmployeeData(fileName, sheetName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readAttendanceData method, of class PayrollCalculator.
     */
    @Test
    public void testReadAttendanceData() {
        System.out.println("readAttendanceData");
        String fileName = "";
        String sheetName = "";
        List<Row> expResult = null;
        List<Row> result = PayrollCalculator.readAttendanceData(fileName, sheetName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
