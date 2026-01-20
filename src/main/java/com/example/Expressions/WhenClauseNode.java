package com.example.Expressions;

import com.example.ASTNode;
import com.example.Expressions.ExpressionNode;

public class WhenClauseNode extends ASTNode {
    private ExpressionNode condition;
    private ExpressionNode result;

    public WhenClauseNode(ExpressionNode condition, ExpressionNode result) {
        this.condition = condition;
        this.result = result;
        addChild(condition);
        addChild(result);
    }

    public ExpressionNode getCondition() { return condition; }
    public ExpressionNode getResult() { return result; }

    @Override
    public String getNodeType() {
        return "WHEN_CLAUSE";
    }
}
