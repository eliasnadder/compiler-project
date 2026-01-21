package com.example.DDL.Create;
import com.example.ASTNode;
public abstract class ColumnConstraintNode extends ASTNode {
    public ColumnConstraintNode(int lineNumber , int columnNumber){
        super(lineNumber, columnNumber);
    }
    @Override
    public abstract String getNodeType();


}
