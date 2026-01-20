package com.example.Expressions;


public class BetweenExpressionNode extends ExpressionNode{

    private ExpressionNode expr;
    private ExpressionNode start;
    private ExpressionNode end;
    private boolean not;

    public BetweenExpressionNode(
        ExpressionNode expr, 
        ExpressionNode start, 
        ExpressionNode end, 
        boolean not
    ) {
        this.expr = expr;
        this.start = start;
        this.end = end;
        this.not = not;

        addChild(expr);
        addChild(start);
        addChild(end);
    }
    
    public ExpressionNode getExpr() { return expr; }
    public ExpressionNode getStart() { return start; }
    public ExpressionNode getEnd() { return end; }
    public boolean isNot() { return not; }

    @Override
    public String getNodeType() { return not ? "NOT_BETWEEN" : "BETWEEN"; }
}
