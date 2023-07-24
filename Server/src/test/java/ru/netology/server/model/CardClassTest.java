package ru.netology.server.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class CardClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running CardClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("CardClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    public void gettersTest(Card card, String id, String validTill, String cvv, int balance, String currency) {
//        act
        final var resultId = card.getId();
        final var resultValidTill = card.getValidTill();
        final var resultCvv = card.getCvv();
        final var resultBalance = card.getBalance(currency);
//        assert
        Assertions.assertEquals(resultId, id);
        Assertions.assertEquals(resultValidTill, validTill);
        Assertions.assertEquals(resultCvv, cvv);
        Assertions.assertEquals(resultBalance, balance);
    }

    private static Stream<Arguments> parametersForGettersTest() {
        final var currencies = new ConcurrentHashMap<String, Integer>();
        currencies.put("RUR", 100000);
        return Stream.of(
                Arguments.of(new Card("100", "05/30", "333"), "100", "05/30", "333", -1, "RUR"),
                Arguments.of(new Card(null, " ", "", currencies), null, " ", "", 100000, "RUR")
        );
    }

    @Test
    public void moneyInTest() {
//        arrange
        final var expectedPre = 100000;
        final var expectedPost = 155555;
        final var currencies = new ConcurrentHashMap<String, Integer>();
        currencies.put("RUR", 100000);
        final var card = new Card("", "", "", currencies);
        final var amount = new Amount("RUR", 55555);
//        act
        final var resultPre = card.getBalance(amount.getCurrency());
        card.moneyIn(amount);
        final var resultPost = card.getBalance(amount.getCurrency());
//        assert
        Assertions.assertEquals(resultPre, expectedPre);
        Assertions.assertEquals(resultPost, expectedPost);
    }

    @Test
    public void moneyOutTest() {
//        arrange
        final var expectedPre = 100000;
        final var expectedPost = 44112;
        final var fee = 333;
        final var currencies = new ConcurrentHashMap<String, Integer>();
        currencies.put("RUR", 100000);
        final var card = new Card("", "", "", currencies);
        final var amount = new Amount("RUR", 55555);
//        act
        final var resultPre = card.getBalance(amount.getCurrency());
        card.moneyOut(amount, fee);
        final var resultPost = card.getBalance(amount.getCurrency());
//        assert
        Assertions.assertEquals(resultPre, expectedPre);
        Assertions.assertEquals(resultPost, expectedPost);
    }
}
