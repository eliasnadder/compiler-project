package com.example.DDL.Create;

public class CollateConstraintNode extends ColumnConstraintNode {
    private final String collation;

    public CollateConstraintNode(String collation,
            int lineNumber,
            int columnNumber) {
        super(lineNumber, columnNumber);
        this.collation = collation;
    }

    public String getCollation() {
        return collation;
    }

    @Override
    public String getNodeType() {
        return "COLLATE (" + collation + ")";
    }

}
