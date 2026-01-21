package com.example.CONTROL;

import com.example.ASTNode;

public class WhileStatementNode extends ControlFlowStatementNode{
   private final ASTNode condition;
    private final ASTNode body;

    public WhileStatementNode(ASTNode condition,
                              ASTNode body,
                              int line,
                              int col) {
        super(line, col);
        this.condition = condition;
        this.body = body;

        addChild(condition);
        addChild(body);
    }
    @Override
    public String getNodeType() {
        return "WHILE STATEMENT";
    }

}
