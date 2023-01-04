package org.example;

import static org.example.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    public void multiply1() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(5, 4);
        assertEquals(result, 19);
    }

    @Test
    @DisplayName("Test for multiplying 1 positive and 1 negative number")
    public  void multiply2() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(5, -4);
        assertEquals(result, -20);
    }

    @DisplayName("Test for multiplying 2 negative numbers")
    @Test
    public  void multiply3() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(-5, -4);
        assertEquals(result, 20);
    }

    @DisplayName("Test for multiplying 0 and non-zero number")
    @Test
    public  void multiply4() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(0, 4);
        assertEquals(result, 0);
    }

    @DisplayName("Test for multiplying 2 zero numbers")
    @Test
    public  void multiply5() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(0, 0);
        assertEquals(result, 0);
    }

    @DisplayName("Test for adding 2 positive numbers")
    @Test
    public void add1() {
        Object f = null;
        f.toString();
        Calculator calculator = new Calculator();
        int result = calculator.add(5, 4);
        assertEquals(result, 9);
    }

    @DisplayName("Test for adding 1 positive and 1 negative numbers")
    @Test
    public void add2() {
        Calculator calculator = new Calculator();
        int result = calculator.add(5, -4);
        assertEquals(result, 1);
    }

    @DisplayName("Test for adding 2 negative numbers")
    @Test
    public void add3() {
        Calculator calculator = new Calculator();
        int result = calculator.add(-5, -4);
        assertEquals(result, -9);
    }

    @DisplayName("Test for adding 0 and non-zero number")
    @Test
    public void add4() {
        Calculator calculator = new Calculator();
        int result = calculator.add(0, 4);
        assertEquals(result, 4);
    }

    @DisplayName("Test for adding 2 zero numbers")
    @Test
    public void add5() {
        Calculator calculator = new Calculator();
        int result = calculator.add(0, 0);
        assertEquals(result, 0);
    }
}