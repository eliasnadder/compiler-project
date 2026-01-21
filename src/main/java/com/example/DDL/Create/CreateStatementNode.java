package com.example.DDL.Create;

import com.example.ASTNode;

public class CreateStatementNode extends ASTNode {
    public CreateStatementNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public String getNodeType() {
        return "CREATE_STATEMENT";
    }
}
