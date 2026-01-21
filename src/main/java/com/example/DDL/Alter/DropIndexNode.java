package com.example.DDL.Alter;

public class DropIndexNode extends AlterTableActionNode {
    private final String indexName;

    public DropIndexNode(String indexName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.indexName = indexName;
    }

    @Override
    public String getNodeType() {
        return "DROP_INDEX " + indexName;
    }

}
