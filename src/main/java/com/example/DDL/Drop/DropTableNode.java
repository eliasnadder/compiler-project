package com.example.DDL.Drop;

import java.util.ArrayList;
import java.util.List;

public class DropTableNode extends DropStatementNode {
    private final List<String> tableNames = new ArrayList<>();
    private final boolean ifExists;

    public DropTableNode(int lineNumber, int columnNumber, boolean ifExists) {
        super(lineNumber, columnNumber);
        this.ifExists = ifExists;
    }

    public void addTableName(String tableName) {
        if (tableNames.size() < 2) {
            tableNames.add(tableName);

        }
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    @Override
    public String getNodeType() {
        return "DROP_TABLE (" + tableNames + (ifExists ? ", IF_EXISTS" : "") + ")";
    }

}
