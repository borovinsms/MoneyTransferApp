package ru.netology.server.logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.netology.server.model.Transaction;
import ru.netology.server.model.Transfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LoggerImpl implements Logger {

    private final Lock lock;
    private final Path pathLogFile;

    public LoggerImpl(@Value("${PATH_LOG_FILE:src/main/resources/files/log.txt}") String pathLogFile) {
        this.lock = new ReentrantLock();
        this.pathLogFile = Path.of(pathLogFile);
    }

    private void fileWrite(String content) {
        try {
            lock.lock();
            if (!Files.exists(pathLogFile)) Files.createFile(pathLogFile);
            if (Files.exists(pathLogFile) && Files.isWritable(pathLogFile)) {
                Files.write(pathLogFile, content.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void log(Transaction transaction) {
        final var dateTime = LocalDateTime.now();
        final var message = "Date and time: " + dateTime + "\n" +
                "Operation id: " + transaction.getId() + "\n" +
                "From card: " + transaction.getTransfer().getCardFromNumber() + "\n" +
                "To card: " + transaction.getTransfer().getCardToNumber() + "\n" +
                "Amount: " + transaction.getTransfer().getAmount().getValue() + "\n" +
                "Currency: " + transaction.getTransfer().getAmount().getCurrency() + "\n" +
                "Transfer fee sum: " + transaction.getTransferFee() + "\n" +
                (transaction.isExecuted() ? "Operation is executed" : "Operation is not completed") + "\n\n";
        fileWrite(message);
    }

    @Override
    public void log(Exception e, Transaction transaction) {
        final var dateTime = LocalDateTime.now();
        final var message = "Date and time: " + dateTime + "\n" +
                "Operation id: " + transaction.getId() + "\n" +
                "From card: " + transaction.getTransfer().getCardFromNumber() + "\n" +
                "To card: " + transaction.getTransfer().getCardToNumber() + "\n" +
                "Amount: " + transaction.getTransfer().getAmount().getValue() + "\n" +
                "Currency: " + transaction.getTransfer().getAmount().getCurrency() + "\n" +
                "Transfer fee sum: " + transaction.getTransferFee() + "\n" +
                (transaction.isExecuted() ? "Operation is executed" : "Operation is not executed") + "\n" +
                e.getMessage() + "\n\n";
        fileWrite(message);
    }

    @Override
    public void log(Exception e, Transfer transfer) {
        final var dateTime = LocalDateTime.now();
        final var message = "Date and time: " + dateTime + "\n" +
                "From card: " + transfer.getCardFromNumber() + "\n" +
                "To card: " + transfer.getCardToNumber() + "\n" +
                "Amount: " + transfer.getAmount().getValue() + "\n" +
                "Currency: " + transfer.getAmount().getCurrency() + "\n" +
                e.getMessage() + "\n\n";
        fileWrite(message);
    }
}
