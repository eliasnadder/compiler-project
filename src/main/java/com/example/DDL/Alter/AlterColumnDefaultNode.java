package com.example.DDL.Alter;

public class AlterColumnDefaultNode extends AlterTableActionNode {
    private final String columnName;
    private final String defaultValue;

    public AlterColumnDefaultNode(int lineNumber, int columnNumber, String columnName, String defaultValue) {
        super(lineNumber, columnNumber);
        this.columnName = columnName;
        this.defaultValue = defaultValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getNodeType() {
        return "ALTER_COLUMN_DEFAULT";
    }

}
