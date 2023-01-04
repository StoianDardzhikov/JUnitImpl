package org.example;

import org.example.Annotations.RepeatedTest;
import org.example.Annotations.DisplayName;
import org.example.Annotations.AfterAll;
import org.example.Annotations.AfterEach;
import org.example.Annotations.BeforeAll;
import org.example.Annotations.BeforeEach;
import org.example.junit.Test;
import org.example.junit.Failure;
import org.example.junit.Result;
import org.example.junit.TestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JUnitRunner {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if (args.length == 0) {
            System.out.println("Please provide test class name");
            return;
        }
        String testClassName = args[0];

        runTestsForClass(testClassName);
    }

    static void runTestsForClass(String className) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        int passedTests = 0;
        int failedTests = 0;
        Class<?> clazz = Class.forName(className);
        Result result = new Result(clazz.getSimpleName());
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> beforeEachMethods = new ArrayList<>();
        List<Method> afterEachMethods = new ArrayList<>();
        List<Method> afterAllMethods = new ArrayList<>();
        List<Test> tests = new ArrayList<>();
        parseMethods(instance, methods, beforeEachMethods, afterEachMethods, afterAllMethods, tests);
        for (Test test : tests) {
            String displayName = test.getDisplayName();
            TestResult testResult = new TestResult(displayName);
            result.addTestResult(testResult);
            Method method = test.getMethod();
            for (int i = 0; i < test.getRepetitions(); i++) {
                runMethods(beforeEachMethods, instance);
                try {
                    passedTests++;
                    method.invoke(instance);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    Failure failure = new Failure(testResult, cause);
                    testResult.fail(failure);
                    result.addFailure(failure);
                    passedTests--;
                    failedTests++;
                }
                runMethods(afterEachMethods, instance);
            }
        }
        runMethods(afterAllMethods, instance);
        result.printResult();
        System.out.println();
        System.out.println("Tests ran " + (passedTests + failedTests)
                + " passed: \u001B[32m" + passedTests + "\u001B[0m" +
                " failed: \u001B[31m" + failedTests + "\u001B[0m" +
                String.format(" success rate (%.2f%%)", ((passedTests + 0d) / (passedTests + failedTests)) * 100d));
    }

    private static void parseMethods(Object instance, Method[] methods, List<Method> beforeEachMethods, List<Method> afterEachMethods, List<Method> afterAllMethods, List<Test> tests) throws IllegalAccessException, InvocationTargetException {
        for (Method method : methods) {
            String displayName = method.getName() + "()";
            boolean isTest = false;
            int repetitions = 1;
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(BeforeEach.class)) {
                    beforeEachMethods.add(method);
                    break;
                }
                if (annotation.annotationType().equals(AfterEach.class)) {
                    afterEachMethods.add(method);
                    break;
                }
                if (annotation.annotationType().equals(BeforeAll.class)) {
                    method.invoke(instance);
                    break;
                }
                if (annotation.annotationType().equals(AfterAll.class)) {
                    afterAllMethods.add(method);
                    break;
                }
                if (annotation.annotationType().equals(org.example.Annotations.Test.class)) {
                    isTest = true;
                }
                if (annotation.annotationType().equals(DisplayName.class)) {
                    DisplayName displayNameAnn = (DisplayName) annotation;
                    displayName = displayNameAnn.value();
                }
                if (annotation.annotationType().equals(RepeatedTest.class)) {
                    RepeatedTest repeatedTestAnn = (RepeatedTest) annotation;
                    repetitions = repeatedTestAnn.value();
                 }
            }
            if (isTest) {
                tests.add(new Test(method, displayName, repetitions));
            }
        }
    }

    static void runMethods(List<Method> methodList, Object o) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methodList) {
            method.invoke(o);
        }
    }
}