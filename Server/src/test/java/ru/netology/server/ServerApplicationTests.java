package ru.netology.server;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.testcontainers.containers.GenericContainer;
import ru.netology.server.model.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String CARDS_PATH = "src/test/resources/cards.json";
    private static final String TRANSACTIONS_PATH = "src/test/resources/transactions.json";
    private static final int PORT = 5500;
    private static final GenericContainer<?> SERVER =
            new GenericContainer<>("money-sharing-server:1.0").withExposedPorts(PORT);

    private static String cardsJson;
    private static String transactionsJson;
    private static long suiteStartTime;

    private long testStartTime;

    @BeforeAll
    public static void initSuite() throws IOException {
        System.out.println("Running ServerApplicationTests");
        suiteStartTime = System.nanoTime();

        cardsJson = Files.readString(Path.of(CARDS_PATH));
        transactionsJson = Files.readString(Path.of(TRANSACTIONS_PATH));
        SERVER.start();
    }

    @AfterAll
    public static void completeSuite() throws IOException {
        System.out.println("ServerApplicationTests complete: " + (System.nanoTime() - suiteStartTime));

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

    @ParameterizedTest
    @MethodSource("parametersForContextLoads")
    <T, R> void contextLoads(String path, R body, T response) {

        final var url = "http://localhost:" + SERVER.getMappedPort(PORT) + path;

        final var request = new RequestEntity<>(body, HttpMethod.POST, URI.create(url));

        final var entity = restTemplate.exchange(url, HttpMethod.POST, request, response.getClass());

        Assertions.assertEquals(entity.getBody(), response);
    }

    private static Stream<Arguments> parametersForContextLoads() {

        var expectedId = 1;
        final var transferPath = "/transfer";
        final var confirmOperationPath = "/confirmOperation";

        final var code = "0000";

        final var invalidCredentials = "Введены неверные данные";
        final var noMoney = "Недостаточно средств на счете";
        final var invalidCodeMsg = "Неправильный код верификации";

        final var succesfullResponse = new SuccessfulResponse(String.valueOf(expectedId));

        final var transferInvalidCredentialsResponse = new ExceptionResponse(invalidCredentials, 400);
        final var transferInsufficientFundsResponse = new ExceptionResponse(noMoney, 500);

        final var confirmInvalidCredentialsResponse = new ExceptionResponse(invalidCodeMsg, 400);
        final var confirmInsufficientFundsResponse = new ExceptionResponse(noMoney, 500);

        final var transfer1 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 1999999));

        final var transfer2 =
                new Transfer(
                        "3333333333333333",
                        "????????????????",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer3 =
                new Transfer(
                        "????????????????",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer4 =
                new Transfer(
                        "3333333333333333",
                        "2",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer5 =
                new Transfer(
                        "3",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer6 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "33",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer7 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "0530",
                        new Amount("RUR", 4141));

        final var transfer8 =
                new Transfer(
                        "3333333333333333",
                        "3333333333333333",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer9 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "02/25",
                        new Amount("RUR", 4141));

        final var transfer10 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "???",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer11 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 0));

        final var transfer12 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", -5));

        final var transfer13 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("EUR", 4141));

        final var transfer14 =
                new Transfer(
                        "3333333333333333",
                        "5555555555555555",
                        "333",
                        "05/30",
                        new Amount("RUR", 4141));

        final var transfer15 =
                new Transfer(
                        "3333333333333333",
                        "2222222222222222",
                        "333",
                        "05/30",
                        new Amount("RUR", 5500000));

        final var secondExpectedId = expectedId + 1;
        final var newSuccesfullResponse = new SuccessfulResponse(String.valueOf(secondExpectedId));

        final var confirmOperation1 = new ConfirmOperation(String.valueOf(expectedId), "????");

        final var confirmOperation2 = new ConfirmOperation(String.valueOf(secondExpectedId), code);

        final var confirmOperation3 = new ConfirmOperation(String.valueOf(expectedId), code);

        return Stream.of(
                Arguments.of(transferPath, transfer1, succesfullResponse),
                Arguments.of(transferPath, transfer2, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer3, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer4, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer5, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer6, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer7, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer8, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer9, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer10, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer11, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer12, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer13, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer14, transferInvalidCredentialsResponse),
                Arguments.of(transferPath, transfer15, transferInsufficientFundsResponse),
                Arguments.of(transferPath, transfer1, newSuccesfullResponse),
                Arguments.of(confirmOperationPath, confirmOperation1, confirmInvalidCredentialsResponse),
                Arguments.of(confirmOperationPath, confirmOperation2, newSuccesfullResponse),
                Arguments.of(confirmOperationPath, confirmOperation3, confirmInsufficientFundsResponse),
                Arguments.of(confirmOperationPath, confirmOperation2, newSuccesfullResponse)
        );
    }
}
