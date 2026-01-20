package com.example.Expressions;

public class CastExpressionNode extends ExpressionNode {

    private ExpressionNode expr;
    private String dataType;

    public CastExpressionNode(ExpressionNode expr, String dataType) {
        this.expr = expr;
        this.dataType = dataType;

        addChild(expr);
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public String getDataType() {
        return dataType;
    }

    @Override
    public String getNodeType() {
        return "CAST(" + dataType + ")";
    }
}
