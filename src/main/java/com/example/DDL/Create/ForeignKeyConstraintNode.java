package com.example.DDL.Create;

public class ForeignKeyConstraintNode extends ColumnConstraintNode {
    private final String referencedTable;
    private final String referencedColumn;
    public ForeignKeyConstraintNode(String referencedTable,
                                    String referencedColumn,
                                    int lineNumber,
                                    int columnNumber) {
        super(lineNumber, columnNumber);
        this.referencedTable = referencedTable;
        this.referencedColumn = referencedColumn;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public String getReferencedColumn() {
        return referencedColumn;
    }

    @Override
    public String getNodeType() {
        return "FOREIGN_KEY (" + referencedTable + "." + referencedColumn + ")";
    }

}
