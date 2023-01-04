package org.example.junit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    int repetitions;
    Method method;
    String displayName;

    public Test(Method method, String displayName, int repetitions) {
        this.method = method;
        this.displayName = displayName;
        this.repetitions = repetitions;
    }

    public String getDisplayName() { return  displayName; }
    public Method getMethod() { return method; }
    public int getRepetitions() { return repetitions; }
}