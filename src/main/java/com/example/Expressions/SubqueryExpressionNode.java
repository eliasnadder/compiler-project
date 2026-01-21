package com.example.Expressions;

import com.example.ASTNode;

/**
 * Node لتمثيل الاستعلامات الفرعية (Subqueries) داخل Expressions
 */
public class SubqueryExpressionNode extends ExpressionNode {

    private ASTNode subquery;

    public SubqueryExpressionNode(ASTNode subquery) {
        this.subquery = subquery;
        if (subquery != null) {
            addChild(subquery);
        }
    }

    public ASTNode getSubquery() {
        return subquery;
    }

    @Override
    public String getNodeType() {
        return "SUBQUERY";
    }
}