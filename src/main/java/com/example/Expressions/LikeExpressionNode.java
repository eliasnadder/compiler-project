package com.example.Expressions;

public class LikeExpressionNode extends ExpressionNode {

    private ExpressionNode expr;
    private ExpressionNode pattern;
    private ExpressionNode escape;
    private boolean not;

    public LikeExpressionNode(
            ExpressionNode expr,
            ExpressionNode pattern,
            ExpressionNode escape,
            boolean not) {
        this.expr = expr;
        this.pattern = pattern;
        this.escape = escape;
        this.not = not;

        addChild(expr);
        addChild(pattern);
        if (escape != null)
            addChild(escape);
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public ExpressionNode getPattern() {
        return pattern;
    }

    public ExpressionNode getEscape() {
        return escape;
    }

    @Override
    public String getNodeType() {
        return not ? "NOT_LIKE" : "LIKE";
    }

}
