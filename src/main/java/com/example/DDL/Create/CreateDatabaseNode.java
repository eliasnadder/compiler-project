package com.example.DDL.Create;

public class CreateDatabaseNode extends CreateStatementNode {
    private final String databaseName;
    private final boolean ifNotExists;

    public CreateDatabaseNode(String databaseName, int lineNumber, int columnNumber, boolean ifNotExists) {
        super(lineNumber, columnNumber);
        this.databaseName = databaseName;
        this.ifNotExists = ifNotExists;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    @Override
    public String getNodeType() {
        return "CREATE_DATABASE(" + databaseName + (ifNotExists ? ", IF_NOT_EXISTS" : "") + ")";
    }
}
