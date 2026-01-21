package com.example.DDL.Alter;

import java.util.ArrayList;
import java.util.List;

public class UniqueTableConstraintNode extends TableConstraintNode {
    private final List<String> columns = new ArrayList<>();
    public UniqueTableConstraintNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }
    public void addColumn(String columnName) {
        if (columnName != null) {
            columns.add(columnName);
        }
    }
    public List<String> getColumns() {
        return columns;
    }

    @Override
    public String getNodeType() {
        return "UNIQUE_TABLE_CONSTRAINT";
    }


}
