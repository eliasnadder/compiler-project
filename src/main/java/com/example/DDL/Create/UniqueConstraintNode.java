package com.example.DDL.Create;

public class UniqueConstraintNode extends ColumnConstraintNode {
    public UniqueConstraintNode(int lineNumber , int columnNumber){
        super(lineNumber, columnNumber);
    }
    @Override
    public String getNodeType() {
        return "UNIQUE";
    }

}
