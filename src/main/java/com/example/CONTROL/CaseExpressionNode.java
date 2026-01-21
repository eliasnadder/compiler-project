package com.example.CONTROL;

import java.util.ArrayList;
import java.util.List;

import com.example.ASTNode;

public class CaseExpressionNode extends ControlFlowStatementNode {
    private final List<WhenClauseNode> whenClauses = new ArrayList<>();
    private ASTNode elseExpression;

    public CaseExpressionNode(int line, int col) {
        super(line, col);
    }

    public void addWhenClause(WhenClauseNode when) {
        whenClauses.add(when);
        addChild(when);
    }

    public void setElseExpression(ASTNode elseExpr) {
        this.elseExpression = elseExpr;
        addChild(elseExpr);
    }

    public List<WhenClauseNode> getWhenClauses() {
        return whenClauses;
    }

    public ASTNode getElseExpression() {
        return elseExpression;
    }

    @Override
    public String getNodeType() {
        return "CASE";
    }
}
