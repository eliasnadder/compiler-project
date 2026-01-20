package com.example.DML;

import com.example.ASTNode;

public class CTENode extends ASTNode {
    private String name;
    private SelectNode select;

    public CTENode(String name, SelectNode select) {
        this.name = name;
        this.select = select;
        addChild(select);
    }

    public String getName() {
        return name;
    }

    public SelectNode getSelect() {
        return select;
    }

    @Override
    public String getNodeType() {
        return "CTE";
    }
}
