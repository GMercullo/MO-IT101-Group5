/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.mycompany.mtrph;

import static org.testng.Assert.*;

/**
 *
 * @author gmmercullo
 */
public class PayrollCalculatorNGTest {
    
    public PayrollCalculatorNGTest() {
    }

    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of calculateSSS method, of class PayrollCalculator.
     */
    @org.testng.annotations.Test
    public void testCalculateSSS() {
        System.out.println("calculateSSS");
        double weeklySalary = 0.0;
        double expResult = 0.0;
        double result = PayrollCalculator.calculateSSS(weeklySalary);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
