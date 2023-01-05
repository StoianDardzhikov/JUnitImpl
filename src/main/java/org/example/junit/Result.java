package org.example.junit;

import java.util.ArrayList;
import java.util.List;

public class Result {
    List<TestResult> testResults;
    List<Failure> failures;
    String className;
    int passed = 0;
    int failed = 0;

    public Result(String className) {
        testResults = new ArrayList<>();
        failures = new ArrayList<>();
        this.className = className;
    }

    public void addTestResults(List<TestResult> testResults) {
        for (TestResult testResult : testResults) {
            this.testResults.add(testResult);
            if (testResult.isPassed())
                passed++;
            else
                failed++;

            Failure failure = testResult.getFailure();
            if (failure != null) {
                failures.add(failure);
            }
        }
    }

    public void addFailure(Failure failure) {
        failures.add(failure);
    }

    public void printResult() {
        if (testResults.size() == 0)
            return;

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
        System.out.println();
        System.out.println("Tests ran " + (passed + failed)
                + " passed: \u001B[32m" + passed + "\u001B[0m" +
                " failed: \u001B[31m" + failed + "\u001B[0m" +
                String.format(" success rate (%.2f%%)", ((passed + 0d) / (passed + failed)) * 100d));
        System.out.println();
    }
}
