package com.example.DDL.Create;

import com.example.Expressions.ExpressionNode;

public class DefaultConstraintNode extends ColumnConstraintNode {
    private final ExpressionNode defaultValue;

    public DefaultConstraintNode(ExpressionNode defaultValue, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.defaultValue = defaultValue;
        addChild(defaultValue);
    }

    public ExpressionNode getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getNodeType() {
        return "DEFAULT";
    }

}
