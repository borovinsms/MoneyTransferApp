package ru.netology.server.repository;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import ru.netology.server.exception.InsufficientFunds;
import ru.netology.server.model.Card;
import ru.netology.server.model.ConfirmOperation;
import ru.netology.server.model.Transaction;
import ru.netology.server.model.Transfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository {

    private final Lock lock;
    private final Path pathCards;
    private final Path pathTransaction;
    private final ConcurrentMap<String, Card> cards;
    private final ConcurrentMap<String, Transaction> transactions;
    private final AtomicLong transactionCounter;

    public RepositoryImpl(@Value("${PATH_CARDS_FILE:src/main/resources/files/cards.json}") String pathCardsFile,
                          @Value("${PATH_TRANSACTION_FILE:src/main/resources/files/transactions.json}") String pathTransactionFile) {
        this.lock = new ReentrantLock();
        this.pathCards = Path.of(pathCardsFile);
        this.pathTransaction = Path.of(pathTransactionFile);
        this.cards = new ConcurrentHashMap<>();
        this.transactions = new ConcurrentHashMap<>();
        try {
            for (var card : readFile(pathCards, Card.class)) {
                cards.put(card.getId(), card);
            }
            for (var transaction : readFile(pathTransaction, Transaction.class)) {
                transactions.put(String.valueOf(transaction.getId()), transaction);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.transactionCounter = new AtomicLong(transactions.size());
    }

    private <T> List<T> readFile(Path path, Class<T> classOft) throws IOException {
        final var list = new ArrayList<T>();
        if (!Files.exists(path)) {
            Files.createFile(path);
        } else {
            final var gson = new Gson();
            for (var element : Files.readString(path).split("\n")) {
                if (!element.isEmpty()) list.add(gson.fromJson(element, classOft));
            }
        }
        return list;
    }

    private <T> void writeFile(Collection<T> list, Path path) {
        try {
            lock.lock();
            if (Files.exists(path) && Files.isWritable(path)) {
                final var gson = new Gson();
                final var stringbuilder = new StringBuilder();
                for (var element : list) {
                    stringbuilder.append(gson.toJson(element)).append("\n");
                }
                Files.write(path, stringbuilder.toString().getBytes());
            }
        } catch (IOException e) {
            throw new InsufficientFunds(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Transaction newTransfer(Transfer transfer, int transferFee, String verificationCode) {
        final var id = transactionCounter.incrementAndGet();
        final var transaction = new Transaction(id, transfer, transferFee, verificationCode);
        transactions.put(String.valueOf(id), transaction);
        writeFile(transactions.values(), pathTransaction);
        return transaction;
    }

    @Override
    public void confirmOperation(ConfirmOperation confirmOperation) {
        final var transaction = transactions.get(confirmOperation.operationId());
        final var amount = transaction.getTransfer().getAmount();
        cards.get(transaction.getTransfer().getCardFromNumber()).moneyOut(amount, transaction.getTransferFee());
        cards.get(transaction.getTransfer().getCardToNumber()).moneyIn(amount);
        transaction.setExecuted(true);
        writeFile(cards.values(), pathCards);
        writeFile(transactions.values(), pathTransaction);
    }

    @Override
    public Card getCard(String id) {
        return cards.get(id);
    }

    @Override
    public Transaction getTransaction(String id) {
        return transactions.get(id);
    }
}
