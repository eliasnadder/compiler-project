package com.example.DDL.Alter;


public class AddConstraintNode extends AlterTableActionNode {
    private final String constraintName;
    private final TableConstraintNode constraint;
    public AddConstraintNode(String constraintName, TableConstraintNode constraint, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.constraintName = constraintName;
        this.constraint = constraint;
        addChild(constraint);
    }
    public String getConstraintName() {
        return constraintName;
    }
    public TableConstraintNode getConstraint() {
        return constraint;
    }
    @Override
    public String getNodeType() {
        return "ADD_CONSTRAINT (" + constraintName + " : " + constraint + ")";
    }

}
