package com.example.CONTROL;

public class BreakStatementNode extends ControlFlowStatementNode {
    public BreakStatementNode(int line, int col) {
        super(line, col);
    }

    @Override
    public String getNodeType() {
        return "BREAK STATEMENT";
    }

}
