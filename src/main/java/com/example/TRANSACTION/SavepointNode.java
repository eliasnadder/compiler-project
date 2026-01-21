package com.example.TRANSACTION;

public class SavepointNode extends TransactionStatementNode {
    private final String savepointName;

    public SavepointNode(String savepointName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.savepointName = savepointName;
    }

    public String getSavepointName() {
        return savepointName;
    }

    @Override
    public String getNodeType() {
        return "SAVEPOINT ( " + savepointName+" )";
    }

}
