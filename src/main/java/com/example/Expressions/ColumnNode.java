package com.example.Expressions;

public class ColumnNode extends ExpressionNode{
    
    private String columnName;

    public ColumnNode(String columnName) { this.columnName = columnName; }

    public String getColumnName() { return columnName; }

    @Override
    public String getNodeType() { return "COLUMN(" + columnName + ")"; }
}
