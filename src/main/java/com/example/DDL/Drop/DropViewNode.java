package com.example.DDL.Drop;

import java.util.ArrayList;
import java.util.List;

public class DropViewNode extends DropStatementNode {
    private final List<String> viewNames = new ArrayList<>();
    private final boolean ifExists;

    public DropViewNode(String viewName, int lineNumber, int columnNumber, boolean ifExists) {
        super(lineNumber, columnNumber);
        this.ifExists = ifExists;
    }

    public void addViewName(String viewName) {
        if (viewName != null) {
            viewNames.add(viewName);

        }
    }

    public List<String> getViewNames() {
        return viewNames;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    @Override
    public String getNodeType() {
        return "DROP_VIEW (" + viewNames + (ifExists ? ", IF_EXISTS" : "") + ")";
    }

}
