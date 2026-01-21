package com.example.TRANSACTION;

public class RollBackTransactionNode extends TransactionStatementNode {
    private final String savepointName;
    public RollBackTransactionNode(String savepointName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.savepointName = savepointName;
    }
   
    public boolean isFullRollback() {
        return savepointName == null;
    }

    @Override
    public String getNodeType() {
        return savepointName == null
                ? "ROLLBACK"
                : "ROLLBACK TO " + savepointName;
    }
}
