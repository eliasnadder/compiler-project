package com.example.DDL.Create;
import com.example.ASTNode;
import java.util.ArrayList;
import java.util.List;

public class CreateTableNode extends CreateStatementNode {
    private final String tableName;
    private final boolean ifNotExists;
    private final List<ColumnDefinitionNode> columns;


    public CreateTableNode(String tableName, int lineNumber , int columnNumber,boolean ifNotExists) {
        super(lineNumber, columnNumber);
        this.tableName = tableName;
        this.ifNotExists=ifNotExists;
        this.columns = new ArrayList<>();


    }
    public String getTableName(){
        return tableName;
    }
    public boolean isIfNotExists() {
        return ifNotExists;
    }
    public void addColumn(ColumnDefinitionNode column) {
        if (column != null) {
            columns.add(column);
            addChild(column); 
        }
    }

    public List<ColumnDefinitionNode> getColumns() {
        return columns;
    }

    @Override
    public String getNodeType() {
        return "CREATE_TABLE (" + tableName +
               (ifNotExists ? ", IF_NOT_EXISTS" : "") + ")";
    }

}
