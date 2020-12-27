package com.training.spring.template;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CalSumTest {
    Calculator calculator;
    String filePath;

    @Before
    public void setUp(){
        calculator = new Calculator();
        filePath = getClass().getClassLoader().getResource("chapter03/numbers.txt").getPath();
    }
    @Test
    public void sumOfNumbers() throws IOException {
        int sum = calculator.calcSum(this.filePath);
        assertThat(sum, is(10));
    }
    @Test
    public void multiplyOfNumbers() throws IOException {
        int mul = calculator.calcMultiply(this.filePath);
        assertEquals(24, mul);
    }
}
