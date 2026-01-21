package com.example.DDL.Alter;

public class RenameColumnNode extends AlterTableActionNode {
    private final String oldName;
    private final String newName;

    public RenameColumnNode(String oldName, String newName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.oldName = oldName;
        this.newName = newName;
    }

    // public String getOldName(){
    // return oldName;
    // }
    // public String getNewName(){
    // return newName;
    // }
    @Override
    public String getNodeType() {
        return "RENAME_COLUMN (" + oldName + " -> " + newName + ")";
    }

}
