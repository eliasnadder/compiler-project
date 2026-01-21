package com.example.DDL.Create;

import java.util.ArrayList;
import java.util.List;

public class CreateIndexNode extends CreateStatementNode {
    private final String indexName;
    private final String tableName;
    private final List<String> columnNames;

    public CreateIndexNode(String indexName, String tableName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnNames = new ArrayList<>();
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return tableName;
    }

    public void addColumnName(String columnName) {
        if (columnName != null) {
            columnNames.add(columnName);
        }
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    @Override
    public String getNodeType() {
        return "CREATE_INDEX (" + indexName + " ON " + tableName + ")";
    }
}
