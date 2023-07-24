package ru.netology.server.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.server.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class RepositoryImplClassTest {

    private static final String CARDS_PATH = "src/test/resources/cards.json";
    private static final String TRANSACTIONS_PATH = "src/test/resources/transactions.json";

    private static long suiteStartTime;
    private static String cardsJson;
    private static String transactionsJson;
    private static RepositoryImpl repository;

    private long testStartTime;

    @BeforeAll
    public static void initSuite() throws IOException {
        System.out.println("Running RepositoryImplClassTest");
        suiteStartTime = System.nanoTime();

        cardsJson = Files.readString(Path.of(CARDS_PATH));
        transactionsJson = Files.readString(Path.of(TRANSACTIONS_PATH));
        repository = new RepositoryImpl(CARDS_PATH, TRANSACTIONS_PATH);
    }

    @AfterAll
    public static void completeSuite() throws IOException {
        System.out.println("RepositoryImplClassTest complete: " + (System.nanoTime() - suiteStartTime));

        Files.writeString(Path.of(CARDS_PATH), cardsJson);
        Files.writeString(Path.of(TRANSACTIONS_PATH), transactionsJson);
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
        final var currency = "RUR";
        final var currencies = new ConcurrentHashMap<String, Integer>();
        currencies.put(currency, 2796778);
        final var card = new Card("3333333333333333", "05/30", "333", currencies);

        final var amount = new Amount("RUR", 300000);
        final var transfer =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        amount);
        final var transaction = new Transaction(1, transfer, 3000, "0000");
        transaction.setExecuted(true);

        final var cardId = card.getId();
        final var transactionId = transaction.getId();
//        act
        final var resultCard = repository.getCard(cardId);
        final var resultTransaction = repository.getTransaction(String.valueOf(transactionId));
        final var resultTransfer = resultTransaction.getTransfer();
        final var resultAmount = resultTransfer.getAmount();
//        assert
        Assertions.assertEquals(resultCard.getId(), card.getId());
        Assertions.assertEquals(resultCard.getCvv(), card.getCvv());
        Assertions.assertEquals(resultCard.getValidTill(), card.getValidTill());
        Assertions.assertEquals(resultCard.getBalance(currency), card.getBalance(currency));

        Assertions.assertEquals(resultTransaction.getId(), transactionId);
        Assertions.assertEquals(resultTransaction.getVerificationCode(), transaction.getVerificationCode());
        Assertions.assertEquals(resultTransaction.getTransferFee(), transaction.getTransferFee());
        Assertions.assertEquals(resultTransaction.isExecuted(), transaction.isExecuted());

        Assertions.assertEquals(resultTransfer.getCardFromNumber(), transfer.getCardFromNumber());
        Assertions.assertEquals(resultTransfer.getCardToNumber(), transfer.getCardToNumber());
        Assertions.assertEquals(resultTransfer.getCardFromValidTill(), transfer.getCardFromValidTill());
        Assertions.assertEquals(resultTransfer.getCardFromCVV(), transfer.getCardFromCVV());

        Assertions.assertEquals(resultAmount.getCurrency(), amount.getCurrency());
        Assertions.assertEquals(resultAmount.getValue(), amount.getValue());
    }

    @ParameterizedTest
    @MethodSource("parametersForNewTransferTest")
    public void newTransferTest(Transfer transfer, int transferFee, String verificationCode, int id, boolean isExecute) {
//        arrange
        final var currency = transfer.getAmount().getCurrency();
        final var cardFrom = repository.getCard(transfer.getCardFromNumber());
        final var cardTo = repository.getCard(transfer.getCardToNumber());
//        act
        final var balanceFromPre = cardFrom.getBalance(currency);
        final var balanceToPre = cardTo.getBalance(currency);

        final var transactionFromNewTransfer = repository.newTransfer(transfer, transferFee, verificationCode);
        final var transactionFromGetter = repository.getTransaction(String.valueOf(transactionFromNewTransfer.getId()));
        final var transactionIsExecute = transactionFromNewTransfer.isExecuted();

        final var balanceFromAfter = cardFrom.getBalance(currency);
        final var balanceToAfter = cardTo.getBalance(currency);
//        assert
        Assertions.assertEquals(transactionFromNewTransfer.getId(), id);
        Assertions.assertEquals(transactionIsExecute, isExecute);
        Assertions.assertNotNull(transactionFromGetter);
        Assertions.assertEquals(transactionFromGetter.getClass(), Transaction.class);
        Assertions.assertEquals(transactionFromGetter.getId(), id);

        Assertions.assertEquals(balanceFromPre, balanceFromAfter);
        Assertions.assertEquals(balanceToPre, balanceToAfter);
    }

    private static Stream<Arguments> parametersForNewTransferTest() {

        final var isExecute = false;

        final var id1 = 5;
        final var code1 = "0000";
        final var fee1 = 41;
        final var transfer1 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var id2 = 6;
        final var code2 = "CODE";
        final var fee2 = 107;
        final var transfer2 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 3233));

        return Stream.of(
                Arguments.of(transfer1, fee1, code1, id1, isExecute),
                Arguments.of(transfer2, fee2, code2, id2, isExecute)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForConfirmOperationTest")
    public void confirmOperationTest(
            ConfirmOperation confirmOperation,
            int balanceFromPre,
            int balanceFromAfter,
            int balanceToPre,
            int balanceToAfter,
            boolean isExecute) {
//        arrange
        final var transaction = repository.getTransaction(confirmOperation.operationId());
        final var currency = transaction.getTransfer().getAmount().getCurrency();
        final var cardFrom = repository.getCard(transaction.getTransfer().getCardFromNumber());
        final var cardTo = repository.getCard(transaction.getTransfer().getCardToNumber());

//        act
        final var resulBalanceFromPre = cardFrom.getBalance(currency);
        final var resulBalanceToPre = cardTo.getBalance(currency);

        repository.confirmOperation(confirmOperation);

        final var resultIsExecute = transaction.isExecuted();
        final var resulBalanceFromAfter = cardFrom.getBalance(currency);
        final var resulBalanceToAfter = cardTo.getBalance(currency);
//        assert
        Assertions.assertEquals(resulBalanceFromPre, balanceFromPre);
        Assertions.assertEquals(resulBalanceToPre, balanceToPre);
        Assertions.assertEquals(resultIsExecute, isExecute);
        Assertions.assertEquals(resulBalanceFromAfter, balanceFromAfter);
        Assertions.assertEquals(resulBalanceToAfter, balanceToAfter);
    }

    private static Stream<Arguments> parametersForConfirmOperationTest() {
        return Stream.of(
                Arguments.of(
                        new ConfirmOperation("5", "0000"), 2796778, 2792596, 1197778, 1201919, true),
                Arguments.of(
                        new ConfirmOperation("6", "CODE"), 2792596, 2789256, 1201919, 1205152, true)
        );
    }
}
