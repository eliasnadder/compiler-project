package com.example.DDL.Alter;

import java.util.ArrayList;
import java.util.List;

public class AlterTableNode extends AlterStatementNode {
    private final String tablename;
    private final List<AlterTableActionNode> actions;
    public AlterTableNode(String tablename, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.tablename = tablename;
        this.actions = new ArrayList<>();
    }
    public void addAction(AlterTableActionNode action) {
        if (action != null) {
            actions.add(action);
        }
    }
    public String getTablename() {
        return tablename;
    }
    public List<AlterTableActionNode> getActions() {
        return actions;
    }
    @Override
    public String getNodeType() {
        return "ALTER_TABLE " + tablename + " ACTIONS=" + actions;
    }


}
