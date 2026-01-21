package com.example;

import java.util.List;

public class ASTNodeList extends ASTNode {

    private List<ASTNode> nodes;

    public ASTNodeList(List<ASTNode> nodes) {
        this.nodes = nodes;
        if (nodes != null) {
            for (ASTNode node : nodes) {
                addChild(node);
            }
        }
    }

    public List<ASTNode> getNodes() {
        return nodes;
    }

    @Override
    public String getNodeType() {
        return "AST_NODE_LIST";
    }
}
