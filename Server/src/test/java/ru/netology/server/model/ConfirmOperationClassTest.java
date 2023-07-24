package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ConfirmOperationClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running ConfirmOperationClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("ConfirmOperationClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    public void gettersTest(ConfirmOperation confirmOperation, String operationId, String code) {
//        act
        final var resultId = confirmOperation.operationId();
        final var resultCode = confirmOperation.code();
//        assert
        Assertions.assertEquals(resultId, operationId);
        Assertions.assertEquals(resultCode, code);
    }

    private static Stream<Arguments> parametersForGettersTest() {
        return Stream.of(
                Arguments.of(new ConfirmOperation("100", "0000"), "100", "0000"),
                Arguments.of(new ConfirmOperation("", " "), "", " "),
                Arguments.of(new ConfirmOperation(null, null), null, null)
        );
    }
}
