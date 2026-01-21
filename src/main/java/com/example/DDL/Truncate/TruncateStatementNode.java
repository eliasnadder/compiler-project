package com.example.DDL.Truncate;

import com.example.ASTNode;

public abstract class TruncateStatementNode extends ASTNode {
    public TruncateStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }
    @Override
    public abstract String getNodeType();


}
