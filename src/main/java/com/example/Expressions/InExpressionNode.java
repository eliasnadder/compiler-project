package com.example.Expressions;

import java.util.List;

public class InExpressionNode extends ExpressionNode{
    
    private ExpressionNode expr;
    private List<ExpressionNode> valueList;
    private boolean not;

    public InExpressionNode(
        ExpressionNode expr, 
        List<ExpressionNode> valueList, 
        boolean not
    ) {
        this.expr = expr;
        this.valueList = valueList;
        this.not = not;

        addChild(expr);
        for (ExpressionNode e : valueList) { addChild(e); }
    }

    public ExpressionNode getExpr() { return expr; }
    public List<ExpressionNode> getValueList() { return valueList; }
    public boolean isNot() { return not; }

    @Override
    public String getNodeType() { return not ? "NOT_IN" : "IN"; }
}
