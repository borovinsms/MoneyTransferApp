package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ExceptionResponseClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running ExceptionResponseClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("ExceptionResponseClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    @MethodSource("parametersForGettersTest")
    public void gettersTest(ExceptionResponse exceptionResponse, String expectedMessage, int expectedId) {
//        act
        final var resultId = exceptionResponse.id();
        final var resultMessage = exceptionResponse.message();
//        assert
        Assertions.assertEquals(resultId, expectedId);
        Assertions.assertEquals(resultMessage, expectedMessage);
    }

    private static Stream<Arguments> parametersForGettersTest() {
        return Stream.of(
                Arguments.of(new ExceptionResponse("100", 0), "100", 0),
                Arguments.of(new ExceptionResponse("", 222), "", 222)
        );
    }
}
