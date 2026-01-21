package com.example.DDL.Alter;

import com.example.ASTNode;

public abstract class AlterStatementNode extends ASTNode {
    public AlterStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public abstract String getNodeType();

}
