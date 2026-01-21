package com.example.DDL.Alter;

public class AddIndexNode extends AlterTableActionNode {
    private final String indexName;

    public AddIndexNode(String indexName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }

    @Override
    public String getNodeType() {
        return "ADD_INDEX " + indexName;
    }

}
