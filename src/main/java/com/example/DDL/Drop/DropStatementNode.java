package com.example.DDL.Drop;

import com.example.ASTNode;

public abstract class DropStatementNode extends ASTNode {
    public DropStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public abstract String getNodeType();

}
