package org.example.junit;

public class TestResult {
    boolean passed;
    String displayName;
    Failure failure;
    public TestResult(String displayName) {
        passed = true;
        this.displayName = displayName;
    }

    public void fail(Failure failure) {
        passed = false;
        this.failure = failure;
    }

    public String toString() {
        return "\u001B[34m" + displayName + " " + (passed ? "\u001B[32m[OK]" : "\u001B[31m[X] " + failure.toString());
    }
}