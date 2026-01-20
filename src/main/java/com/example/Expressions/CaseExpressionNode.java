package com.example.Expressions;

import java.util.List;

public class CaseExpressionNode extends ExpressionNode {

    private List<WhenClause> whenClauses;
    private ExpressionNode elseExpression;

    public static class WhenClause {
        private ExpressionNode condition;
        private ExpressionNode result;

        public WhenClause(ExpressionNode condition, ExpressionNode result) {
            this.condition = condition;
            this.result = result;
        }

        public ExpressionNode getCondition() {
            return condition;
        }

        public ExpressionNode getResult() {
            return result;
        }
    }

    public CaseExpressionNode(List<WhenClause> whenClauses, ExpressionNode elseExpression) {
        this.whenClauses = whenClauses;
        this.elseExpression = elseExpression;

        whenClauses.forEach(wc -> {
            addChild(wc.getCondition());
            addChild(wc.getResult());
        });
        if (elseExpression != null)
            addChild(elseExpression);
    }

    public List<WhenClause> getWhenClauses() {
        return whenClauses;
    }

    public ExpressionNode getElseExpression() {
        return elseExpression;
    }

    @Override
    public String getNodeType() {
        return "CASE";
    }
}
