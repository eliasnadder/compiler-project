package com.example.DML;

public class TableReferenceNode extends DMLStatementsNode {
    
    private String tableName;
    private String alias;
    
    public TableReferenceNode(String tableName) {
        this.tableName = tableName;
        this.alias = null;
    }
    
    public TableReferenceNode(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }
    
    public String getTableName() { return tableName; }
    public String getAlias() { return alias; }
    
    @Override
    public String getNodeType() {
        return alias != null ? 
               "TABLE_REFERENCE(" + tableName + " AS " + alias + ")" : 
               "TABLE_REFERENCE(" + tableName + ")";
    }
}