package com.example.DDL.Alter;

public class RenameTableNode extends AlterTableActionNode {
    private final String newName;

    public RenameTableNode(String newName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.newName = newName;
    }

    @Override
    public String getNodeType() {
        return "RENAME_TABLE TO " + newName;
    }

}
