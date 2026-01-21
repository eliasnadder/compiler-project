package com.example.DML;

import com.example.Expressions.ExpressionNode;
import java.util.List;

public class InsertNode extends DMLStatementsNode {

    private String tableName;
    private List<String> columns;
    private List<List<ExpressionNode>> values;

    public InsertNode(
            String tableName,
            List<String> columns,
            List<List<ExpressionNode>> values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
        for (List<ExpressionNode> row : values) {
            row.forEach(this::addChild);
        }
    }

    @Override
    public String getNodeType() {
        return "INSERT";
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<ExpressionNode>> getValues() {
        return values;
    }

}
