package com.example.DML;

import com.example.ASTNode;
import java.util.List;

public class WithNode extends ASTNode {

    private boolean recursive;
    private List<CTETable> cteTables; 

    public static class CTETable {
        private String name;
        private DMLStatementsNode subquery;

        public CTETable(String name, DMLStatementsNode subquery) {
            this.name = name;
            this.subquery = subquery;
        }

        public String getName() { return name; }
        public DMLStatementsNode getSubquery() { return subquery; }
    }

    public WithNode(boolean recursive, List<CTETable> cteTables) {
        this.recursive = recursive;
        this.cteTables = cteTables;

        cteTables.forEach(cte -> addChild(cte.getSubquery()));
    }

    public boolean isRecursive() { return recursive; }
    public List<CTETable> getCteTables() { return cteTables; }

    @Override
    public String getNodeType() {
        return recursive ? "WITH_RECURSIVE" : "WITH";
    }
}
