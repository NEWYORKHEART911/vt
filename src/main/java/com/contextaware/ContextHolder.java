package com.contextaware;

final class ContextHolder {

    private static final ThreadLocal<TransactionContext> TL = new ThreadLocal<>();

    static void set(TransactionContext ctx) {
        TL.set(ctx);
    }

    static TransactionContext get() {
        return TL.get();
    }

    static void clear() {
        TL.remove();
    }
}

