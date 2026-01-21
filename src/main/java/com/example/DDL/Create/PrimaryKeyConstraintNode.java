package com.example.DDL.Create;

public class PrimaryKeyConstraintNode extends ColumnConstraintNode {
    public PrimaryKeyConstraintNode(int lineNumber , int columnNumber){
        super(lineNumber, columnNumber);
    }
    @Override
    public String getNodeType() {
        return "PRIMARY_KEY";
    }

}
