package com.example.DML;

import com.example.Expressions.ExpressionNode;

public class DeleteNode extends DMLStatementsNode{
    
    private String tableName;
    private ExpressionNode whereClause;

    public DeleteNode(
        String tablename,
        ExpressionNode whereClause
    ) {
        this.tableName = tablename;
        this.whereClause = whereClause;

        if (whereClause != null ) addChild(whereClause);
    }

    @Override
    public String getNodeType() { return "DELETE"; }

    public String getTableName() { return tableName; }
    public ExpressionNode getWhereClause() { return whereClause; }
}
