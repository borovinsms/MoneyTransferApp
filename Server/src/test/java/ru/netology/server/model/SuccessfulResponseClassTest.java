package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SuccessfulResponseClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running SuccessfulResponseClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("SuccessfulResponseClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    public void gettersTest(SuccessfulResponse successfulResponse, String expected) {
//        act
        final var result = successfulResponse.operationId();
//        assert
        Assertions.assertEquals(result, expected);
    }

    private static Stream<Arguments> parametersForGettersTest() {
        return Stream.of(
                Arguments.of(new SuccessfulResponse("100"), "100"),
                Arguments.of(new SuccessfulResponse(""), ""),
                Arguments.of(new SuccessfulResponse(" "), " "),
                Arguments.of(new SuccessfulResponse(null), null)
        );
    }
}
