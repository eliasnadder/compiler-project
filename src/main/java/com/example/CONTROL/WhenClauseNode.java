package com.example.CONTROL;

import com.example.Expressions.ExpressionNode;

public class WhenClauseNode extends ControlFlowStatementNode {
    private final ExpressionNode condition;
    private final ExpressionNode result;

    public WhenClauseNode(ExpressionNode condition, ExpressionNode result, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.condition = condition;
        this.result = result;
        addChild(condition);
        addChild(result);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public ExpressionNode getResult() {
        return result;
    }

    @Override
    public String getNodeType() {
        return "WhenClause";
    }

}
