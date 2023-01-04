package org.example.junit;

public class AssertionFailedError extends Error {
    String message;

    public String getMessage() {
        return message;
    }

    AssertionFailedError(String message) {
        this.message = message;
    }
}