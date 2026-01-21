package com.example.TRANSACTION;

public class CommitTranasactionNode extends TransactionStatementNode {
    public CommitTranasactionNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public String getNodeType() {
        return "COMMIT TRANSACTION";
    }

}
