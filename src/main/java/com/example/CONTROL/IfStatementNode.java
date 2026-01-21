package com.example.CONTROL;

import com.example.Expressions.ExpressionNode;

public class IfStatementNode extends ControlFlowStatementNode {
    private final ExpressionNode condition;
    private final BlockNode thenBlock;
    private final BlockNode elseBlock;

    public IfStatementNode(ExpressionNode condition, BlockNode thenBlock, BlockNode elseBlock, int lineNumber,
            int columnNumber) {
        super(lineNumber, columnNumber);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
        addChild(condition);
        addChild(thenBlock);
        if (elseBlock != null) {
            addChild(elseBlock);
        }
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockNode getThenBlock() {
        return thenBlock;
    }

    public BlockNode getElseBlock() {
        return elseBlock;
    }

    @Override
    public String getNodeType() {
        return "IfStatementNode";
    }

}
