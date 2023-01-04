package org.example.junit;

import java.util.ArrayList;
import java.util.List;

public class Result {
    List<TestResult> testResults;
    List<Failure> failures;
    String className;

    public Result(String className) {
        testResults = new ArrayList<>();
        failures = new ArrayList<>();
        this.className = className;
    }

    public void addTestResult(TestResult testResult) {
        testResults.add(testResult);
    }

    public void addFailure(Failure failure) {
        failures.add(failure);
    }

    public void printResult() {
        System.out.println("\u001B[36m| +-- " + className + "");
        for (TestResult testResult : testResults) {
            System.out.println("\u001B[36m|   +-- " + testResult.toString());
        }
        System.out.println("\u001B[36m| --+");
        System.out.println();
        if (failures.size() == 0)
            return;

        System.out.println("\u001B[0m" + "Failures(" + failures.size() + "):");
        for (Failure failure: failures) {
            System.out.println(failure.causeToString());
        }
    }
}
