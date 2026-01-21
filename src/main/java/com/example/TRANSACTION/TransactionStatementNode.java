package com.example.TRANSACTION;

import com.example.ASTNode;

public abstract class TransactionStatementNode extends ASTNode {
    public TransactionStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public abstract String getNodeType();

}
