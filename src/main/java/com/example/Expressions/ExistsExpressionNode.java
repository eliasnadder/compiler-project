package com.example.Expressions;

import com.example.*;

public class ExistsExpressionNode extends ExpressionNode {

    private ASTNode subquery;
    private boolean not;

    public ExistsExpressionNode(
            ASTNode subquery,
            boolean not) {
        this.subquery = subquery;
        this.not = not;

        addChild(subquery);
    }

    public ASTNode getSubquery() {
        return subquery;
    }

    public boolean isNot() {
        return not;
    }

    @Override
    public String getNodeType() {
        return not ? "NOT_EXISTS" : "EXISTS";
    }
}
