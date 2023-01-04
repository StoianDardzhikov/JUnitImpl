package org.example;

public class TestResult {
    boolean passed;
    String displayName;
    Failure failure;
    TestResult(String displayName) {
        passed = true;
        this.displayName = displayName;
    }

    void fail(Failure failure) {
        passed = false;
        this.failure = failure;
    }

    public String toString() {
        return displayName + " " + (passed ? "[OK]" : "[X] " + failure.toString());
    }
}