package com.example.DDL.Alter;

import com.example.DDL.Create.ColumnDefinitionNode;

public class ChangeColumnNode extends AlterTableActionNode {
    private final String oldName;
    private final ColumnDefinitionNode newColumn;
    public ChangeColumnNode(String oldName, ColumnDefinitionNode newColumn, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.oldName = oldName;
        this.newColumn = newColumn;
        addChild(newColumn);
    }
    public String getOldName() {
        return oldName;
    }
    public ColumnDefinitionNode getNewColumn() {
        return newColumn;
    }
    @Override
    public String getNodeType() {
        return "CHANGE_COLUMN (" + oldName + " -> " + newColumn + ")";
    }
    

}
