package com.example.DDL.Alter;

public class DropConstraintNode extends AlterTableActionNode {
    private final String constraintName;

    public DropConstraintNode(String constraintName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.constraintName = constraintName;

    }

    public String getConstraintName() {
        return constraintName;
    }

    @Override
    public String getNodeType() {
        return "DROP_CONSTRAINT (" + constraintName + ")";
    }

}
