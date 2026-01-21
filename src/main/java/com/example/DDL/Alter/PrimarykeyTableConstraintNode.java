package com.example.DDL.Alter;

import java.util.ArrayList;
import java.util.List;

public class PrimarykeyTableConstraintNode extends TableConstraintNode {
    private final List<String> columns = new ArrayList<>();

    public PrimarykeyTableConstraintNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    public void addColumn(String columnName) {
        if (columnName != null) {
            columns.add(columnName);
        }
    }

    @Override
    public String getNodeType() {
        return "PRIMARY_KEY_TABLE_CONSTRAINT";
    }

}
