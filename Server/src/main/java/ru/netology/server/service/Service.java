package ru.netology.server.service;

import org.springframework.beans.factory.annotation.Value;
import ru.netology.server.exception.InsufficientFunds;
import ru.netology.server.exception.InvalidCredentials;
import ru.netology.server.logger.Logger;
import ru.netology.server.model.ConfirmOperation;
import ru.netology.server.model.SuccessfulResponse;
import ru.netology.server.model.Transfer;
import ru.netology.server.repository.Repository;

@org.springframework.stereotype.Service
public class Service {

    private final int cardSize;
    private final int cvvSize;
    private final int dateValidTillSize;
    private final int transferFee;
    private final String verificationCode;
    private final Repository repository;

    public Service(Repository repository,
                   @Value("${VERIFICATION_CODE:0000}") String verificationCode,
                   @Value("${TRANSFER_FEE:1}") int transferFee,
                   @Value("${CARD_SIZE:16}") int cardSize,
                   @Value("${CVV_SIZE:3}") int cvvSize,
                   @Value("${DATE_VALID_TILL_SIZE:5}") int dateValidTillSize) {
        this.repository = repository;
        this.transferFee = transferFee;
        this.verificationCode = verificationCode;
        this.cardSize = cardSize;
        this.cvvSize = cvvSize;
        this.dateValidTillSize = dateValidTillSize;
    }

    public SuccessfulResponse runTransaction(Transfer transfer, Logger logger) {
        final var cardTo = repository.getCard(transfer.getCardToNumber());
        final var cardFrom = repository.getCard(transfer.getCardFromNumber());
        final var amountValue = transfer.getAmount().getValue();
        final var currency = transfer.getAmount().getCurrency();
        if (cardTo != null
                && cardFrom != null
                && cardTo.getId().length() >= cardSize
                && cardFrom.getId().length() >= cardSize
                && transfer.getCardFromCVV().length() >= cvvSize
                && transfer.getCardFromValidTill().length() >= dateValidTillSize
                && !cardFrom.equals(cardTo)
                && cardFrom.getValidTill().equals(transfer.getCardFromValidTill())
                && cardFrom.getCvv().equals(transfer.getCardFromCVV())
                && amountValue > 0
                && cardFrom.getBalance(currency) >= 0
                && cardTo.getBalance(currency) >= 0) {
            final var transferFeeSum = transferFee > 0 ? amountValue / 100 * transferFee :
                    0;
            if (cardFrom.getBalance(currency) >= (amountValue + transferFeeSum)) {
                final var transaction = repository.newTransfer(transfer, transferFeeSum, verificationCode);
                logger.log(transaction);
                return new SuccessfulResponse(String.valueOf(transaction.getId()));
            } else {
                final var exception = new InsufficientFunds("Недостаточно средств на счете");
                logger.log(exception, transfer);
                throw exception;
            }
        } else {
            final var exception = new InvalidCredentials("Введены неверные данные");
            logger.log(exception, transfer);
            throw exception;
        }
    }

    public SuccessfulResponse confirmOperation(ConfirmOperation confirmOperation, Logger logger) {
        final var operationId = confirmOperation.operationId();
        final var transaction = repository.getTransaction(operationId);
        final var codeConfirm = confirmOperation.code();
        if (codeConfirm != null && codeConfirm.equals(transaction.getVerificationCode())) {
            if (!transaction.isExecuted()) {
                final var cardFrom = repository.getCard(transaction.getTransfer().getCardFromNumber());
                final var balance = cardFrom.getBalance(transaction.getTransfer().getAmount().getCurrency());
                final var amount = transaction.getTransfer().getAmount().getValue();
                final var transferFeeSum = transaction.getTransferFee();
                if (balance < (amount + transferFeeSum)) {
                    final var exception = new InsufficientFunds("Недостаточно средств на счете");
                    logger.log(exception, transaction);
                    throw exception;
                }
                repository.confirmOperation(confirmOperation);
                logger.log(transaction);
            }
            return new SuccessfulResponse(operationId);
        } else {
            final var exception = new InvalidCredentials("Неправильный код верификации");
            logger.log(exception, transaction);
            throw exception;
        }
    }
}
