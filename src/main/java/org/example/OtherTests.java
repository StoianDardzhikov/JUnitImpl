package org.example;

import org.example.Annotations.Test;
import org.example.junit.Assertions;

public class OtherTests {
    @Test
    public void secondClassTest() {
        Assertions.assertEquals(20, 20);
    }

    @Test
    public void secondClassTest2() {
        Assertions.assertEquals(19, 20);
    }
}