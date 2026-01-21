package com.example.DDL.Alter;

import com.example.DDL.Create.ColumnDefinitionNode;

public class ModifyColumnNode extends AlterTableActionNode {
    private final ColumnDefinitionNode column;

    public ModifyColumnNode(ColumnDefinitionNode column, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.column = column;
        addChild(column);
    }

    public ColumnDefinitionNode getColumn() {
        return column;
    }

    @Override
    public String getNodeType() {
        return "MODIFY_COLUMN";
    }

}
