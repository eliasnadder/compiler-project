package com.example.DML;

import com.example.ASTNode;
import com.example.Expressions.ExpressionNode;

public class UpdateAssignmentNode extends ASTNode {

    private String column;
    private String operator;
    private ExpressionNode value;

    public UpdateAssignmentNode(String column, String operator, ExpressionNode value) {
        this.column = column;
        this.operator = operator;
        this.value = value;

        addChild(value);
    }

    public String getColumn() { return column; }
    public String getOperator() { return operator; }
    public ExpressionNode getValue() { return value; }

    @Override
    public String getNodeType() {
        return "UPDATE_ASSIGNMENT(" + column + " " + operator + ")";
    }
}
