package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Failure {
    TestResult testResult;
    Throwable cause;

    Failure(TestResult testResult, Throwable cause) {
        this.testResult = testResult;
        this.cause = cause;
    }

    public String toString() {
        return cause.getMessage();
    }

    public String causeToString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        cause.printStackTrace(printWriter);
        return testResult.displayName + " failed, cause: " + stringWriter;
    }
}