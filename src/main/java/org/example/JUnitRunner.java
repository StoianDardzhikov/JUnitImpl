package org.example;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Result result = new Result(clazz.getName());
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> beforeEachMethods = new ArrayList<>();
        List<Method> afterEachMethods = new ArrayList<>();
        List<Method> afterAllMethods = new ArrayList<>();
        Map<String, Method> tests = new HashMap<>();
        parseMethods(instance, methods, beforeEachMethods, afterEachMethods, afterAllMethods, tests);
        for (Map.Entry<String, Method> entry : tests.entrySet()) {
            String displayName = entry.getKey();
            TestResult testResult = new TestResult(displayName);
            result.addTestResult(testResult);
            Method test = entry.getValue();
            runMethods(beforeEachMethods, instance);
            try {
                passedTests++;
                test.invoke(instance);
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
        runMethods(afterAllMethods, instance);
        result.printResult();
        System.out.println();
        System.out.println("Tests ran " + (passedTests + failedTests) + " passed: " + passedTests + " failed: " + failedTests);
    }

    private static void parseMethods(Object instance, Method[] methods, List<Method> beforeEachMethods, List<Method> afterEachMethods, List<Method> afterAllMethods, Map<String, Method> tests) throws IllegalAccessException, InvocationTargetException {
        for (Method method : methods) {
            String displayName = method.getName();
            boolean isTest = false;
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
                if (annotation.annotationType().equals(Test.class)) {
                    isTest = true;
                }
                if (annotation.annotationType().equals(DisplayName.class)) {
                    DisplayName displayNameAnn = (DisplayName) annotation;
                    displayName = displayNameAnn.value();
                }
            }
            if (isTest) {
                tests.put(displayName, method);
            }
        }
    }

    static void runMethods(List<Method> methodList, Object o) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methodList) {
            method.invoke(o);
        }
    }
}