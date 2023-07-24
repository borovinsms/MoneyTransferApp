package ru.netology.server.repository;

import ru.netology.server.model.Card;
import ru.netology.server.model.ConfirmOperation;
import ru.netology.server.model.Transaction;
import ru.netology.server.model.Transfer;

public interface Repository {
    Transaction newTransfer(Transfer transfer, int transferFee, String verificationCode);

    void confirmOperation(ConfirmOperation confirmOperation);

    Card getCard(String id);

    Transaction getTransaction(String id);
}
