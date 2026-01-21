package com.example.DDL.Drop;

public class DropDatabaseNode extends DropStatementNode {
    private final String databaseName;
    private final boolean ifExists;

    public DropDatabaseNode(String databaseName, int lineNumber, int columnNumber,boolean ifExists) {
        super(lineNumber, columnNumber);
        this.databaseName = databaseName;
        this.ifExists = ifExists;
    }

    public String getDatabaseName() {
        return databaseName;
    }
    public boolean isIfExists() {
        return ifExists;
    }
    @Override
    public String getNodeType() {
         return "DROP_DATABASE (" + databaseName + (ifExists ? ", IF_EXISTS" : "") + ")";
    }

}
