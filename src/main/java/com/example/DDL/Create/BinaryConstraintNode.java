package com.example.DDL.Create;

public class BinaryConstraintNode extends ColumnConstraintNode {
     public BinaryConstraintNode(int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
    }

    @Override
    public String getNodeType() {
        return "BINARY";
    }

}
