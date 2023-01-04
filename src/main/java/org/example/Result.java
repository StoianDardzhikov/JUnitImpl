package org.example;

import java.util.ArrayList;
import java.util.List;

public class Result {
    List<TestResult> testResults;
    List<Failure> failures;
    String className;

    Result(String className) {
        testResults = new ArrayList<>();
        failures = new ArrayList<>();
        this.className = className;
    }

    void addTestResult(TestResult testResult) {
        testResults.add(testResult);
    }

    void addFailure(Failure failure) {
        failures.add(failure);
    }

    void printResult() {
        System.out.println("Running tests for " + className + "...");
        System.out.println();
        for (TestResult testResult : testResults) {
            System.out.println(testResult.toString());
        }
        System.out.println();
        if (failures.size() == 0)
            return;

        System.out.println("Failures:");
        for (Failure failure: failures) {
            System.out.println(failure.causeToString());
        }
    }
}
