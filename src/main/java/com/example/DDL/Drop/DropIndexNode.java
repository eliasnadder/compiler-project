package com.example.DDL.Drop;

public class DropIndexNode extends DropStatementNode {
    private final String indexName;
    private final String tableName;

    public DropIndexNode(String indexName, String tableName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.indexName = indexName;
        this.tableName = tableName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String getNodeType() {
        return "DROP_INDEX (" + indexName + " ON " + tableName + ")";
    }
}
