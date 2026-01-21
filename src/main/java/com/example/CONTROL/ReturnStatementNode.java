package com.example.CONTROL;

import com.example.ASTNode;

public class ReturnStatementNode extends ControlFlowStatementNode {
    
    private final ASTNode expression;

    public ReturnStatementNode(ASTNode expression,
                               int line,
                               int col) {
        super(line, col);
        this.expression = expression;

        if (expression != null) {
            addChild(expression);
        }
    }
    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public String getNodeType() {
        return "RETURN";
    }

}
