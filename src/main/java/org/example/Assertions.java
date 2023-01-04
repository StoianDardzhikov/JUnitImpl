package org.example;

import java.util.function.Supplier;

public class Assertions {
    static void assertEquals(Object a, Object b) throws AssertionFailedError {
        assertEquals(a, b, () -> "expected: <" + a + ">, but was: <" + b + ">");
    }

    static void assertEquals(Object a, Object b, Supplier<String> messageSupplier) {
        boolean passed = a.equals(b);
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }

    static void assertTrue(boolean bool) throws AssertionFailedError {
        assertTrue(bool, () -> "expected: <" + true + ">, but was: <" + false + ">");
    }

    static void assertTrue(boolean bool, Supplier<String> messageSupplier) throws AssertionFailedError {
        if (!bool) throw new AssertionFailedError(messageSupplier.get());
    }

    static void assertFalse(boolean bool) throws AssertionFailedError {
        assertFalse(bool, () -> "expected: <" + false + ">, but was: <" + true + ">");
    }

    static void assertFalse(boolean bool, Supplier<String> messageSupplier) throws AssertionFailedError {
        if (bool) throw new AssertionFailedError(messageSupplier.get());
    }

    static void assertNotNull(Object object) throws AssertionFailedError {
        assertNotNull(object, () -> "expected: <" + object + ">, but was: <" + null + ">");
    }

    static void assertNotNull(Object object, Supplier<String> messageSupplier) throws AssertionFailedError {
        boolean passed = object != null;
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }

    static void assertNull(Object object) throws AssertionFailedError {
        assertNull(object, () -> "expected: <" + null + ">, but was: <" + object + ">");
    }

    static void assertNull(Object object, Supplier<String> messageSupplier) throws AssertionFailedError {
        boolean passed = object == null;
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }
}