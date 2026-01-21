package com.example.DDL.Alter;

import com.example.ASTNode;

public abstract class TableConstraintNode extends ASTNode {
    public TableConstraintNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }
    @Override
    public abstract String getNodeType();

}
