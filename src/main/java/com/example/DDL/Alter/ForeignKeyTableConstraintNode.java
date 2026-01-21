package com.example.DDL.Alter;

import java.util.ArrayList;
import java.util.List;

public class ForeignKeyTableConstraintNode extends TableConstraintNode {
    private final List<String> localColumns = new ArrayList<>();
    private final String referencedTable;
    private final List<String> referencedColumns = new ArrayList<>();

    public ForeignKeyTableConstraintNode(String referencedTable, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.referencedTable = referencedTable;
    }

    public void addLocalColumn(String columnName) {
        if (columnName != null) {
            localColumns.add(columnName);
        }
    }

    public void addReferencedColumn(String columnName) {
        if (columnName != null) {
            referencedColumns.add(columnName);
        }
    }

    public String getReferencedTableName() {
        return referencedTable;
    }

    public List<String> getLocalColumns() {
        return localColumns;
    }

    public List<String> getReferencedColumns() {
        return referencedColumns;
    }

    @Override
    public String getNodeType() {
        return "FOREIGN_KEY_CONSTRAINT";
    }

}
