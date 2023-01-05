package org.example.junit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    int repetitions;
    Method method;
    String displayName;
    long timeout;
    Class<? extends Throwable> throwableClass;
    String[] dependsOnMethods;

    public Test(Method method, String displayName, int repetitions, long timeout, Class<? extends Throwable> throwableClass, String[] dependsOnMethods) {
        this.method = method;
        this.displayName = displayName;
        this.repetitions = repetitions;
        this.timeout = timeout;
        this.throwableClass = throwableClass;
        this.dependsOnMethods = dependsOnMethods;
    }

    public String getDisplayName() { return  displayName; }
    public Method getMethod() { return method; }
    public boolean repetitionsArePositive() { return repetitions > 0; }
    public int getRepetitions() { return repetitions <= 0 ? 1 : repetitions; }
    public Class<? extends Throwable> getThrowableClass() { return throwableClass; }
    public long getTimeout() { return timeout; }
    public String[] getDependsOnMethods() { return dependsOnMethods; }
}