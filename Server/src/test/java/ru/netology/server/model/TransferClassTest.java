package ru.netology.server.model;

import org.junit.jupiter.api.*;

public class TransferClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running TransferClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("TransferClassTest complete: " + (System.nanoTime() - suiteStartTime));
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

    @Test
    public void gettersTest() {
//        arrange
        final var amount = new Amount("RUR", 0);
        final var transfer = new Transfer("", " ", null, "null", amount);
//        act
        final var resultCardFromNumber = transfer.getCardFromNumber();
        final var resultCardToNumber = transfer.getCardToNumber();
        final var resultCardFromCVV = transfer.getCardFromCVV();
        final var resultCardFromValidTill = transfer.getCardFromValidTill();
        final var resultAmount = transfer.getAmount();
//        assert
        Assertions.assertEquals(resultCardFromNumber, "");
        Assertions.assertEquals(resultCardToNumber, " ");
        Assertions.assertNull(resultCardFromCVV);
        Assertions.assertEquals(resultCardFromValidTill, "null");
        Assertions.assertNotNull(resultAmount);
        Assertions.assertEquals(resultAmount.getClass(), Amount.class);
    }
}
