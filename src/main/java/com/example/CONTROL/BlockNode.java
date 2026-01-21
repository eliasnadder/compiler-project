package com.example.CONTROL;

import java.util.ArrayList;
import java.util.List;

import com.example.ASTNode;

public class BlockNode extends ASTNode {
    private final List<ASTNode> statements = new ArrayList<>();
    public BlockNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }
    public void addStatement(ASTNode statement) {
        if (statement != null) {
            statements.add(statement);
            addChild(statement);
        }
    }
    public List<ASTNode> getStatements() {
        return statements;
    }
    @Override
    public String getNodeType() {
        return "BLOCK";
    }


}
