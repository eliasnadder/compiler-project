package com.example.DDL.Alter;

public class DropColumnNode extends AlterTableActionNode {
    private final String columnName;

    public DropColumnNode(String columnName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.columnName = columnName;

    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getNodeType() {
        return "DROP_COLUMN (" + columnName + ")";
    }

}
