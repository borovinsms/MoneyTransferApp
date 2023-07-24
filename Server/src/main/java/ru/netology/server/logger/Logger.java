package ru.netology.server.logger;

import ru.netology.server.model.Transaction;
import ru.netology.server.model.Transfer;

public interface Logger {

    void log(Transaction transaction);

    void log(Exception e, Transaction transaction);

    void log(Exception e, Transfer transfer);
}
