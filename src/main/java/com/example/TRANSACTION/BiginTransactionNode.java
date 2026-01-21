package com.example.TRANSACTION;

public class BiginTransactionNode extends TransactionStatementNode {
    private final boolean explicitTransaction;

    public BiginTransactionNode(boolean explicitTransaction, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.explicitTransaction = explicitTransaction;
    }

    public boolean isExplicitTransaction() {
        return explicitTransaction;
    }

    @Override
    public String getNodeType() {
        return explicitTransaction ? "BEGIN TRANSACTION" : "BEGIN";
    }
}
