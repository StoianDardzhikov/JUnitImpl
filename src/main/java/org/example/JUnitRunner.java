package org.example;

import org.apache.commons.cli.*;
import org.example.Annotations.RepeatedTest;
import org.example.Annotations.DisplayName;
import org.example.Annotations.AfterAll;
import org.example.Annotations.AfterEach;
import org.example.Annotations.BeforeAll;
import org.example.Annotations.BeforeEach;
import org.example.junit.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JUnitRunner {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, ParseException, IOException {
        Options options = new Options();
        Option sp = new Option("sp",true, "A package containing the test files");
        options.addOption(sp);
        Option cp = new Option("cp",true, "Path to classes");
        options.addOption(cp);
        List<String> fileNames = new ArrayList<>();
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);
        if (commandLine.hasOption(sp)) {
            String testPackage = commandLine.getOptionValue(sp);
            String testPackagePath = testPackage.replace('.', '/');
            Path classesPath = Path.of(commandLine.getOptionValue(cp) ,"/" , testPackagePath);
            Files.walkFileTree(classesPath, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".class")) {
                        fileNames.add(String.valueOf(file.getFileName()));
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(classesPath))
                        return FileVisitResult.CONTINUE;
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });

            for (String fileName : fileNames) {
                fileName = fileName.replace(".class", "");
                runTestsForClass(testPackage + "." + fileName);
            }
        } else {
            if (args.length == 0) {
                System.out.println("Please provide test class name");
                return;
            }
            String testClassName = args[0];

            runTestsForClass(testClassName);
        }
    }

    static void runTestsForClass(String className) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException{
        Class<?> clazz = Class.forName(className);
        Result result = new Result(clazz.getSimpleName());
        Object instance;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return;
        }
        Methods methods = parseMethods(instance, declaredMethods);
        for (Test test : methods.getTests()) {
            for (int i = 0; i < test.getRepetitions(); i++) {
                runMethods(methods.beforeEachMethods, instance);
                List<TestResult> testResults = invokeTest(test, instance);
                result.addTestResults(testResults);
                runMethods(methods.afterEachMethods, instance);
            }
        }
        runMethods(methods.afterClassMethods, instance);
        result.printResult();
    }

    private static Methods parseMethods(Object instance, Method[] declaredMethods) throws IllegalAccessException, InvocationTargetException {
        Methods methods = new Methods();
        for (Method method : declaredMethods) {
            String displayName = method.getName() + "()";
            boolean isTest = false;
            int repetitions = 1;
            Class<? extends Throwable> throwableClass = null;
            long timeout = 0;
            String[] dependsOnMethods = {};
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(BeforeEach.class)) {
                    methods.addBeforeEachMethod(method);
                    break;
                }
                if (annotation.annotationType().equals(AfterEach.class)) {
                    methods.addAfterEachMethods(method);
                    break;
                }
                if (annotation.annotationType().equals(BeforeAll.class)) {
                    method.invoke(instance);
                    break;
                }
                if (annotation.annotationType().equals(AfterAll.class)) {
                    methods.addAfterClassMethod(method);
                    break;
                }
                if (annotation.annotationType().equals(org.example.Annotations.Test.class)) {
                    isTest = true;
                    org.example.Annotations.Test testAnn = (org.example.Annotations.Test) annotation;
                    throwableClass = testAnn.expected();
                    timeout = testAnn.timeout();
                    dependsOnMethods = testAnn.dependsOnMethods();
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
                methods.addTest(new Test(method, displayName, repetitions, timeout, throwableClass, dependsOnMethods));
            }
        }

        return methods;
    }

    static void runMethods(List<Method> methodList, Object o) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methodList) {
            method.invoke(o);
        }
    }

    static List<TestResult> invokeTest(Test test, Object instance) {
        List<TestResult> testResults = new ArrayList<>();

        TestResult negativeRepetitionsResult = checkForNegativeRepetitions(test);
        if (negativeRepetitionsResult != null) testResults.add(negativeRepetitionsResult);

        TestResult testResult = new TestResult(test.getDisplayName());
        AtomicBoolean done = new AtomicBoolean(false);
        Thread thread = startTestThread(test, instance, testResult, done);
        long timeout = test.getTimeout();
        try {
            if (timeout > 0)
                thread.join(timeout);
            else
                thread.join();
        } catch (InterruptedException ignored) {}
        thread.stop();

        if (!done.get() && timeout > 0) {
            AssertionFailedError assertionFailedError = new AssertionFailedError("exceeded test timeout ("+ timeout + " ms)");
            Failure failure = new Failure(testResult, assertionFailedError);
            testResult.fail(failure);
        }
        testResults.add(testResult);
        return testResults;
    }

    private static TestResult checkForNegativeRepetitions(Test test) {
        if (test.repetitionsArePositive())
            return null;

        String displayName = test.getDisplayName();
        Method method = test.getMethod();
        TestResult testResult = new TestResult(displayName);
        IllegalArgumentException exception = new IllegalArgumentException("Configuration error: @RepeatedTest on method [public void " + method.getName() + "()] must be declared with a positive 'value'.");
        Failure failure = new Failure(testResult, exception);
        testResult.fail(failure);
        return testResult;
    }

    private static Thread startTestThread(Test test, Object instance, TestResult testResult, AtomicBoolean done) {
        Method method = test.getMethod();
        Thread thread = new Thread(() -> {
            try {
                method.invoke(instance);
                done.set(true);
            } catch (InvocationTargetException | IllegalAccessException e) {
                if (e.getClass().equals(test.getThrowableClass())) {
                    return;
                }
                Throwable cause = e.getCause();
                Failure failure = new Failure(testResult, cause);
                testResult.fail(failure);
            }
        });
        thread.start();
        return thread;
    }
}