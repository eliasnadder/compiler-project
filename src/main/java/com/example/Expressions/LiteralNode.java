package com.example.Expressions;

public class LiteralNode extends ExpressionNode {

    private Object value;

    public LiteralNode(Object value) { this.value = value; }

    public Object getValue() { return value; }

    @Override
    public String getNodeType() { return "LITERAL(" + value + ")"; }
}
