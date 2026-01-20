package com.example.DML;

import com.example.Expressions.ExpressionNode;
import java.util.Map;

public class UpdateNode extends DMLStatementsNode {

    private String tableName;
    private Map<String, ExpressionNode> assignments;
    private ExpressionNode whereClause;

    public UpdateNode(
            String tableName,
            Map<String, ExpressionNode> assignments,
            ExpressionNode whereClause) {
        this.tableName = tableName;
        this.assignments = assignments;
        this.whereClause = whereClause;

        assignments.values().forEach(this::addChild);
        if (whereClause != null)
            addChild(whereClause);
    }

    @Override
    public String getNodeType() {
        return "UPDATE";
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ExpressionNode> getAssignments() {
        return assignments;
    }

    public ExpressionNode getWhereClause() {
        return whereClause;
    }
}
