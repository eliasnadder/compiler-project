package com.example.CONTROL;

import com.example.ASTNode;

public abstract class ControlFlowStatementNode extends ASTNode {
    public ControlFlowStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public abstract String getNodeType();

}
