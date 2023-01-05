package org.example;

import org.example.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

public class Methods {
    List<Method> beforeEachMethods = new ArrayList<>();
    List<Method> afterClassMethods = new ArrayList<>();
    List<Method> afterEachMethods = new ArrayList<>();
    Map<String, Test> tests = new HashMap<>();

    public void addBeforeEachMethod(Method method) {
        beforeEachMethods.add(method);
    }

    public void addAfterClassMethod(Method method) {
        afterClassMethods.add(method);
    }

    public void addAfterEachMethods(Method method) {
        afterEachMethods.add(method);
    }

    public void addTest(Test test) {
        tests.put(test.getDisplayName(), test);
    }

    public Collection<Test> getTests() {
        return tests.values();
    }

    public Test getTest(String name) {
        return tests.get(name);
    }
}