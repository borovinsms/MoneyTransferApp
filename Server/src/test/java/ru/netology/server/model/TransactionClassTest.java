package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TransactionClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running TransactionClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("TransactionClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    public void gettersTest(Transaction transaction, long id, int fee, String code, boolean isExecuted) {
//        act
        final var resultId = transaction.getId();
        final var resultCode = transaction.getVerificationCode();
        final var resultIsExecuted = transaction.isExecuted();
        final var resultFee = transaction.getTransferFee();
        final var resultTransfer = transaction.getTransfer();
//        assert
        Assertions.assertEquals(resultId, id);
        Assertions.assertEquals(resultFee, fee);
        Assertions.assertEquals(resultCode, code);
        Assertions.assertEquals(resultIsExecuted, isExecuted);
        Assertions.assertNotNull(resultTransfer);
        Assertions.assertEquals(resultTransfer.getClass(), Transfer.class);
    }

    private static Stream<Arguments> parametersForGettersTest() {
        final var amount = new Amount("RUR", 0);
        final var transfer = new Transfer("00", "11", "", " ", amount);
        return Stream.of(
                Arguments.of(new Transaction(1, transfer, 2, "00"), 1L, 2, "00", false),
                Arguments.of(new Transaction(3333333333L, transfer, 0, "code"),
                        3333333333L, 0, "code", false)
        );
    }

    @Test
    public void setExecutedTest() {
//        arrange
        final var amount = new Amount("RUR", 0);
        final var transfer = new Transfer("00", "11", "", " ", amount);
        final var transaction = new Transaction(0, transfer, 0, "");
//        act
        final var resultPre = transaction.isExecuted();
        transaction.setExecuted(true);
        final var resultPost = transaction.isExecuted();
//        assert
        Assertions.assertTrue(resultPost);
        Assertions.assertFalse(resultPre);
    }
}
