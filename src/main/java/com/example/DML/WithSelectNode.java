package com.example.DML;

public class WithSelectNode extends DMLStatementsNode {

    private WithNode withNode;
    private SelectNode selectNode;

    public WithSelectNode(WithNode withNode, SelectNode selectNode) {
        this.withNode = withNode;
        this.selectNode = selectNode;

        addChild(withNode);
        addChild(selectNode);
    }

    @Override
    public String getNodeType() { return "WITH_SELECT"; }

    public WithNode getWithNode() { return withNode; }
    public SelectNode getSelectNode() { return selectNode; }
}
