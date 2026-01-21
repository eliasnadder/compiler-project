package com.example.CONTROL;

public class ContenueStatementNode extends ControlFlowStatementNode{
    public ContenueStatementNode(int line, int col) {
        super(line, col);
    }
    @Override
    public String getNodeType() {
        return "CONTINUE STATEMENT";
    }

}
