package ru.netology.server.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.server.exception.InsufficientFunds;
import ru.netology.server.exception.InvalidCredentials;
import ru.netology.server.logger.Logger;
import ru.netology.server.model.*;
import ru.netology.server.repository.RepositoryImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ServiceClassTest {

    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running ServiceClassTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("ServiceClassTest complete: " + (System.nanoTime() - suiteStartTime));
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
    @MethodSource("parametersForRunTransactionTest")
    public void runTransactionTest(Transfer transfer, int fee, int feeExpected, Card cardTo, Card cardFrom, Long operationId) {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var code = "0000";
        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        final var transaction = new Transaction(operationId, transfer, feeExpected, code);
        final var feeSum = transfer.getAmount().getValue() / 100 * feeExpected;

        Mockito.when(repositoryMock.getCard(transfer.getCardFromNumber())).thenReturn(cardFrom);
        Mockito.when(repositoryMock.getCard(transfer.getCardToNumber())).thenReturn(cardTo);
        Mockito.when(repositoryMock.newTransfer(transfer, feeSum, code)).thenReturn(transaction);
//        act
        final var result = service.runTransaction(transfer, loggerMock);
//        assert
        Assertions.assertEquals(result.operationId(), String.valueOf(operationId));
        Mockito.verify(loggerMock).log(transaction);
    }

    private static Stream<Arguments> parametersForRunTransactionTest() {

        final var currensy = new ConcurrentHashMap<String, Integer>();
        currensy.put("RUR", 1000000);

        final var transfer1 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var fee1 = 3;
        final var feeExpected1 = 3;
        final var cardTo1 = new Card(
                transfer1.getCardToNumber(),
                "any",
                "215",
                currensy
        );
        final var cardFrom1 = new Card(
                transfer1.getCardFromNumber(),
                transfer1.getCardFromValidTill(),
                transfer1.getCardFromCVV(),
                currensy
        );
        final var operationId1 = 1L;

        final var transfer2 =
                new Transfer(
                        "333333333333333355",
                        "2222222222222222666",
                        "xxxx",
                        "12/05/30",
                        new Amount("RUR", 4141));
        final var fee2 = -5;
        final var feeExpected2 = 0;
        final var cardTo2 = new Card(
                transfer2.getCardToNumber(),
                "any",
                "215",
                currensy
        );
        final var cardFrom2 = new Card(
                transfer2.getCardFromNumber(),
                transfer2.getCardFromValidTill(),
                transfer2.getCardFromCVV(),
                currensy
        );
        final var operationId2 = 0L;

        return Stream.of(
                Arguments.of(transfer1, fee1, feeExpected1, cardTo1, cardFrom1, operationId1),
                Arguments.of(transfer2, fee2, feeExpected2, cardTo2, cardFrom2, operationId2)
        );
    }

    @Test
    public void runTransactionTrowInsufficientFundsTest() {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var code = "0000";
        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;
        final var fee = 7;
        final var operationId = 0L;
        final var message = "Недостаточно средств на счете";

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        final var currency = new ConcurrentHashMap<String, Integer>();
        currency.put("RUR", 1000);
        final var transfer = new Transfer(
                "333333333333333355",
                "2222222222222222666",
                "xxxx",
                "12/05/30",
                new Amount("RUR", 4141));
        final var cardFrom = new Card(
                transfer.getCardFromNumber(),
                transfer.getCardFromValidTill(),
                transfer.getCardFromCVV(),
                currency
        );
        final var cardTo = new Card(
                transfer.getCardToNumber(),
                "any",
                "any",
                currency
        );
        final var feeSum = transfer.getAmount().getValue() / 100 * fee;

        final var transaction = new Transaction(operationId, transfer, fee, code);

        Mockito.when(repositoryMock.getCard(transfer.getCardFromNumber())).thenReturn(cardFrom);
        Mockito.when(repositoryMock.getCard(transfer.getCardToNumber())).thenReturn(cardTo);
        Mockito.when(repositoryMock.newTransfer(transfer, feeSum, code)).thenReturn(transaction);
//        assert
        Assertions.assertThrows(InsufficientFunds.class, () -> service.runTransaction(transfer, loggerMock), message);
    }

    @ParameterizedTest
    @MethodSource("parametersForRunTransactionThrowInvalidCredentialsTest")
    public void runTransactionThrowInvalidCredentialsTest(Transfer transfer, Card cardFrom, Card cardTo) {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var code = "code";
        final var fee = 9;
        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;

        final var message = "Введены неверные данные";

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        Mockito.when(repositoryMock.getCard(transfer.getCardFromNumber())).thenReturn(cardFrom);
        Mockito.when(repositoryMock.getCard(transfer.getCardToNumber())).thenReturn(cardTo);
//        assert
        Assertions.assertThrows(InvalidCredentials.class, () -> service.runTransaction(transfer, loggerMock), message);
    }

    private static Stream<Arguments> parametersForRunTransactionThrowInvalidCredentialsTest() {

        final var currensy = new ConcurrentHashMap<String, Integer>();
        currensy.put("RUR", 1000);

        final var transfer1 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom1 = new Card(
                transfer1.getCardFromNumber(),
                transfer1.getCardFromValidTill(),
                transfer1.getCardFromCVV(),
                currensy
        );

        final var transfer2 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardTo2 = new Card(
                transfer2.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer3 =
                new Transfer(
                        "3",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom3 = new Card(
                transfer3.getCardFromNumber(),
                transfer3.getCardFromValidTill(),
                transfer3.getCardFromCVV(),
                currensy
        );
        final var cardTo3 = new Card(
                transfer3.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer4 =
                new Transfer(
                        "3333333333333333",
                        "2",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom4 = new Card(
                transfer4.getCardFromNumber(),
                transfer4.getCardFromValidTill(),
                transfer4.getCardFromCVV(),
                currensy
        );
        final var cardTo4 = new Card(
                transfer4.getCardToNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer5 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "1",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom5 = new Card(
                transfer5.getCardFromNumber(),
                transfer5.getCardFromValidTill(),
                transfer5.getCardFromCVV(),
                currensy
        );
        final var cardTo5 = new Card(
                transfer5.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer6 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "0530",
                        new Amount("RUR", 4141));
        final var cardFrom6 = new Card(
                transfer6.getCardFromNumber(),
                transfer6.getCardFromValidTill(),
                transfer6.getCardFromCVV(),
                currensy
        );
        final var cardTo6 = new Card(
                transfer6.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer7 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom7 = new Card(
                transfer7.getCardFromNumber(),
                transfer7.getCardFromValidTill(),
                transfer7.getCardFromCVV(),
                currensy
        );

        final var transfer8 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom8 = new Card(
                transfer8.getCardFromNumber(),
                "difference valid till",
                transfer8.getCardFromCVV(),
                currensy
        );
        final var cardTo8 = new Card(
                transfer8.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer9 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var cardFrom9 = new Card(
                transfer9.getCardFromNumber(),
                transfer9.getCardFromValidTill(),
                "difference CVV",
                currensy
        );
        final var cardTo9 = new Card(
                transfer9.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer10 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 0));
        final var cardFrom10 = new Card(
                transfer10.getCardFromNumber(),
                transfer10.getCardFromValidTill(),
                transfer10.getCardFromCVV(),
                currensy
        );
        final var cardTo10 = new Card(
                transfer10.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer11 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", -200));
        final var cardFrom11 = new Card(
                transfer11.getCardFromNumber(),
                transfer11.getCardFromValidTill(),
                transfer11.getCardFromCVV(),
                currensy
        );
        final var cardTo11 = new Card(
                transfer11.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer12 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 1));
        final var cardFrom12 = new Card(
                transfer12.getCardFromNumber(),
                transfer12.getCardFromValidTill(),
                transfer12.getCardFromCVV()
        );
        final var cardTo12 = new Card(
                transfer12.getCardFromNumber(),
                "any",
                "any",
                currensy
        );

        final var transfer13 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 1));
        final var cardFrom13 = new Card(
                transfer13.getCardFromNumber(),
                transfer13.getCardFromValidTill(),
                transfer13.getCardFromCVV(),
                currensy
        );
        final var cardTo13 = new Card(
                transfer13.getCardFromNumber(),
                "any",
                "any"
        );

        return Stream.of(
                Arguments.of(transfer1, cardFrom1, null),
                Arguments.of(transfer2, null, cardTo2),
                Arguments.of(transfer3, cardFrom3, cardTo3),
                Arguments.of(transfer4, cardFrom4, cardTo4),
                Arguments.of(transfer5, cardFrom5, cardTo5),
                Arguments.of(transfer6, cardFrom6, cardTo6),
                Arguments.of(transfer7, cardFrom7, cardFrom7),
                Arguments.of(transfer8, cardFrom8, cardTo8),
                Arguments.of(transfer9, cardFrom9, cardTo9),
                Arguments.of(transfer10, cardFrom10, cardTo10),
                Arguments.of(transfer11, cardFrom11, cardTo11),
                Arguments.of(transfer12, cardFrom12, cardTo12),
                Arguments.of(transfer13, cardFrom13, cardTo13)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForConfirmOperationTest")
    public void confirmOperationTest(Transaction transaction, int expected) {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var code = transaction.getVerificationCode();
        final var fee = transaction.getTransferFee();
        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;
        final var operationId = transaction.getId();
        final var transfer = transaction.getTransfer();

        final var currensy = new ConcurrentHashMap<String, Integer>();
        currensy.put("RUR", 1000000);

        final var cardFrom = new Card(
                transfer.getCardFromNumber(),
                transfer.getCardFromValidTill(),
                transfer.getCardFromCVV(),
                currensy
        );

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        final var confirmOperation = new ConfirmOperation(String.valueOf(operationId), code);

        Mockito.when(repositoryMock.getCard(transfer.getCardFromNumber())).thenReturn(cardFrom);
        Mockito.when(repositoryMock.getTransaction(confirmOperation.operationId())).thenReturn(transaction);
//        act
        final var result = service.confirmOperation(confirmOperation, loggerMock);
//        assert
        Assertions.assertEquals(result.operationId(), String.valueOf(operationId));
        Mockito.verify(loggerMock, Mockito.times(expected)).log(transaction);
        Mockito.verify(repositoryMock, Mockito.times(expected)).confirmOperation(confirmOperation);
    }

    private static Stream<Arguments> parametersForConfirmOperationTest() {

        final var operationId = 0L;
        final var fee = 3;
        final var code = "0000";
        final var transfer =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var expexted1 = 1;
        final var transaction1 = new Transaction(operationId, transfer, fee, code);

        final var expexted2 = 0;
        final var transaction2 = new Transaction(operationId, transfer, fee, code);
        transaction2.setExecuted(true);

        return Stream.of(
                Arguments.of(transaction1, expexted1),
                Arguments.of(transaction2, expexted2)
        );
    }

    @Test
    public void confirmOperationTrowInsufficientFundsTest() {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var code = "0000";
        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;
        final var fee = 7;
        final var operationId = 0L;
        final var message = "Недостаточно средств на счете";

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        final var currency = new ConcurrentHashMap<String, Integer>();
        currency.put("RUR", 1000);
        final var transfer = new Transfer(
                "333333333333333355",
                "2222222222222222666",
                "xxxx",
                "12/05/30",
                new Amount("RUR", 4141));
        final var cardFrom = new Card(
                transfer.getCardFromNumber(),
                transfer.getCardFromValidTill(),
                transfer.getCardFromCVV(),
                currency
        );

        final var transaction = new Transaction(operationId, transfer, fee, code);

        final var confirmOperation = new ConfirmOperation(String.valueOf(operationId), code);

        Mockito.when(repositoryMock.getTransaction(String.valueOf(operationId))).thenReturn(transaction);
        Mockito.when(repositoryMock.getCard(transfer.getCardFromNumber())).thenReturn(cardFrom);
//        assert
        Assertions.assertThrows(InsufficientFunds.class,
                () -> service.confirmOperation(confirmOperation, loggerMock),
                message);
    }

    @ParameterizedTest
    @MethodSource("parametersForConfirmOperationTrowInvalidCredentialsTest")
    public void confirmOperationTrowInvalidCredentialsTest(ConfirmOperation confirmOperation, String code) {
//        arrange
        final var loggerMock = Mockito.mock(Logger.class);
        final var repositoryMock = Mockito.mock(RepositoryImpl.class);

        final var operationId = confirmOperation.operationId();
        final var fee = 3;
        final var transfer =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));
        final var transaction = new Transaction(Long.parseLong(operationId), transfer, fee, code);

        final var cardSize = 16;
        final var cvvSize = 3;
        final var dateValidTillSize = 5;
        final var message = "Неправильный код верификации";

        final var service = new Service(repositoryMock, code, fee, cardSize, cvvSize, dateValidTillSize);

        Mockito.when(repositoryMock.getTransaction(operationId)).thenReturn(transaction);
//        assert
        Assertions.assertThrows(
                InvalidCredentials.class, () -> service.confirmOperation(confirmOperation, loggerMock), message);
    }

    private static Stream<Arguments> parametersForConfirmOperationTrowInvalidCredentialsTest() {
        final var code = "0000";
        return Stream.of(
                Arguments.of(new ConfirmOperation("0", null), code),
                Arguments.of(new ConfirmOperation("0", "difference code"), code)
        );
    }
}
