package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.server.model.Amount;

import java.util.stream.Stream;

public class AmountClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running AmountClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("AmountClassTest complete: " + (System.nanoTime() - suiteStartTime));
    }

    @BeforeEach
    public void initTest() {
        System.out.println("Starting new test");
        testStartTime = System.nanoTime();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete: " + (System.nanoTime() - testStartTime));
    }

    @ParameterizedTest
    @MethodSource("parametersForTest")
    public void test(Amount amount, String expectedCurrency, int expectedValue) {
//        act
        final var resultCurrensy = amount.getCurrency();
        final var resultValue = amount.getValue();
//        assert
        Assertions.assertEquals(resultCurrensy, expectedCurrency);
        Assertions.assertEquals(resultValue, expectedValue);
    }

    private static Stream<Arguments> parametersForTest() {
        return Stream.of(
                Arguments.of(new Amount("RUR", 1000), "RUR", 1000),
                Arguments.of(new Amount(null, 0), null, 0),
                Arguments.of(new Amount("", 0), "", 0),
                Arguments.of(new Amount(" ", 0), " ", 0)
        );
    }
}
