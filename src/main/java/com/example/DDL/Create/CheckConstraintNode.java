package com.example.DDL.Create;
import com.example.Expressions.ExpressionNode;
public class CheckConstraintNode extends ColumnConstraintNode{
    private final ExpressionNode condition;
    public CheckConstraintNode(ExpressionNode condition, int lineNumber , int columnNumber){
        super(lineNumber, columnNumber);
        this.condition = condition;
        addChild(condition);
    }
    public ExpressionNode getCondition() {
        return condition;
    }

    @Override
    public String getNodeType() {
        return "CHECK";
    }

}
