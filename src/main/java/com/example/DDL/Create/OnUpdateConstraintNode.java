package com.example.DDL.Create;

import com.example.Expressions.ExpressionNode;

public class OnUpdateConstraintNode extends ColumnConstraintNode {
    private final ExpressionNode updateValue;

    public OnUpdateConstraintNode(ExpressionNode updateValue, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.updateValue = updateValue;
        addChild(updateValue);
    }

    public ExpressionNode getUpdateValue() {
        return updateValue;
    }

    @Override
    public String getNodeType() {
        return "ON_UPDATE";
    }

}
