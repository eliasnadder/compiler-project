package com.example.Expressions;

public class UnaryExpressionNode extends ExpressionNode {

    private String operator;
    private ExpressionNode operand;

    public UnaryExpressionNode(
        String operator, 
        ExpressionNode operand
    ) {
        this.operator = operator;
        this.operand = operand;

        addChild(operand);
    }

    public String getOperator() { return operator; }
    public ExpressionNode getOperand() { return operand; }

    @Override
    public String getNodeType() { return "UNARY(" + operator + ")"; }
    
}
