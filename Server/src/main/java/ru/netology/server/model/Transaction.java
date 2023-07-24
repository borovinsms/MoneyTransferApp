package ru.netology.server.model;

public class Transaction {

    private final long id;
    private final Transfer transfer;
    private final int comission;
    private final String verificationCode;
    private volatile boolean isExecuted;

    public Transaction(long id, Transfer transfer, int comission, String verificationCode) {
        this.id = id;
        this.transfer = transfer;
        this.comission = comission;
        this.verificationCode = verificationCode;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public long getId() {
        return id;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public int getTransferFee() {
        return comission;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public boolean isExecuted() {
        return isExecuted;
    }
}
