package com.example.DDL.Alter;
import com.example.Expressions.ExpressionNode;

public class CheckTableConstraintNode extends TableConstraintNode {
    private final ExpressionNode checkExpression;
    public CheckTableConstraintNode(ExpressionNode checkExpression, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.checkExpression = checkExpression;
        addChild(checkExpression);
    }
    public ExpressionNode getCheckExpression() {
        return checkExpression;
    }
    @Override
    public String getNodeType() {
        return "CHECK_CONSTRAINT (" + checkExpression + ")";
    }


}
