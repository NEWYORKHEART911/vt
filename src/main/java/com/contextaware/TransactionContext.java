package com.contextaware;

public final class TransactionContext {
    public final String txnId;
    public final long startTimeNanos;
    // ...any logging metadata...

    public TransactionContext(String txnId) {
        this.txnId = txnId;
        this.startTimeNanos = System.nanoTime();
    }

    public TransactionContext copy() {
        return new TransactionContext(txnId);
    }
}

