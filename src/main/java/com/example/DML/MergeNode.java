package com.example.DML;

import com.example.Expressions.ExpressionNode;
import java.util.List;

public class MergeNode extends DMLStatementsNode {

    private String tableName;
    private DMLStatementsNode usingSource;
    private ExpressionNode onCondition;
    private List<DMLStatementsNode> whenMatchedActions;
    private List<DMLStatementsNode> whenNotMatchedActions;

    public MergeNode (
        String tableName,
        DMLStatementsNode usingSource,
        ExpressionNode onCondition,
        List<DMLStatementsNode> whenMatchedActions,
        List<DMLStatementsNode> whenNotMatchedActions
    ) {
        this.tableName = tableName;
        this.usingSource = usingSource;
        this.onCondition = onCondition;
        this.whenMatchedActions = whenMatchedActions;
        this.whenNotMatchedActions = whenNotMatchedActions;

        addChild(usingSource);
        addChild(onCondition);
        whenMatchedActions.forEach(this::addChild);
        whenNotMatchedActions.forEach(this::addChild);
    }

    @Override
    public String getNodeType() { return "MERGE"; }

    public String getTableName() { return tableName; }
    public DMLStatementsNode getUsingSource() { return usingSource; }
    public ExpressionNode getCondition() { return onCondition; }
    public List<DMLStatementsNode> getWhenMatchedActions() { return whenMatchedActions; }
    public List<DMLStatementsNode> getWhenNotMatchedActions() { return whenNotMatchedActions; }
    
}
