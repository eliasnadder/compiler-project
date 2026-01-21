package com.example.DDL.Create;

import com.example.ASTNode;

public class ColumnDefinitionNode extends ASTNode {
    private final String columnName;
    private final String dataType;

    public ColumnDefinitionNode(String columnName, String dataType, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.columnName = columnName;
        this.dataType = dataType;

    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void addConstraint(ColumnConstraintNode constraint) {
        addChild(constraint);
    }

    @Override
    public String getNodeType() {
        return "COLUMN_DEFINITION (" + columnName + " " + dataType + ")";
    }

}
