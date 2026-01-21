package com.example.Expressions;

public class BinaryExpressionNode extends ExpressionNode {

    private String operator;
    private ExpressionNode left;
    private ExpressionNode right;

    public BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;

        addChild(left);
        addChild(right);
    }

    public String getOperator() {
        return operator;
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String getNodeType() {
        return "BINARY(" + operator + ")";
    }
}
