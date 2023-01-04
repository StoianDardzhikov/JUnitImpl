package org.example.junit;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Assertions {
    public static void assertEquals(Object a, Object b) throws AssertionFailedError {
        assertEquals(a, b, () -> "expected: <" + a + ">, but was: <" + b + ">");
    }

    public static void assertEquals(Object a, Object b, Supplier<String> messageSupplier) {
        boolean passed = a.equals(b);
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }

    public static void assertTrue(boolean bool) throws AssertionFailedError {
        assertTrue(bool, () -> "expected: <" + true + ">, but was: <" + false + ">");
    }

    public static void assertTrue(boolean bool, Supplier<String> messageSupplier) throws AssertionFailedError {
        if (!bool) throw new AssertionFailedError(messageSupplier.get());
    }

    public static void assertFalse(boolean bool) throws AssertionFailedError {
        assertFalse(bool, () -> "expected: <" + false + ">, but was: <" + true + ">");
    }

    public static void assertFalse(boolean bool, Supplier<String> messageSupplier) throws AssertionFailedError {
        if (bool) throw new AssertionFailedError(messageSupplier.get());
    }

    public static void assertNotNull(Object object) throws AssertionFailedError {
        assertNotNull(object, () -> "expected: <" + object + ">, but was: <" + null + ">");
    }

    public static void assertNotNull(Object object, Supplier<String> messageSupplier) throws AssertionFailedError {
        boolean passed = object != null;
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }

    public static void assertNull(Object object) throws AssertionFailedError {
        assertNull(object, () -> "expected: <" + null + ">, but was: <" + object + ">");
    }

    public static void assertNull(Object object, Supplier<String> messageSupplier) throws AssertionFailedError {
        boolean passed = object == null;
        if (!passed) throw new AssertionFailedError(messageSupplier.get());
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) throws AssertionFailedError {
        return assertThrows(expectedType, executable, (String) null);
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, Supplier<String> messageSupplier) throws AssertionFailedError {
        return assertThrows(expectedType, executable, messageSupplier.get());
    }

    private static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, String message) throws AssertionFailedError {
        try {
            executable.execute();
        }
        catch (Throwable e) {
            if (!e.getClass().equals(expectedType)) {
                String errorMessage = message == null ? "Unexpected exception type, expected: <" + expectedType.getName() + ">, but was: <" + e.getClass().getName() + ">"
                                                        : message;
                throw new AssertionFailedError(errorMessage);
            }
            else {
                return (T) e;
            }
        }
        throw new AssertionFailedError("No exception was thrown");
    }

    public static void assertTimeout(Duration duration, Executable executable) {
        assertTimeout(duration, executable, (String) null);
    }
    public static void assertTimeout(Duration duration, Executable executable, Supplier<String> messageSupplier) {
        assertTimeout(duration, executable, messageSupplier.get());
    }

    private static void assertTimeout(Duration duration, Executable executable, String message) {
        long start = System.currentTimeMillis();
        try {
            executable.execute();
        } catch (Throwable ignored) {}
        long end = System.currentTimeMillis();
        long actualDuration = end - start;
        long expectedDuration = duration.toMillis();
        if (actualDuration > expectedDuration) {
            String errorMessage = message == null ? "execution exceeded timeout of " + expectedDuration + " ms by " + (actualDuration - expectedDuration) + " ms"
                                                    : message;
            throw new AssertionFailedError(errorMessage);
        }
    }

    public static void assertTimeoutPreemptively(Duration duration, Executable executable) {
        assertTimeoutPreemptively(duration, executable, () -> "execution exceeded timeout of " + duration.toMillis() + " ms");
    }

    public static void assertTimeoutPreemptively(Duration duration, Executable executable, Supplier<String> messageSupplier) {
        AtomicBoolean done = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            try {
                executable.execute();
                done.set(true);
            } catch (Throwable ignored) {}
        });
        thread.start();
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ignored) {}
        if (!done.get())
            throw new AssertionFailedError(messageSupplier.get());
        thread.interrupt();
    }
}