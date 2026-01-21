package com.example.DDL.Alter;

import com.example.ASTNode;

public abstract class AlterTableActionNode extends ASTNode {
    public AlterTableActionNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public abstract String getNodeType();

}
