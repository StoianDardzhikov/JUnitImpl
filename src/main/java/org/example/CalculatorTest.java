package org.example;

import org.example.Annotations.DisplayName;
import org.example.Annotations.Test;
import org.example.junit.Assertions;

import java.time.Duration;

import static org.example.junit.Assertions.*;

public class CalculatorTest {

    @Test
    public void throwsException() {
        Object x = null;
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            x.toString();
        });
    }

    @Test
    public void timeoutPreemptively() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            Thread.sleep(1500);
        });
    }

    @Test
    public void timeout() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            Thread.sleep(1500);
        });
    }

    @Test
    public void multiply1() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(5, 4);
        assertEquals(result, 19);
    }

    @Test
    @DisplayName("Example for a test that throws exception")
    public void testForException() {
        Object f = null;
        f.toString();
        assertEquals(1, 1);
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